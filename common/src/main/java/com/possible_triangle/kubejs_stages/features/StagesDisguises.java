package com.possible_triangle.kubejs_stages.features;

import com.google.common.collect.ImmutableMap;
import com.possible_triangle.kubejs_stages.stage.Stages;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Optional;

public class StagesDisguises {

    private static Map<Block, Block> blocks;
    private static Map<Block, Float> breakSpeedFactors;

    public static void init() {
        Stages.onChange("disguises", stage -> {
            blocks = stage.disguisedBlocks();

            var disguises = new ImmutableMap.Builder<Block, Float>();
            blocks.forEach((block, disguise) -> {
                disguises.put(block, block.defaultDestroyTime() / disguise.defaultDestroyTime());
            });
            breakSpeedFactors = disguises.build();
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

    public static Optional<Float> getBreakSpeed(Block block, float originalSpeed) {
        if (breakSpeedFactors == null) return Optional.empty();
        return Optional.ofNullable(breakSpeedFactors.get(block)).map(it -> it * originalSpeed);
    }

}
