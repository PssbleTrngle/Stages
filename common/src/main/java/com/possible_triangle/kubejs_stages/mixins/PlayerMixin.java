package com.possible_triangle.kubejs_stages.mixins;

import com.possible_triangle.kubejs_stages.Disguises;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(at = @At("RETURN"), method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F", cancellable = true)
    private void modifyBreakSpeed(BlockState blockState, CallbackInfoReturnable<Float> cir) {
        var block = blockState.getBlock();
        Disguises.getDisguise(block)
                .map(disguise -> block.defaultDestroyTime() / disguise.defaultDestroyTime())
                .map(factor -> factor * cir.getReturnValue())
                .ifPresent(cir::setReturnValue);
    }


}
