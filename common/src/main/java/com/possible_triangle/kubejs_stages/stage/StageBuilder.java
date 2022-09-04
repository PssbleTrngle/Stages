package com.possible_triangle.kubejs_stages.stage;

import com.google.common.collect.ImmutableSet;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;

public class StageBuilder {

    public interface Consumer {
        void accept(StageBuilder builder);
    }

    private final String id;

    private StageBuilder(String id) {
        this.id = id;
    }

    public static void create(String id, Consumer consumer) {
        var builder = new StageBuilder(id);
        consumer.accept(builder);
        var stage = builder.build();

        Stages.registerStage(stage);
    }

    private Stage build() {
        return new Stage(id, items.build(), fluids.build());
    }

    private final ImmutableSet.Builder<ItemStackJS> items = new ImmutableSet.Builder<>();
    private final ImmutableSet.Builder<FluidStackJS> fluids = new ImmutableSet.Builder<>();

    public void addItem(IngredientJS ingredient) {
        items.addAll(ingredient.getStacks());
    }

    public void addFluid(FluidStackJS fluid) {
        fluids.add(fluid);
    }

}
