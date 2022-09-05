package com.possible_triangle.kubejs_stages.stage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.world.level.block.Block;

public class StageBuilder {

    public interface Consumer {
        void accept(StageBuilder builder);
    }

    private StageBuilder() {

    }

    public static StageBuilder create(Consumer consumer) {
        var builder = new StageBuilder();
        consumer.accept(builder);
        return builder;
    }

    public Stage build() {
        return new Stage(items.build(), fluids.build(), disguisedBlocks.build());
    }

    private final ImmutableSet.Builder<IngredientJS> items = new ImmutableSet.Builder<>();
    private final ImmutableSet.Builder<FluidStackJS> fluids = new ImmutableSet.Builder<>();
    private final ImmutableMap.Builder<Block, Block> disguisedBlocks = new ImmutableMap.Builder<>();

    public void addItem(IngredientJS ingredient) {
        items.add(ingredient);
    }

    public void addFluid(FluidStackJS fluid) {
        fluids.add(fluid);
    }

    public void disguiseBlock(Block block, Block as) {
        disguisedBlocks.put(block, as);
    }

}
