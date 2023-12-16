package com.possible_triangle.kubejs_stages.fabric.platform;

import com.possible_triangle.kubejs_stages.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

public class FabricPlatformHelper implements IPlatformHelper {

    public CompoundTag getPersistentData(Player player) {
        // TODO
        return new CompoundTag();
    }

    @Override
    public Ingredient subtract(Ingredient ingredient, Ingredient excluded) {
        return DefaultCustomIngredients.difference(ingredient, excluded);
    }

}
