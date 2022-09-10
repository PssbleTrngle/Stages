package com.possible_triangle.kubejs_stages;

import com.possible_triangle.kubejs_stages.command.StageCommand;
import com.possible_triangle.kubejs_stages.features.StagesDisguises;
import com.possible_triangle.kubejs_stages.network.StagesNetwork;
import com.possible_triangle.kubejs_stages.stage.Stages;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

public class KubeJSStages {

    public static final String ID = "kubejs_stages";
    public static final Logger LOGGER = LogManager.getLogger("KubeJS Stages");

    public static void init() {
        StagesNetwork.init();
        Stages.setup();
        StagesDisguises.init();

        CommandRegistrationEvent.EVENT.register(StageCommand::register);
    }

    public static @Nullable  MinecraftServer getServer() {
        if (ServerJS.instance == null) return null;
        return ServerJS.instance.getMinecraftServer();
    }
}