package com.possible_triangle.kubejs_stages.mixins;

import com.possible_triangle.kubejs_stages.features.StagesDisguises;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {

    @Shadow
    @Final
    private Map<BlockState, BakedModel> modelByStateCache;

    @Inject(at = @At("HEAD"), method = "getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;", cancellable = true)
    private void overrideModel(BlockState blockState, CallbackInfoReturnable<BakedModel> cir) {
        StagesDisguises.getDisguise(blockState.getBlock())
                .map(Block::defaultBlockState)
                .map(modelByStateCache::get)
                .ifPresent(cir::setReturnValue);
    }

}
