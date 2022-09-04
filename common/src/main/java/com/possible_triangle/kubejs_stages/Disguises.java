package com.possible_triangle.kubejs_stages;

import com.google.common.collect.ImmutableMap;
import com.possible_triangle.kubejs_stages.stage.Stages;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Optional;

public class Disguises {

    private static Map<Block, Block> blocks;

    public static void init() {
        Stages.onReceivedStages("disguises", stages -> {
            var disguises = new ImmutableMap.Builder<Block, Block>();
            stages.forEach(stage -> {
                disguises.putAll(stage.disguisedBlocks());

            });
            blocks = disguises.build();
        });

        /*
        PlatformEvents.modifyBreakSpeed(event -> {
            var block = event.getBlock();
            var stage = Stages.getDisabledStages().stream().filter(it -> it.disguisedBlocks().containsKey(block)).findFirst();
            return stage.map(it -> it.disguisedBlocks().get(block)).map(disguise -> {
                return block.defaultDestroyTime() / disguise.defaultDestroyTime();
            });
        });
        */
    }

    public static Optional<Block> getDisguise(Block block) {
        if (blocks == null) return Optional.empty();
        return Optional.ofNullable(blocks.get(block));
    }

}
