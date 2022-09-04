package com.possible_triangle.kubejs_stages.stage;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;

public record Stage(String id, Collection<IngredientJS> items, Collection<FluidStackJS> fluids) {
    public List<ItemStack> stacks() {
        return items().stream()
                .flatMap(it -> it.getStacks().stream())
                .map(ItemStackJS::getItemStack)
                .toList();
    }

}
