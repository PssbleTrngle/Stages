package com.possible_triangle.kubejs_stages.mixins.forge;

import com.possible_triangle.kubejs_stages.features.StagesDisguises;
import mcjty.theoneprobe.network.PacketGetInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PacketGetInfo.class, remap = false)
public class PacketGetInfoMixin {

    @Redirect(
            require = 0,
            method = "Lmcjty/theoneprobe/network/PacketGetInfo;getProbeInfo(Lnet/minecraft/world/entity/player/Player;Lmcjty/theoneprobe/api/ProbeMode;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/item/ItemStack;)Lmcjty/theoneprobe/apiimpl/ProbeInfo;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState getBlockStateProxy(Level world, BlockPos pos) {
        var state = world.getBlockState(pos);
        return StagesDisguises.getDisguise(state.getBlock())
                .map(Block::defaultBlockState)
                .orElse(state);
    }

}
