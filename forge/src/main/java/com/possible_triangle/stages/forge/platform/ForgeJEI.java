package com.possible_triangle.stages.forge.platform;

import com.possible_triangle.stages.CommonClass;
import com.possible_triangle.stages.forge.ForgeEntrypoint;
import com.possible_triangle.stages.platform.FluidStack;
import com.possible_triangle.stages.Stage;
import com.possible_triangle.stages.StageContext;
import com.possible_triangle.stages.Stages;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@JeiPlugin
public class ForgeJEI implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(CommonClass.ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    private static <T> Runnable hideRecipesFor(IRecipeCategory<T> category, Stage disabled, IJeiRuntime runtime) {
        Stream<T> byId = runtime.getRecipeManager().createRecipeLookup(category.getRecipeType()).get().filter(it -> {
            if (it instanceof Recipe<?> recipe) return disabled.recipes().contains(recipe.getId());
            return false;
        });

        Stream<T> byOutput = disabled.stacks().stream().flatMap(it -> {
            var focus = runtime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, it);
            var lookup = runtime.getRecipeManager().createRecipeLookup(category.getRecipeType()).limitFocus(Collections.singleton(focus));
            return lookup.get();
        });

        final var hidden = Stream.of(byId, byOutput).flatMap(Function.identity()).distinct().toList();
        if (hidden.isEmpty()) return null;

        CommonClass.LOGGER.debug("Hiding {} recipes", hidden.size());
        runtime.getRecipeManager().hideRecipes(category.getRecipeType(), hidden);

        return () -> {
            CommonClass.LOGGER.debug("Unhiding {} recipes", hidden.size());
            runtime.getRecipeManager().unhideRecipes(category.getRecipeType(), hidden);
        };
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        var availableItems = runtime.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK).stream().map(ItemStack::getItem).toList();
        var availableFluids = runtime.getIngredientManager().getAllIngredients(ForgeTypes.FLUID_STACK).stream().map(net.minecraftforge.fluids.FluidStack::getFluid).toList();

        Stages.requireAccess().onChange("jei", access -> {
            var disabled = access.getDisabledContent(StageContext.EMPTY);
            var categories = runtime.getRecipeManager().createRecipeCategoryLookup().get();

            var recipeCleanups = categories
                    .map(it -> hideRecipesFor(it, disabled, runtime))
                    .filter(Objects::nonNull)
                    .toList();

            runtime.getRecipeManager().createRecipeCategoryLookup().get()
                    .map(IRecipeCategory::getRecipeType)
                    .filter(it -> disabled.categories().contains(it.getUid().toString()))
                    .forEach(it -> runtime.getRecipeManager().hideRecipeCategory(it));

            var buckets = disabled.fluids().stream()
                    .map(FluidStack::fluid)
                    .filter(availableFluids::contains)
                    .map(Fluid::getBucket)
                    .map(ItemStack::new)
                    .filter(it -> !it.isEmpty())
                    .toList();

            var hiddenStacks = Stream.of(disabled.stacks(), buckets).flatMap(Collection::stream)
                    .filter(it -> availableItems.contains(it.getItem()))
                    .toList();

            if (!hiddenStacks.isEmpty()) {
                runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenStacks);
            }

            var fluids = disabled.fluids().stream()
                    .filter(it -> availableFluids.contains(it.fluid()))
                    .map(ForgeEntrypoint::mapFluidStack)
                    .toList();

            if (!fluids.isEmpty()) {
                runtime.getIngredientManager().removeIngredientsAtRuntime(ForgeTypes.FLUID_STACK, fluids);
            }

            return () -> {
                recipeCleanups.forEach(it -> it.run());

                disabled.categories().forEach(id -> {
                    runtime.getRecipeManager().createRecipeCategoryLookup().includeHidden().get()
                            .map(IRecipeCategory::getRecipeType)
                            .filter(it -> disabled.categories().contains(it.getUid().toString()))
                            .forEach(it -> runtime.getRecipeManager().unhideRecipeCategory(it));
                });

                if (!hiddenStacks.isEmpty()) {
                    runtime.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenStacks);
                }

                if (!fluids.isEmpty()) {
                    runtime.getIngredientManager().addIngredientsAtRuntime(ForgeTypes.FLUID_STACK, fluids);
                }
            };
        });
    }

}
