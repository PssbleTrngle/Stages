package com.possible_triangle.kubejs_stages.platform.forge;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Optional;
import java.util.function.Function;

public class PlatformEventsImpl {

    public static void modifyBreakSpeed(Function<BlockState, Optional<Float>> modifier) {
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.BreakSpeed event)  -> {
            modifier.apply(event.getState()).ifPresent(factor -> {
                event.setNewSpeed(factor * event.getOriginalSpeed());
            });
        });
    }

}
