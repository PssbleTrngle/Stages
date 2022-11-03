package com.possible_triangle.kubejs_stages.stage;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

public record StageContext(@Nullable MinecraftServer server, @Nullable Player player) {

    public static final StageContext EMPTY = new StageContext(null, null);

}
