package com.possible_triangle.kubejs_stages;

import com.possible_triangle.kubejs_stages.features.StagesDisguises;
import com.possible_triangle.kubejs_stages.network.StagesNetwork;
import com.possible_triangle.kubejs_stages.stage.Stages;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KubeJSStages {

    public static final String ID = "kubejs_stages";
    public static final Logger LOGGER = LogManager.getLogger("KubeJS Stages");

    public static void init() {
        StagesNetwork.init();
        Stages.setup();
        StagesDisguises.init();

        CommandRegistrationEvent.EVENT.register(StageCommand::register);
    }
}