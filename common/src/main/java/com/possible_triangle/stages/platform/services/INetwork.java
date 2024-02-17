package com.possible_triangle.stages.platform.services;

import com.possible_triangle.stages.network.SyncMessage;
import net.minecraft.server.level.ServerPlayer;

public interface INetwork {

    void send(ServerPlayer player, SyncMessage message);

}
