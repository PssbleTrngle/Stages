package com.possible_triangle.kubejs_stages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class StageReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();

    public StageReloadListener() {
        super(GSON, "stage");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> loaded, ResourceManager manager, ProfilerFiller profiler) {
        StagesApi.addStage("test", builder -> {
            builder.addItem(Ingredient.of(Items.DIAMOND));
            builder.addRecipe(new ResourceLocation("diamond_pickaxe"));
            builder.disguiseBlock(Blocks.DIAMOND_ORE, Blocks.STONE);
        });
    }
}
