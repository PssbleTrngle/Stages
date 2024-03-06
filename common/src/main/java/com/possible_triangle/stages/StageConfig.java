package com.possible_triangle.stages;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class StageConfig {

    private final Map<ResourceLocation, ThreeState> states = Maps.newHashMap();

    private static StageConfig instance = null;

    private static final Path PATH = Paths.get("config", "stages.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static StageConfig read() {
        var created = new StageConfig();

        if (!Files.exists(PATH)) {
            created.write();
            return created;
        }

        try {
            var json = GSON.fromJson(Files.readString(PATH), JsonObject.class);
            var list = json.getAsJsonObject("stages");
            if (list != null) {
                list.entrySet().forEach(e -> {
                    var state = ThreeState.valueOf(e.getValue().getAsString().toUpperCase());
                    created.states.put(new ResourceLocation(e.getKey()), state);
                });
            }
        } catch (IOException e) {
            CommonClass.LOGGER.warn("Error reading config '{}'", PATH);
        }
        return created;
    }

    private void write() {
        try {
            var json = new JsonObject();
            var statesMap = new JsonObject();
            states.forEach((id, state) -> {
                if (state == ThreeState.UNSET) return;
                statesMap.addProperty(id.toString(), state.name().toLowerCase());
            });

            json.add("stages", statesMap);

            Files.writeString(PATH, GSON.toJson(json));
        } catch (IOException e) {
            CommonClass.LOGGER.warn("Error while saving to '{}'", PATH);
        }
    }

    public static StageConfig instance(@Nullable MinecraftServer server) {
        if(server == null) CommonClass.LOGGER.warn("Accessing global scope without server context");
        if (instance == null) {
            CommonClass.LOGGER.debug("Loading config");
            instance = read();
        }
        return instance;
    }

    public ThreeState getState(ResourceLocation id) {
        return states.getOrDefault(id, ThreeState.UNSET);
    }

    public boolean setState(ResourceLocation id, ThreeState state) {
        if (getState(id) == state) return false;
        states.put(id, state);
        write();
        return true;
    }

}
