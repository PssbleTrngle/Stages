package com.possible_triangle.kubejs_stages;

import com.possible_triangle.kubejs_stages.stage.StageBuilder;
import com.possible_triangle.kubejs_stages.stage.StageContext;
import com.possible_triangle.kubejs_stages.stage.Stages;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class KubeJSStagesPlugin extends KubeJSPlugin {

    @Override
    public void init() {
        KubeJSStages.LOGGER.info("Loaded Stages Plugin");
    }

    @Override
    public void addBindings(BindingsEvent event) {

        if (event.type.isServer()) event.addFunction("addStage", args -> {
            Stages.getServerAccess().ifPresent(registration -> {
                var id = args[0].toString();
                var consumer = (StageBuilder.Consumer) args[1];
                var stage = StageBuilder.create(consumer);
                registration.registerStage(id, stage);
            });
            return null;
        }, null, StageBuilder.Consumer.class);

        // TODO additional player argument?
        event.addFunction("isStageEnabled", args -> {
            var id = args[0].toString();
            return Stages.getAccess().isEnabled(id, StageContext.EMPTY);
        });

        event.addFunction("isStageDisabled", args -> {
            var id = args[0].toString();
            return Stages.getAccess().isDisabled(id, StageContext.EMPTY);
        });

    }

}
