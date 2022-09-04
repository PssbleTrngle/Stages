package com.possible_triangle.kubejs_stages.stage;

import com.google.common.collect.Maps;
import com.possible_triangle.kubejs_stages.KubeJSStages;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import dev.latvian.mods.kubejs.recipe.filter.OutputFilter;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

public class Stages {

    public static void setup() {
        LifecycleEvent.SERVER_STOPPED.register($ -> SERVER_VALUES.clear());
    }

    public static void removeRecipes(RecipeEventJS event) {
        getServerStages().forEach(stage -> {
            stage.items().forEach(item -> {
                event.remove(new OutputFilter(item, false));
            });
        });
    }

    private static final HashMap<String, Consumer<Stage>> CLIENT_LISTENERS = Maps.newHashMap();

    private static final ArrayList<Stage> SERVER_VALUES = Lists.newArrayList();

    public static void registerStage(Stage stage) {
        SERVER_VALUES.add(stage);
    }

    public static void onClient(String id, Consumer<Stage> listener) {
        CLIENT_LISTENERS.put(id, listener);
    }

    public static Collection<Stage> getServerStages() {
        return SERVER_VALUES.stream().toList();
    }

    public static void receivedSync(Collection<Stage> stages) {
        stages.forEach(stage -> CLIENT_LISTENERS.values().forEach((it) -> {
            try {
                it.accept(stage);
            } catch (Throwable e) {
                KubeJSStages.LOGGER.error("Client Handler encountered exception: {}", e.getMessage());
            }
        }));
    }
}
