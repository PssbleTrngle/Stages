package com.possible_triangle.kubejs_stages.stage;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.resources.ResourceLocation;
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

    private List<IngredientJS> buildItems() {
        var excluded = excludedItems.build();
        return items.build().stream().map(ingredient ->
                excluded.stream().map(IngredientJS::not).reduce(ingredient, IngredientJS::filter)
        ).toList();
    }

    public Stage build() {
        return new Stage(defaultState, buildItems(), fluids.build(), categories.build(), disguisedBlocks.build(), recipes.build());
    }

    private final ImmutableSet.Builder<IngredientJS> items = new ImmutableSet.Builder<>();
    private final ImmutableSet.Builder<IngredientJS> excludedItems = new ImmutableSet.Builder<>();

    private final ImmutableSet.Builder<FluidStackJS> fluids = new ImmutableSet.Builder<>();
    private final ImmutableSet.Builder<String> categories = new ImmutableSet.Builder<>();
    private final ImmutableMap.Builder<Block, Block> disguisedBlocks = new ImmutableMap.Builder<>();
    private final ImmutableSet.Builder<ResourceLocation> recipes = new ImmutableSet.Builder<>();

    private ThreeState defaultState = ThreeState.UNSET;

    public void enabledByDefault() {
        setDefaultState(true);
    }

    public void disabledByDefault() {
        setDefaultState(false);
    }

    public void setDefaultState(boolean value) {
        setDefaultState(ThreeState.of(value));
    }

    public void setDefaultState(ThreeState state) {
        defaultState = state;
    }

    public void addItem(IngredientJS ingredient) {
        items.add(ingredient);
    }

    public void excludeItem(IngredientJS ingredient) {
        excludedItems.add(ingredient);
    }

    public void addFluid(FluidStackJS fluid) {
        fluids.add(fluid);
    }

    public void addCategory(String id) {
        categories.add(id);
    }

    public void disguiseBlock(Block block, Block as) {
        disguisedBlocks.put(block, as);
    }

    public void addRecipe(ResourceLocation id) {
        recipes.add(id);
    }

}
