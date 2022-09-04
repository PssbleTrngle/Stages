package com.possible_triangle.kubejs_stages.platform.fabric;

import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Function;

public class PlatformEventsImpl {

    public static void modifyBreakSpeed(Function<BlockState, Optional<Float>> modifier) {
        // No event?
    }

}
