package com.possible_triangle.stages.network;

import com.possible_triangle.stages.platform.Services;
import com.possible_triangle.stages.Stages;
import net.minecraft.server.level.ServerPlayer;

public class StagesNetwork {

    public static void sync(ServerPlayer player) {
        Stages.getServerAccess().ifPresent(stages -> {
            var packet = stages.createSyncMessage(player);
            Services.NETWORK.send(player, packet);
        });
    }

}
