package com.possible_triangle.kubejs_stages.platform.forge;

import com.possible_triangle.kubejs_stages.stage.Stage;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.runtime.IJeiRuntime;

public class PlatformJEIImpl {

    public static void handleStage(Stage stage, IJeiRuntime runtime) {
        var fluids = stage.fluids().stream().map(FluidStackHooksForge::toForge).toList();
        runtime.getIngredientManager().removeIngredientsAtRuntime(ForgeTypes.FLUID_STACK, fluids);
    }

}
