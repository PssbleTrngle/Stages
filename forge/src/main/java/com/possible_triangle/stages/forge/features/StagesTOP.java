package com.possible_triangle.stages.forge.features;

import java.util.function.Function;

import com.possible_triangle.stages.features.StagesDisguises;

import mcjty.theoneprobe.api.IBlockDisplayOverride;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoProvider;
import mcjty.theoneprobe.config.Config;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class StagesTOP implements Function<ITheOneProbe, Void>, IBlockDisplayOverride {

    @Override
    public Void apply(ITheOneProbe probe) {
        probe.registerBlockDisplayOverride(this);
        return null;
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData hitData) {
        var disguise = StagesDisguises.getDisguise(state.getBlock(), player);

        disguise.ifPresent(block -> {
            DefaultProbeInfoProvider.showStandardBlockInfo(Config.getRealConfig(), mode, info, block.defaultBlockState(), block, level, hitData.getPos(), player, hitData);
        });

        return disguise.isPresent();
    }

}
