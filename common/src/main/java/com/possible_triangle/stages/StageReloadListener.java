package com.possible_triangle.stages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.possible_triangle.stages.platform.FluidStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class StageReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();

    public StageReloadListener() {
        super(GSON, "stage");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> loaded, ResourceManager manager, ProfilerFiller profiler) {
        Stages.getServerAccess().ifPresent(access -> {
            loaded.forEach((id, json) -> {
                decode(json).ifPresent(stage -> {
                    access.registerStage(id.toString(), stage);
                });
            });

            access.finishLoad();
        });
    }

    private static Optional<StageBuilder> decode(JsonElement element) {
        var json = element.getAsJsonObject();

        var stage = StageBuilder.create(builder -> {
            if (json.has("defaultState")) {
                builder.setDefaultState(json.get("defaultState").getAsBoolean());
            }

            if (json.has("items")) json.getAsJsonArray("items").forEach(it ->
                    tryDecode(() -> Ingredient.fromJson(it))
                            .ifPresent(builder::addItem)
            );

            if (json.has("fluids")) json.getAsJsonArray("fluids").forEach(it ->
                    tryDecode(() -> FluidStack.fromJson(it))
                            .ifPresent(builder::addFluid)
            );

            if (json.has("categories")) json.getAsJsonArray("categories").forEach(it ->
                    tryDecode(it::getAsString)
                            .ifPresent(builder::addCategory)
            );

            if (json.has("disguisedBlocks")) json.getAsJsonObject("disguisedBlocks").entrySet().forEach(it ->
                    tryDecode(() -> {
                                var disguised = Registry.BLOCK.getOrThrow(ResourceKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(it.getKey())));
                                var disguise = Registry.BLOCK.getOrThrow(ResourceKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(it.getValue().getAsString())));
                                return new Pair<>(disguised, disguise);
                            }
                    ).ifPresent(pair -> builder.disguiseBlock(pair.getFirst(), pair.getSecond()))
            );

            if (json.has("recipes")) json.getAsJsonArray("recipes").forEach(it ->
                    tryDecode(() -> new ResourceLocation(it.getAsString()))
                            .ifPresent(builder::addRecipe)
            );

            if (json.has("requires")) json.getAsJsonArray("requires").forEach(it ->
                    tryDecode(() -> new ResourceLocation(it.getAsString()))
                            .ifPresent(builder::requires)
            );
        });

        return Optional.of(stage);
    }

    private static <T> Optional<T> tryDecode(Supplier<T> decoder) {
        try {
            return Optional.of(decoder.get());
        } catch (JsonSyntaxException | IllegalStateException ignored) {
            return Optional.empty();
        }
    }
}
