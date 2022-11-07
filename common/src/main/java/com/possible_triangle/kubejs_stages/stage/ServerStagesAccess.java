package com.possible_triangle.kubejs_stages.stage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.network.StagesNetwork;
import com.possible_triangle.kubejs_stages.network.SyncMessage;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ServerStagesAccess extends StagesAccess {

    public ServerStagesAccess() {
        LifecycleEvent.SERVER_STOPPED.register($ -> clear());
    }

    private Map<String, Stage> definedStages = Collections.emptyMap();
    private final HashMap<String, StageBuilder> loadingStages = Maps.newHashMap();

    void updateDisabled() {
        syncToPlayers();
        notifyListeners();
    }

    private static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(id -> new TextComponent(String.format("stage does not exist: '%s'", id)));

    public void assertExists(String id) throws CommandSyntaxException {
        if (!definedStages.containsKey(id)) throw NOT_FOUND.create(id);
    }

    public Stream<String> getStages() {
        return definedStages.keySet().stream();
    }

    @Override
    public Stream<String> getDisabledStages(StageContext context) {
        return getStages().filter(it -> isDisabled(it, context));
    }

    @Override
    public Stage getDisabledContent(StageContext context) {
        return definedStages.entrySet().stream()
                .filter(it -> isDisabled(it.getKey(), context))
                .map(Map.Entry::getValue)
                .reduce(Stage.EMPTY, Stage::merge);
    }

    @Override
    public ThreeState getState(String id, StageContext context) {
        if (!definedStages.containsKey(id)) return ThreeState.UNSET;

        var optionalState = Stream.of(StageScope.GLOBAL, StageScope.PLAYER)
                .map(it -> it.getOptionalState(id, context))
                .filter(it -> it != ThreeState.UNSET)
                .findFirst();

        return optionalState.orElseGet(() -> {
            if (context.strict()) return ThreeState.UNSET;
            else return getDefaultState(id);
        });
    }

    public ThreeState getDefaultState(String id) {
        return definedStages.get(id).defaultState();
    }

    public void clear() {
        loadingStages.clear();
        definedStages = Collections.emptyMap();
    }

    private @Nullable MinecraftServer getServer() {
        return KubeJSStages.getServer();
    }

    public void finishLoad() {
        var map = new ImmutableMap.Builder<String, Stage>();
        loadingStages.forEach((key, builder) -> map.put(key, builder.build()));
        loadingStages.clear();
        definedStages = map.build();

        KubeJSStages.LOGGER.debug("Loaded {} stages", definedStages.size());

        updateDisabled();
    }

    protected void syncToPlayers() {
        var server = getServer();
        if (server != null) server.getPlayerList().getPlayers().forEach(StagesNetwork::sync);
    }

    public void registerStage(String id, StageBuilder stage) {
        KubeJSStages.LOGGER.debug("Registered stage '{}'", id);
        loadingStages.put(id, stage);
    }

    public SyncMessage createSyncMessage(ServerPlayer player) {
        var context = new StageContext(player.server, player, false);
        return new SyncMessage(getDisabledContent(context), getDisabledStages(context).toList());
    }
}
