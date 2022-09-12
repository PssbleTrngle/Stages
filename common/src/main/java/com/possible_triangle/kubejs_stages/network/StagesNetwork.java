package com.possible_triangle.kubejs_stages.network;

import com.possible_triangle.kubejs_stages.KubeJSStages;
import com.possible_triangle.kubejs_stages.stage.Stages;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class StagesNetwork {
    private static final ResourceLocation SYNC_PACKET = new ResourceLocation(KubeJSStages.ID, "sync");

    public static void sync(ServerPlayer player) {
        Stages.getServerAccess().ifPresent(stages -> {
            var packet = stages.createSyncMessage();
            NetworkManager.sendToPlayer(player, SYNC_PACKET, packet.encode(player.server.registryAccess()));
        });
    }

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SYNC_PACKET, (buf, context) -> {
            var message = SyncMessage.decode(buf);

            context.queue(() -> {
                var minecraft = Minecraft.getInstance();
                var connection = minecraft.getConnection();
                message.apply(connection.registryAccess()).handle(context);
            });
        });

        PlayerEvent.PLAYER_JOIN.register(StagesNetwork::sync);
    }

}
