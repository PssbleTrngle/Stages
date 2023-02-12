package com.possible_triangle.kubejs_stages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.possible_triangle.kubejs_stages.stage.ThreeState;

import net.minecraft.server.MinecraftServer;

public class StageConfig {

    private final Map<String, ThreeState> states = Maps.newHashMap();

    private static StageConfig instance = null;

    private static final Path PATH = Paths.get("config", "kubejs-stages.json");

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
                    created.states.put(e.getKey(), state);
                });
            }
        } catch (IOException e) {
            KubeJSStages.LOGGER.warn("Error reading config '{}'", PATH);
        }
        return created;
    }

    private void write() {
        try {
            var json = new JsonObject();
            var statesMap = new JsonObject();
            states.forEach((id, state) -> {
                if (state == ThreeState.UNSET) return;
                statesMap.addProperty(id, state.name().toLowerCase());
            });

            Files.writeString(PATH, GSON.toJson(json));
        } catch (IOException e) {
            KubeJSStages.LOGGER.warn("Error while saving to '{}'", PATH);
        }
    }

    public static StageConfig instance(@Nullable MinecraftServer server) {
        if (instance == null) {
            KubeJSStages.LOGGER.debug("Loading config");
            instance = read();
        }
        return instance;
    }

    public ThreeState getState(String id) {
        return states.getOrDefault(id, ThreeState.UNSET);
    }

    public boolean setState(String id, ThreeState state) {
        if (getState(id) == state) return false;
        states.put(id, state);
        write();
        return true;
    }

}
