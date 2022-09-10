package com.possible_triangle.kubejs_stages;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class StageConfig {

    private final Set<String> disabled = Sets.newHashSet();

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
            var list = json.getAsJsonArray("disabledStages");
            list.forEach(it -> created.disabled.add(it.getAsString()));
        } catch (IOException e) {
            KubeJSStages.LOGGER.warn("Error reading config '{}'", PATH);
        }
        return created;
    }

    private void write() {
        try {
            var json = new JsonObject();
            var list = new JsonArray();
            disabled.forEach(list::add);
            json.add("disabledStages", list);

            Files.writeString(PATH, GSON.toJson(json));
        } catch (IOException e) {
            KubeJSStages.LOGGER.warn("Error while saving to '{}'", PATH);
        }
    }

    public static StageConfig instance(@Nullable MinecraftServer server) {
        if (instance == null) {
            KubeJSStages.LOGGER.info("Loading config");
            instance = read();
        }
        return instance;
    }

    public boolean isDisabled(String id) {
        return disabled.contains(id);
    }

    public boolean enable(String id) {
        if (!disabled.contains(id)) return false;
        disabled.remove(id);
        write();
        return true;
    }

    public boolean disable(String id) {
        if (disabled.contains(id)) return false;
        disabled.add(id);
        write();
        return true;
    }

}
