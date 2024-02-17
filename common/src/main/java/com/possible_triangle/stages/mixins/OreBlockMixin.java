package com.possible_triangle.stages.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.possible_triangle.stages.features.StagesDisguises;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(DropExperienceBlock.class)
public class OreBlockMixin {

    @Inject(at = @At("HEAD"), method = "spawnAfterBreak(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V", cancellable = true)
    private void modifyXpDrops(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl, CallbackInfo ci) {
        var disguise = StagesDisguises.getDisguise(blockState.getBlock(), null);
        disguise.ifPresent(as -> {
            as.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
            ci.cancel();
        });
    }

}
