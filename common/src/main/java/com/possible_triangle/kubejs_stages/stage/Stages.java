package com.possible_triangle.kubejs_stages.stage;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.possible_triangle.kubejs_stages.features.StagesDisguises;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public class Stages {

    private static StagesAccess clientAccess = null;
    private static ServerStagesAccess serverAccess = new ServerStagesAccess();

    public static void initClient() {
        clientAccess = new ClientStagesAccess();
        registerListeners();
    }

    public static void clearClient() {
        clientAccess = null;
    }

    public static void initServer(MinecraftServer server) {
        getServerAccess().ifPresent(it -> it.setServer(server));
    }

    public static void clearServer() {
        serverAccess = null;
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
