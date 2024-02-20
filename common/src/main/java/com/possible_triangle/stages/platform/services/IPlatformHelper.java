package com.possible_triangle.stages.platform.services;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

public interface IPlatformHelper {

    CompoundTag getPersistentData(Player player);

    Ingredient subtract(Ingredient ingredient, Ingredient excluded);

}
