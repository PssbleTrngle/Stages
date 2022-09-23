package com.possible_triangle.kubejs_stages.stage;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public record StageContext(@Nullable MinecraftServer server, @Nullable ServerPlayer player) {
}
