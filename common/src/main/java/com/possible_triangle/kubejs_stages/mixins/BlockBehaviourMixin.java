package com.possible_triangle.kubejs_stages.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.possible_triangle.kubejs_stages.features.StagesDisguises;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(at = @At("HEAD"), method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootContext$Builder;)Ljava/util/List;", cancellable = true)
    private void replaceLoot(BlockState blockState, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        var entity = builder.create(LootContextParamSets.BLOCK).getParamOrNull(LootContextParams.THIS_ENTITY);
        var player = entity instanceof Player it ? it : null;
        var disguise = StagesDisguises.getDisguise(blockState.getBlock(), player);
        disguise.ifPresent(as -> {
            var drops = as.getDrops(as.defaultBlockState(), builder);
            cir.setReturnValue(drops);
        });
    }

}
