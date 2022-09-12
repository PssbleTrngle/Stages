package com.possible_triangle.kubejs_stages.stage;

import com.possible_triangle.kubejs_stages.features.StagesDisguises;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.LifecycleEvent;

import java.util.Objects;
import java.util.Optional;

public class Stages {

    private static StagesAccess clientAccess = null;
    private static ServerStagesAccess serverAccess = null;

    public static void setup() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register($ -> {
            clientAccess = new ClientStagesAccess();
            registerListeners();
        });

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register($ -> {
            clientAccess = null;
        });

        LifecycleEvent.SETUP.register(() -> {
            serverAccess = new ServerStagesAccess();
            registerListeners();
        });
    }

    private static void registerListeners() {
        StagesDisguises.init();
    }

    public static StagesAccess getAccess() {
        if (clientAccess != null) return clientAccess;
        return Objects.requireNonNull(serverAccess);
    }

    public static Optional<ServerStagesAccess> getServerAccess() {
        return Optional.ofNullable(serverAccess);
    }
}
