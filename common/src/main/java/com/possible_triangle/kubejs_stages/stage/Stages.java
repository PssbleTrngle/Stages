package com.possible_triangle.kubejs_stages.stage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.StageConfig;
import com.possible_triangle.kubejs_stages.network.StagesNetwork;
import com.possible_triangle.kubejs_stages.network.SyncMessage;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Stages {

    public static void setup() {
        LifecycleEvent.SERVER_STOPPED.register($ -> clear());
    }

    private static final HashMap<String, Consumer<Stage>> LISTENERS = Maps.newHashMap();

    private static Map<String, Stage> definedStages = Collections.emptyMap();
    private static final HashMap<String, StageBuilder> LOADING_STAGES = Maps.newHashMap();
    private static Stage disabledContent = Stage.EMPTY;


    private static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(id -> new TextComponent("stage does not exist: '" + id + "'"));

    public static boolean isDisabled(String id) {
        return StageConfig.instance().isDisabled(id);
    }

    public static boolean enable(MinecraftServer server, String id) throws CommandSyntaxException {
        if (!definedStages.containsKey(id)) throw NOT_FOUND.create(id);
        StageConfig.instance().disable(id);
        updateDisabled(server);
        return true;
    }

    public static boolean disable(MinecraftServer server, String id) throws CommandSyntaxException {
        if (!definedStages.containsKey(id)) throw NOT_FOUND.create(id);
        StageConfig.instance().enable(id);
        updateDisabled(server);
        return true;
    }

    public static int disableAll(MinecraftServer server) {
        var enabled = definedStages.keySet().stream().filter(it -> !isDisabled(it)).toList();
        enabled.forEach(it -> StageConfig.instance().disable(it));
        updateDisabled(server);
        return enabled.size();
    }

    public static int enableAll(MinecraftServer server) {
        var disabled = definedStages.keySet().stream().filter(Stages::isDisabled).toList();
        disabled.forEach(it -> StageConfig.instance().enable(it));
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
                .filter(it -> isDisabled(it.getKey()))
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
        LISTENERS.remove(id);
    }

    public static Runnable onChange(String id, Consumer<Stage> listener) {
        LISTENERS.put(id, listener);
        return () -> unsubscribe(id);
    }

    public static Runnable onChangeOnce(String id, Consumer<Stage> listener) {
        return onChange(id, stage -> {
            unsubscribe(id);
            listener.accept(stage);
        });
    }

    public static SyncMessage createSyncMessage() {
        return new SyncMessage(disabledContent);
    }

    public static void notifyListeners(Stage disabled) {
        var frozen = new ImmutableSet.Builder<Consumer<Stage>>().addAll(LISTENERS.values()).build();
        frozen.forEach(listener -> {
            try {
                listener.accept(disabled);
            } catch (Throwable e) {
                KubeJSStages.LOGGER.error("Client Handler encountered exception: {}", e.getMessage());
            }
        });
    }
}
