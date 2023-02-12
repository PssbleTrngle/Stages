package com.possible_triangle.kubejs_stages.stage;

import java.util.Optional;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.possible_triangle.kubejs_stages.features.StagesDisguises;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.client.Minecraft;

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

    public static StagesAccess requireAccess() {
        return getAccess().orElseThrow(() -> new NullPointerException("StageAccess not initialized"));
    }

    public static Optional<StagesAccess> getAccess() {
        if (clientAccess != null) return Optional.of(clientAccess);
        return Optional.ofNullable(serverAccess);
    }

    public static Optional<ServerStagesAccess> getServerAccess() {
        return Optional.ofNullable(serverAccess);
    }

    static <T> Optional<T> modify(AccessConsumer<T> consumer) throws CommandSyntaxException {
        var access = getServerAccess();
        if (access.isPresent()) {
            var result = consumer.accept(access.get());
            access.get().updateDisabled();
            return Optional.ofNullable(result);
        } else {
            return Optional.empty();
        }
    }

    public static StageContext clientContext() {
        return new StageContext(null, Minecraft.getInstance().player, false);
    }

    @FunctionalInterface
    public interface AccessConsumer<T> {
        T accept(ServerStagesAccess access) throws CommandSyntaxException;
    }
}
