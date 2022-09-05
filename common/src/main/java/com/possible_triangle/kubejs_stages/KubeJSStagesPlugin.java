package com.possible_triangle.kubejs_stages;

import com.possible_triangle.kubejs_stages.stage.StageBuilder;
import com.possible_triangle.kubejs_stages.stage.Stages;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

public class KubeJSStagesPlugin extends KubeJSPlugin {
    @Override
    public void init() {
        KubeJSStages.LOGGER.info("Loaded Stages Plugin");
    }

    @Override
    public void addBindings(BindingsEvent event) {

        event.addFunction("addStage", args -> {
            if (event.type != ScriptType.SERVER) return null;

            var id = args[0].toString();
            var consumer = (StageBuilder.Consumer) args[1];
            var stage = StageBuilder.create(consumer);
            Stages.registerStage(id, stage);
            return null;
        }, null, StageBuilder.Consumer.class);

        event.addFunction("isStageEnabled", args -> {
            if (event.type != ScriptType.SERVER) return null;
            var id = args[0].toString();
            return !Stages.isDisabled(id);
        }, null);

        event.addFunction("isStageDisabled", args -> {
            if (event.type != ScriptType.SERVER) return null;
            var id = args[0].toString();
            return Stages.isDisabled(id);
        }, null);

    }

}