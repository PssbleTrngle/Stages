package com.possible_triangle.kubejs_stages.platform;

import java.util.Optional;
import java.util.function.Function;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class PlatformHelper {

    @ExpectPlatform
    public static CompoundTag getPersistantData(Player player) {
        throw new AssertionError();
    }

}
