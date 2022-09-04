package com.possible_triangle.kubejs_stages.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Function;

public class PlatformEvents {

    @ExpectPlatform
    public static void modifyBreakSpeed(Function<BlockState, Optional<Float>> modifier) {
        throw new AssertionError();
    }

}
