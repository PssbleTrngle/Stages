package com.possible_triangle.kubejs_stages.platform.services;

import com.possible_triangle.kubejs_stages.stage.Stage;
import mezz.jei.api.runtime.IJeiRuntime;

public interface IPlatformJEI {

    void handleStage(Stage stage, IJeiRuntime runtime);

}
