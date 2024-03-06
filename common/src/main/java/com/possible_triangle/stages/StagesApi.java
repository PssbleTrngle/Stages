package com.possible_triangle.stages;

import net.minecraft.resources.ResourceLocation;

public class StagesApi {

    public static void addStage(ResourceLocation id, StageBuilder.Consumer consumer) {
        Stages.getServerAccess().ifPresent(registration -> {
            var stage = StageBuilder.create(consumer);
            registration.registerStage(id, stage);
        });
    }

    public static boolean isStageEnabled(ResourceLocation id) {
        return Stages.requireAccess().isEnabled(id, StageContext.EMPTY);
    }

    public static boolean isStageDisabled(ResourceLocation id) {
        return Stages.requireAccess().isEnabled(id, StageContext.EMPTY);
    }

}
