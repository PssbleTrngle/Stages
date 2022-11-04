package com.possible_triangle.kubejs_stages.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlatformHelper {

    @ExpectPlatform
    public static CompoundTag getPersistentData(Player player) {
        throw new AssertionError();
    }

}
