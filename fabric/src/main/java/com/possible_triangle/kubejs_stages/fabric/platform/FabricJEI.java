package com.possible_triangle.kubejs_stages.fabric.platform;

import com.possible_triangle.kubejs_stages.platform.services.IPlatformJEI;
import com.possible_triangle.kubejs_stages.stage.Stage;

import mezz.jei.api.runtime.IJeiRuntime;

public class FabricJEI implements IPlatformJEI {

    @Override
    public void handleStage(Stage stage, IJeiRuntime runtime) {
        // Fluid stuff is weird on fabric
    }

}
