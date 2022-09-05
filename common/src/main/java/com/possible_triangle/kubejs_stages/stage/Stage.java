package com.possible_triangle.kubejs_stages.stage;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record Stage(Collection<IngredientJS> items, Collection<FluidStackJS> fluids,
                    Map<Block, Block> disguisedBlocks) {

    public static final Stage EMPTY = new Stage(Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());

    public List<ItemStack> stacks() {
        return items().stream().flatMap(it -> it.getStacks().stream()).map(ItemStackJS::getItemStack).toList();
    }

    public Stage merge(Stage other) {
        return StageBuilder.create(builder -> {
            this.items().forEach(builder::addItem);
            other.items().forEach(builder::addItem);
            this.fluids().forEach(builder::addFluid);
            other.fluids().forEach(builder::addFluid);
            this.disguisedBlocks().forEach(builder::disguiseBlock);
            other.disguisedBlocks().forEach(builder::disguiseBlock);
        }).build();
    }

}
