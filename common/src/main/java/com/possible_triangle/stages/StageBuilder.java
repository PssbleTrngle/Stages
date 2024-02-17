package com.possible_triangle.stages;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.possible_triangle.stages.platform.FluidStack;
import com.possible_triangle.stages.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.List;

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

    private List<Ingredient> buildItems() {
        var excluded = excludedItems.build();
        return items.build().stream().map(ingredient ->
                excluded.stream().reduce(ingredient, Services.PLATFORM::subtract)
        ).toList();
    }

    public Stage build() {
        return new Stage(defaultState, buildItems(), fluids.build(), categories.build(), disguisedBlocks.build(), recipes.build());
    }

    private final ImmutableSet.Builder<Ingredient> items = new ImmutableSet.Builder<>();
    private final ImmutableSet.Builder<Ingredient> excludedItems = new ImmutableSet.Builder<>();

    private final ImmutableSet.Builder<FluidStack> fluids = new ImmutableSet.Builder<>();
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

    public void addItem(Ingredient ingredient) {
        items.add(ingredient);
    }

    public void excludeItem(Ingredient ingredient) {
        excludedItems.add(ingredient);
    }

    public void addFluid(FluidStack fluid) {
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
