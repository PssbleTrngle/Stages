package com.possible_triangle.kubejs_stages.platform.services;

import com.possible_triangle.kubejs_stages.network.SyncMessage;
import net.minecraft.server.level.ServerPlayer;

public interface INetwork {

    void send(ServerPlayer player, SyncMessage message);

}
