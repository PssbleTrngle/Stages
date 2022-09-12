package com.possible_triangle.kubejs_stages.stage;

import com.google.common.collect.ImmutableMap;
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
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ServerStagesAccess extends StagesAccess {

    public ServerStagesAccess() {
        LifecycleEvent.SERVER_STOPPED.register($ -> clear());
    }

    private Map<String, Stage> definedStages = Collections.emptyMap();
    private final HashMap<String, StageBuilder> LOADING_STAGES = Maps.newHashMap();
    private Stage disabledContent = Stage.EMPTY;

    @Override
    public Stage getDisabledContent() {
        return disabledContent;
    }

    private void updateDisabled() {
        disabledContent = getStages()
                .filter(it -> isDisabled(it.getKey()))
                .map(Map.Entry::getValue)
                .reduce(Stage.EMPTY, Stage::merge);
        onUpdate();
        notifyListeners(disabledContent);
    }

    private static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(id -> new TextComponent(String.format("stage does not exist: '%s'", id)));

    @Override
    public Stream<Map.Entry<String, Stage>> getStages() {
        return definedStages.entrySet().stream();
    }

    @Override
    public boolean isDisabled(String id) {
        return StageConfig.instance(null).isDisabled(id);
    }


    public boolean enable(String id) throws CommandSyntaxException {
        if (!definedStages.containsKey(id)) throw NOT_FOUND.create(id);
        var success = StageConfig.instance(getServer()).enable(id);
        updateDisabled();
        return success;
    }

    public boolean disable(String id) throws CommandSyntaxException {
        if (!definedStages.containsKey(id)) throw NOT_FOUND.create(id);
        var success = StageConfig.instance(getServer()).disable(id);
        updateDisabled();
        return success;
    }

    public int disableAll() {
        var enabled = definedStages.keySet().stream().filter(this::isEnabled).toList();
        enabled.forEach(it -> StageConfig.instance(getServer()).disable(it));
        updateDisabled();
        return enabled.size();
    }

    public int enableAll() {
        var disabled = definedStages.keySet().stream().filter(this::isDisabled).toList();
        disabled.forEach(it -> StageConfig.instance(getServer()).enable(it));
        updateDisabled();
        return disabled.size();
    }

    public void clear() {
        LOADING_STAGES.clear();
        definedStages = Collections.emptyMap();
        disabledContent = Stage.EMPTY;
    }

    private @Nullable MinecraftServer getServer() {
        return KubeJSStages.getServer();
    }

    public void finishLoad() {
        var map = new ImmutableMap.Builder<String, Stage>();
        LOADING_STAGES.forEach((key, builder) -> map.put(key, builder.build()));
        LOADING_STAGES.clear();
        definedStages = map.build();

        KubeJSStages.LOGGER.info("Loaded {} stages", definedStages.size());

        updateDisabled();
    }

    @Override
    protected void onUpdate() {
        var server = getServer();
        if (server != null) server.getPlayerList().getPlayers().forEach(StagesNetwork::sync);
    }

    public void registerStage(String id, StageBuilder stage) {
        KubeJSStages.LOGGER.info("Registered stage '{}'", id);
        LOADING_STAGES.put(id, stage);
    }

    public SyncMessage createSyncMessage() {
        return new SyncMessage(disabledContent, getDisabledStages().toList());
    }
}
