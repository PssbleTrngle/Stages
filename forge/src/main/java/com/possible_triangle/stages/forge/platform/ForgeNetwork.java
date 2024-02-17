package com.possible_triangle.stages.forge.platform;

import com.possible_triangle.stages.CommonClass;
import com.possible_triangle.stages.network.StagesNetwork;
import com.possible_triangle.stages.network.SyncMessage;
import com.possible_triangle.stages.platform.services.INetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgeNetwork implements INetwork {

    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel CHANNEL;

    @Override
    public void send(ServerPlayer player, SyncMessage message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void register(IEventBus forgeBus) {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(CommonClass.ID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        CHANNEL.registerMessage(0, SyncMessage.class, SyncMessage::encode, SyncMessage::decode, (msg, supplier) -> {
           var ctx = supplier.get();
           ctx.enqueueWork(msg::handle);
           ctx.setPacketHandled(true);
        });

        forgeBus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            StagesNetwork.sync((ServerPlayer) event.getEntity());
        });
    }

}
