package com.possible_triangle.kubejs_stages.features;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoProvider;
import mcjty.theoneprobe.config.Config;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class StagesTOP implements Function<ITheOneProbe, Void>, IBlockDisplayOverride {

    @Override
    public Void apply(ITheOneProbe probe) {
        probe.registerBlockDisplayOverride(this);
        return null;
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitData) {
        var disguise = StagesDisguises.getDisguise(state.getBlock());

        disguise.ifPresent(block -> {
            DefaultProbeInfoProvider.showStandardBlockInfo(Config.getRealConfig(), mode, info, block.defaultBlockState(), block, level, hitData.getPos(), player, hitData);
        });

        return disguise.isPresent();
    }

}
