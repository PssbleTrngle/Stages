package com.possible_triangle.stages;

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
