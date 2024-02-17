package com.possible_triangle.stages.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.possible_triangle.stages.StageScope;

import net.minecraft.server.level.ServerPlayer;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(at = @At("RETURN"), method = "restoreFrom")
    private void restoreStageData(ServerPlayer oldPlayer, boolean bl, CallbackInfo ci) {
        StageScope.restorePlayerData(oldPlayer, (ServerPlayer) (Object) (this));
    }


}
