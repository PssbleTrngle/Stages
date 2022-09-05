package com.possible_triangle.kubejs_stages.features;

import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.platform.PlatformJEI;
import com.possible_triangle.kubejs_stages.stage.Stages;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class StagesJEI implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(KubeJSStages.ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        var ingredients = runtime.getIngredientManager();
        Stages.onChangeOnce("jei", stage -> {

            if (!stage.stacks().isEmpty()) {
                ingredients.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, stage.stacks());
            }

            stage.categories().forEach(id -> {
                runtime.getRecipeManager().hideRecipeCategory(new ResourceLocation(id));
            });

            PlatformJEI.handleStage(stage, runtime);

        });
    }

}
