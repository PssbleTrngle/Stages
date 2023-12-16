package com.possible_triangle.kubejs_stages.forge.platform;

import com.possible_triangle.kubejs_stages.forge.KubeJSStagesForge;
import com.possible_triangle.kubejs_stages.platform.services.IPlatformJEI;
import com.possible_triangle.kubejs_stages.stage.Stage;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.runtime.IJeiRuntime;

public class ForgeJEI implements IPlatformJEI {

    @Override
    public void handleStage(Stage stage, IJeiRuntime runtime) {
        var fluids = stage.fluids().stream()
                .map(KubeJSStagesForge::mapFluidStack)
                .toList();

        if (!fluids.isEmpty()) {
            runtime.getIngredientManager().removeIngredientsAtRuntime(ForgeTypes.FLUID_STACK, fluids);
        }
    }

}
