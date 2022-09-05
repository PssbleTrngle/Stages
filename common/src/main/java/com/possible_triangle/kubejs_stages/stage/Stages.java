package com.possible_triangle.kubejs_stages.stage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.network.StagesNetwork;
import com.possible_triangle.kubejs_stages.network.SyncMessage;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import dev.latvian.mods.kubejs.recipe.filter.OutputFilter;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Stages {

    public static void setup() {
        LifecycleEvent.SERVER_STOPPED.register($ -> clear());
    }

    public static void removeRecipes(RecipeEventJS event) {
        definedStages.forEach((id, stage) -> {
            stage.items().forEach(item -> {
                event.remove(new OutputFilter(item, false));
            });
        });
    }

    private static final HashMap<String, Consumer<Stage>> CLIENT_LISTENERS = Maps.newHashMap();

    private static Map<String, Stage> definedStages = Collections.emptyMap();
    private static final HashMap<String, StageBuilder> LOADING_STAGES = Maps.newHashMap();
    private static Stage disabledContent = Stage.EMPTY;

    private static final Set<String> DISABLED = Sets.newHashSet();

    private static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(id -> new TextComponent("stage does not exist: '" + id + "'"));

    public static boolean enable(MinecraftServer server, String id) throws CommandSyntaxException {
        if (!definedStages.containsKey(id)) throw NOT_FOUND.create(id);
        if (!DISABLED.contains(id)) return false;
        DISABLED.remove(id);
        updateDisabled(server);
        return true;
    }

    public static boolean disable(MinecraftServer server, String id) throws CommandSyntaxException {
        if (!definedStages.containsKey(id)) throw NOT_FOUND.create(id);
        if (DISABLED.contains(id)) return false;
        DISABLED.add(id);
        updateDisabled(server);
        return true;
    }

    public static int disableAll(MinecraftServer server) {
        var enabled = definedStages.keySet().stream().filter(it -> !DISABLED.contains(it)).toList();
        DISABLED.addAll(enabled);
        updateDisabled(server);
        return enabled.size();
    }

    public static int enableAll(MinecraftServer server) {
        var disabled = definedStages.keySet().stream().filter(DISABLED::contains).toList();
        DISABLED.clear();
        updateDisabled(server);
        return disabled.size();
    }

    public static void clear() {
        LOADING_STAGES.clear();
        definedStages = Collections.emptyMap();
        disabledContent = Stage.EMPTY;
    }

    public static void finishLoad() {
        var map = new ImmutableMap.Builder<String, Stage>();
        LOADING_STAGES.forEach((key, builder) -> map.put(key, builder.build()));
        LOADING_STAGES.clear();
        definedStages = map.build();

        KubeJSStages.LOGGER.info("Loaded {} stages", definedStages.size());

        updateDisabled(null);
    }

    private static void updateDisabled(@Nullable MinecraftServer server) {
        disabledContent = definedStages.entrySet().stream()
                .filter(it -> DISABLED.contains(it.getKey()))
                .map(Map.Entry::getValue)
                .reduce(Stage.EMPTY, Stage::merge);
        if (server != null) server.getPlayerList().getPlayers().forEach(StagesNetwork::sync);
        notifyListeners(disabledContent);
    }

    public static void registerStage(String id, StageBuilder stage) {
        KubeJSStages.LOGGER.info("Registered stage '{}'", id);
        LOADING_STAGES.put(id, stage);
    }

    public static void unsubscribe(String id) {
        CLIENT_LISTENERS.remove(id);
    }

    public static Runnable onChange(String id, Consumer<Stage> listener) {
        CLIENT_LISTENERS.put(id, listener);
        return () -> unsubscribe(id);
    }

    public static Runnable onChangeOnce(String id, Consumer<Stage> listener) {
        return onChange(id, stage -> {
            listener.accept(stage);
            unsubscribe(id);
        });
    }

    public static SyncMessage createSyncMessage() {
        return new SyncMessage(disabledContent);
    }

    public static void notifyListeners(Stage disabled) {
        CLIENT_LISTENERS.values().forEach(listener -> {
            try {
                listener.accept(disabled);
            } catch (Throwable e) {
                KubeJSStages.LOGGER.error("Client Handler encountered exception: {}", e.getMessage());
            }
        });
    }
}
