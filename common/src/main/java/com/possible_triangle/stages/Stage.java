package com.possible_triangle.stages;

import com.possible_triangle.stages.platform.FluidStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record Stage(ThreeState defaultState, Collection<Ingredient> items, Collection<FluidStack> fluids,
                    Collection<String> categories,
                    Map<Block, Block> disguisedBlocks, Collection<ResourceLocation> recipes,
                    Collection<ResourceLocation> parents) {

    public static final Stage EMPTY = new Stage(ThreeState.UNSET, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyList());

    public List<ItemStack> stacks() {
        return items().stream().flatMap(it -> Arrays.stream(it.getItems())).toList();
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

            this.parents().forEach(builder::requires);
            other.parents().forEach(builder::requires);

            if (this.defaultState != ThreeState.UNSET) builder.setDefaultState(this.defaultState);
            if (other.defaultState != ThreeState.UNSET) builder.setDefaultState(other.defaultState);
        }).build();
    }

    public String info() {
        return String.format("%s items, %s fluids, %s categories, %s disguised blocks, %s recipes", items().size(), fluids().size(), categories().size(), disguisedBlocks().size(), recipes.size());
    }
}
