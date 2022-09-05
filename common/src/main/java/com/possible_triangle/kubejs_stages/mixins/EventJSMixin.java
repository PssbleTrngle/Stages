package com.possible_triangle.kubejs_stages.mixins;

import com.possible_triangle.kubejs_stages.features.StagesRecipes;
import com.possible_triangle.kubejs_stages.stage.Stages;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EventJS.class, remap = false)
public class EventJSMixin {

    @Inject(method = "post(Ldev/latvian/mods/kubejs/script/ScriptType;Ljava/lang/String;)Z", at = @At("HEAD"))
    private void removeLockedRecipes(ScriptType type, String id, CallbackInfoReturnable<Boolean> cir) {
        var self = (EventJS) (Object) this;
        if(type != ScriptType.SERVER) return;
        if ("recipes".equals(id) && self instanceof RecipeEventJS recipes) {
            StagesRecipes.removeRecipes(recipes);
            Stages.finishLoad();
        }
    }

}
