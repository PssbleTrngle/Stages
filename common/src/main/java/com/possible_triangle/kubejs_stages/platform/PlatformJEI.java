package com.possible_triangle.kubejs_stages.platform;

import com.possible_triangle.kubejs_stages.stage.Stage;
import dev.architectury.injectables.annotations.ExpectPlatform;
import mezz.jei.api.runtime.IJeiRuntime;

public class PlatformJEI {

    @ExpectPlatform
    public static void handleStage(Stage stage, IJeiRuntime runtime) {
        throw new AssertionError();
    }

}
