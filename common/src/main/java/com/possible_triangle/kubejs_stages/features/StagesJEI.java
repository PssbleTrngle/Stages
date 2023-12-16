package com.possible_triangle.kubejs_stages.features;

import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.platform.FluidStack;
import com.possible_triangle.kubejs_stages.platform.Services;
import com.possible_triangle.kubejs_stages.platform.services.IPlatformJEI;
import com.possible_triangle.kubejs_stages.stage.Stage;
import com.possible_triangle.kubejs_stages.stage.Stages;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
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
public class StagesJEI implements IModPlugin {

    private static final IPlatformJEI PLATFORM = Services.load(IPlatformJEI.class);

    private static final ResourceLocation ID = new ResourceLocation(KubeJSStages.ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    private static <T> Runnable hideRecipesFor(IRecipeCategory<T> category, Stage disabled, IJeiRuntime runtime) {
        Stream<T> byId = runtime.getRecipeManager().createRecipeLookup(category.getRecipeType()).get().filter(it -> {
            if (it instanceof Recipe<?> recipe) return disabled.recipes().contains(recipe.getId());
            return false;
        });

        Stream<T> byInput = disabled.stacks().stream().flatMap(it -> {
            var focus = runtime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, it);
            var lookup = runtime.getRecipeManager().createRecipeLookup(category.getRecipeType()).limitFocus(Collections.singleton(focus));
            return lookup.get();
        });

        var hidden = Stream.of(byId, byInput).flatMap(Function.identity()).distinct().toList();
        if (hidden.isEmpty()) return null;

        runtime.getRecipeManager().hideRecipes(category.getRecipeType(), hidden);
        return () -> runtime.getRecipeManager().unhideRecipes(category.getRecipeType(), hidden);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        Stages.requireAccess().onChange("jei", access -> {
            var disabled = access.getDisabledContent(Stages.clientContext());
            var categories = runtime.getRecipeManager().createRecipeCategoryLookup().get();

            var recipeCleanups = categories.map(it -> hideRecipesFor(it, disabled, runtime)).filter(Objects::nonNull);

            runtime.getRecipeManager().createRecipeCategoryLookup().get()
                    .map(IRecipeCategory::getRecipeType)
                    .filter(it -> disabled.categories().contains(it.getUid().toString()))
                    .forEach(it -> runtime.getRecipeManager().hideRecipeCategory(it));

            var buckets = disabled.fluids().stream()
                    .map(FluidStack::fluid)
                    .map(Fluid::getBucket)
                    .map(ItemStack::new)
                    .filter(it -> !it.isEmpty())
                    .toList();

            var hiddenStacks = Stream.of(disabled.stacks(), buckets).flatMap(Collection::stream).toList();

            if (!hiddenStacks.isEmpty()) {
                runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenStacks);
            }

            PLATFORM.handleStage(disabled, runtime);

            return () -> {
                KubeJSStages.LOGGER.debug("JEI Cleanup");

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
            };
        });
    }

}
