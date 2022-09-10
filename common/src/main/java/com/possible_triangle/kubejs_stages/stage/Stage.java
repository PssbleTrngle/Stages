package com.possible_triangle.kubejs_stages.stage;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record Stage(Collection<IngredientJS> items, Collection<FluidStackJS> fluids, Collection<String> categories,
                    Map<Block, Block> disguisedBlocks, Collection<ResourceLocation> recipes) {

    public static final Stage EMPTY = new Stage(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());

    public List<ItemStack> stacks() {
        return items().stream().flatMap(it -> it.getStacks().stream()).map(ItemStackJS::getItemStack).toList();
    }

    public Stage merge(Stage other) {
        return StageBuilder.create(builder -> {
            this.items().forEach(builder::addItem);
            other.items().forEach(builder::addItem);

            this.fluids().forEach(builder::addFluid);
            other.fluids().forEach(builder::addFluid);

            this.categories().forEach(builder::addCategory);
            other.categories().forEach(builder::addCategory);

            this.disguisedBlocks().forEach(builder::disguiseBlock);
            other.disguisedBlocks().forEach(builder::disguiseBlock);

            this.recipes().forEach(builder::addRecipe);
            other.recipes().forEach(builder::addRecipe);
        }).build();
    }

    public String info() {
        return String.format("%s items, %s fluids, %s categories, %s disguised blocks, %s recipes", items().size(), fluids().size(), categories().size(), disguisedBlocks().size(), recipes.size());
    }
}
