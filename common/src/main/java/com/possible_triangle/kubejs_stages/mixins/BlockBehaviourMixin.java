package com.possible_triangle.kubejs_stages.mixins;

import com.possible_triangle.kubejs_stages.features.StagesDisguises;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(at = @At("HEAD"), method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootContext$Builder;)Ljava/util/List;", cancellable = true)
    private void replaceLoot(BlockState blockState, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        var disguise = StagesDisguises.getDisguise(blockState.getBlock());
        disguise.ifPresent(as -> {
            var drops = as.getDrops(as.defaultBlockState(), builder);
            cir.setReturnValue(drops);
        });
    }

}
