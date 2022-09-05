package com.possible_triangle.kubejs_stages.mixins;

import com.possible_triangle.kubejs_stages.features.StagesDisguises;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OreBlock.class)
public class OreBlockMixin {

    @Inject(at = @At("HEAD"), method = "spawnAfterBreak(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", cancellable = true)
    private void modifyXpDrops(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, CallbackInfo ci) {
        var disguise = StagesDisguises.getDisguise(blockState.getBlock());
        disguise.ifPresent(as -> {
            as.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack);
            ci.cancel();
        });
    }

}
