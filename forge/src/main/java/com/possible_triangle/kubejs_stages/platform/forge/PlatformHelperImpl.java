package com.possible_triangle.kubejs_stages.platform.forge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Optional;
import java.util.function.Function;

public class PlatformHelperImpl {

    public static CompoundTag getPersistentData(Player player) {
        return player.getPersistentData();
    }

}
