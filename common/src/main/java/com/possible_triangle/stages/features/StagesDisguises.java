package com.possible_triangle.stages.features;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;
import com.possible_triangle.stages.StageContext;
import com.possible_triangle.stages.Stages;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public class StagesDisguises {

    private static final UUID GLOBAL = UUID.randomUUID();

    private static final HashMap<UUID, Map<Block, Block>> CACHE = Maps.newHashMap();

    public static void init() {
        Stages.requireAccess().onChange("disguises", access -> {
            synchronized (CACHE) {
                CACHE.clear();
            }
        });
    }

    public static Optional<Block> getDisguise(Block block, @Nullable Player player) {
        var uuid = player == null ? GLOBAL : player.getUUID();
        var access = Stages.getAccess();
        synchronized (CACHE) {
            var disguises = CACHE.computeIfAbsent(uuid, $ -> {
                var context = new StageContext(null, player, false);
                return access.map(it -> it.getDisabledContent(context).disguisedBlocks()).orElse(null);
            });
            return Optional.ofNullable(disguises).map(it -> it.get(block));
        }
    }

    public static Optional<Float> getBreakSpeed(Block block, float originalSpeed, @Nullable Player player) {
        return getDisguise(block, player).map(disguise -> {
            var factor = block.defaultDestroyTime() / disguise.defaultDestroyTime();
            return factor * originalSpeed;
        });
    }

}
