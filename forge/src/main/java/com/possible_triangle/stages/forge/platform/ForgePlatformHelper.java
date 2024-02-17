package com.possible_triangle.stages.forge.platform;

import com.possible_triangle.stages.platform.services.IPlatformHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public CompoundTag getPersistentData(Player player) {
        return player.getPersistentData();
    }

    @Override
    public Ingredient subtract(Ingredient ingredient, Ingredient excluded) {
        return DifferenceIngredient.of(ingredient, excluded);
    }

}
