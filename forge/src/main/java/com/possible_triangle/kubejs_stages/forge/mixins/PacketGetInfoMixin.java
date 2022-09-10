package com.possible_triangle.kubejs_stages.forge.mixins;

import com.possible_triangle.kubejs_stages.features.StagesDisguises;
import mcjty.theoneprobe.network.PacketGetInfo;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = PacketGetInfo.class, remap = false)
public class PacketGetInfoMixin {

    @ModifyVariable(
            method = "getProbeInfo",
            at = @At("STORE"),
            name = "state"
    )
    private static BlockState disguiseBlock(BlockState current) {
        return StagesDisguises.getDisguise(current.getBlock())
                .map(Block::defaultBlockState)
                .orElse(current);
    }

    @ModifyVariable(
            method = "getProbeInfo",
            at = @At("HEAD"),
            ordinal = 0
    )
    private static ItemStack disguiseBlock(ItemStack current) {
        var item = current.getItem();
        if (item instanceof BlockItem blockItem) {
            return StagesDisguises.getDisguise(blockItem.getBlock())
                    .map(ItemStack::new)
                    .orElse(current);
        } else {
            return current;
        }
    }

}
