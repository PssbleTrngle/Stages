package com.possible_triangle.kubejs_stages;

import com.possible_triangle.kubejs_stages.stage.StageBuilder;
import com.possible_triangle.kubejs_stages.stage.StageContext;
import com.possible_triangle.kubejs_stages.stage.Stages;

public class StagesApi {

    public static void addStage(String id, StageBuilder.Consumer consumer) {
        Stages.getServerAccess().ifPresent(registration -> {
            var stage = StageBuilder.create(consumer);
            registration.registerStage(id, stage);
        });
    }

    public static boolean isStageEnabled(String id) {
        return Stages.requireAccess().isEnabled(id, StageContext.EMPTY);
    }

    public static boolean isStageDisabled(String id) {
        return Stages.requireAccess().isEnabled(id, StageContext.EMPTY);
    }

}
