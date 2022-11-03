package com.possible_triangle.kubejs_stages.features;

import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.platform.PlatformJEI;
import com.possible_triangle.kubejs_stages.stage.Stage;
import com.possible_triangle.kubejs_stages.stage.Stages;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;

@JeiPlugin
public class StagesJEI implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(KubeJSStages.ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    private static <T> void hideRecipesFor(IRecipeCategory<T> category, Stage disabled, IJeiRuntime runtime) {
        var recipes = runtime.getRecipeManager().createRecipeLookup(category.getRecipeType()).get();
        var hidden = recipes.filter(it -> {
            if (it instanceof Recipe<?> recipe) return disabled.recipes().contains(recipe.getId());
            return false;
        });
        runtime.getRecipeManager().hideRecipes(category.getRecipeType(), hidden.toList());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        var ingredients = runtime.getIngredientManager();
        Stages.getAccess().onChangeOnce("jei", access -> {
            var stage = access.getDisabledContent(Stages.clientContext());
            var categories = runtime.getRecipeManager().createRecipeCategoryLookup().get();

            categories.forEach(it -> hideRecipesFor(it, stage, runtime));

            if (!stage.stacks().isEmpty()) {
                ingredients.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, stage.stacks());
            }

            stage.categories().forEach(id -> {
                runtime.getRecipeManager().hideRecipeCategory(new ResourceLocation(id));
            });

            var buckets = stage.fluids().stream()
                    .map(FluidStackJS::getFluid)
                    .map(Fluid::getBucket)
                    .map(ItemStack::new)
                    .filter(it -> !it.isEmpty())
                    .toList();

            if (!buckets.isEmpty()) {
                runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, buckets);
            }

            PlatformJEI.handleStage(stage, runtime);
        });
    }

}
