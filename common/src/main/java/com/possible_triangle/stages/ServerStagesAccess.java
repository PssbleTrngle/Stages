package com.possible_triangle.stages;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.possible_triangle.stages.network.StagesNetwork;
import com.possible_triangle.stages.network.SyncMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ServerStagesAccess extends StagesAccess {

    @Nullable
    private MinecraftServer server;

    public void setServer(MinecraftServer server) {
        this.server = server;
        if(server == null) clear();
    }

    public Optional<MinecraftServer> getServer() {
        return Optional.ofNullable(server);
    }

    private Map<String, Stage> definedStages = Collections.emptyMap();
    private final HashMap<String, StageBuilder> loadingStages = Maps.newHashMap();

    void updateDisabled() {
        syncToPlayers();
        notifyListeners();
    }

    private static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(id -> Component.literal(String.format("stage does not exist: '%s'", id)));

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

    public void finishLoad() {
        var map = new ImmutableMap.Builder<String, Stage>();
        loadingStages.forEach((key, builder) -> map.put(key, builder.build()));
        loadingStages.clear();
        definedStages = map.build();

        CommonClass.LOGGER.debug("Loaded {} stages", definedStages.size());

        updateDisabled();
    }

    protected void syncToPlayers() {
        getServer().ifPresent(server -> {
            server.getPlayerList().getPlayers().forEach(StagesNetwork::sync);
        });
    }

    public void registerStage(String id, StageBuilder stage) {
        CommonClass.LOGGER.debug("Registered stage '{}'", id);
        loadingStages.put(id, stage);
    }

    public SyncMessage createSyncMessage(ServerPlayer player) {
        var context = new StageContext(player.server, player, false);
        return new SyncMessage(getDisabledContent(context), getDisabledStages(context).toList(), player.server.registryAccess());
    }
}
