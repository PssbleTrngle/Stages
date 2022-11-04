package com.possible_triangle.kubejs_stages.forge.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.possible_triangle.kubejs_stages.features.StagesDisguises;

import mcjty.theoneprobe.network.PacketGetInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = PacketGetInfo.class, remap = false)
public class PacketGetInfoMixin {

    @ModifyVariable(
            method = "getProbeInfo",
            at = @At("STORE"),
            name = "state"
    )
    private static BlockState disguiseBlock(BlockState current) {
        return StagesDisguises.getDisguise(current.getBlock(), Minecraft.getInstance().player)
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
            return StagesDisguises.getDisguise(blockItem.getBlock(), Minecraft.getInstance().player)
                    .map(ItemStack::new)
                    .orElse(current);
        } else {
            return current;
        }
    }

}
