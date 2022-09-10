package com.possible_triangle.kubejs_stages;

import com.possible_triangle.kubejs_stages.stage.StageBuilder;
import com.possible_triangle.kubejs_stages.stage.Stages;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraft.server.MinecraftServer;

import java.util.function.Supplier;

public class KubeJSStagesPlugin extends KubeJSPlugin {

    @Override
    public void init() {
        KubeJSStages.LOGGER.info("Loaded Stages Plugin");
    }

    @Override
    public void addBindings(BindingsEvent event) {
        if (event.type.isClient()) return;
        Supplier<MinecraftServer> server = KubeJSStages::getServer;

        event.addFunction("addStage", args -> {

            var id = args[0].toString();
            var consumer = (StageBuilder.Consumer) args[1];
            var stage = StageBuilder.create(consumer);
            Stages.registerStage(id, stage);
            return null;
        }, null, StageBuilder.Consumer.class);

        event.addFunction("isStageEnabled", args -> {
            var id = args[0].toString();
            return !Stages.isDisabled(server.get(), id);
        }, null);

        event.addFunction("isStageDisabled", args -> {
            var id = args[0].toString();
            return Stages.isDisabled(server.get(), id);
        }, null);

    }

}