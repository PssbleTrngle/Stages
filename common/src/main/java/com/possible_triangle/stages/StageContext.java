package com.possible_triangle.stages;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;

public record StageContext(@Nullable MinecraftServer server, @Nullable Player player, boolean strict) {

    public static final StageContext EMPTY = new StageContext(null, null, false);

}
