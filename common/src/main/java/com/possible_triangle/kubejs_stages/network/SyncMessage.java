package com.possible_triangle.kubejs_stages.network;

import com.possible_triangle.kubejs_stages.stage.Stage;
import com.possible_triangle.kubejs_stages.stage.Stages;
import dev.architectury.networking.NetworkManager;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Collection;
import java.util.function.Function;

public class SyncMessage {

    private final Collection<Stage> stages;

    public SyncMessage(Collection<Stage> stages) {
        this.stages = stages;
    }

    public FriendlyByteBuf encode(RegistryAccess registries) {
        var itemRegistry = registries.registryOrThrow(Registry.ITEM_REGISTRY);
        var fluidRegistry = registries.registryOrThrow(Registry.FLUID_REGISTRY);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeCollection(stages, (stageBuf, stage) -> {
            buf.writeUtf(stage.id());

            stageBuf.writeCollection(stage.items(), (itemBuf, stack) -> {
                var itemId = itemRegistry.getId(stack.getItem());
                itemBuf.writeInt(itemId);
            });

            stageBuf.writeCollection(stage.fluids(), (itemBuf, stack) -> {
                var itemId = fluidRegistry.getId(stack.getFluid());
                itemBuf.writeInt(itemId);
            });
        });

        return buf;
    }

    public static Function<RegistryAccess, SyncMessage> decode(FriendlyByteBuf buf) {

        Collection<Function<RegistryAccess, Stage>> stages = buf.readList(stageBuf -> {
            var id = stageBuf.readUtf();

            Collection<Function<RegistryAccess, ItemStackJS>> items = stageBuf.readList(itemBuf -> {
                var itemId = itemBuf.readInt();
                return registries -> {
                    var itemRegistry = registries.registryOrThrow(Registry.ITEM_REGISTRY);
                    var key = itemRegistry.getHolder(itemId).get().unwrapKey().get();
                    return ItemStackJS.of(itemRegistry.get(key));
                };
            });

            Collection<Function<RegistryAccess, FluidStackJS>> fluids = stageBuf.readList(itemBuf -> {
                var itemId = itemBuf.readInt();
                return registries -> {
                    var fluidRegistry = registries.registryOrThrow(Registry.FLUID_REGISTRY);
                    var key = fluidRegistry.getHolder(itemId).get().unwrapKey().get();
                    return FluidStackJS.of(fluidRegistry.get(key));
                };
            });

            return r -> new Stage(id, items.stream().map(it -> it.apply(r)).toList(), fluids.stream().map(it -> it.apply(r)).toList());
        });

        return r -> new SyncMessage(stages.stream().map(it -> it.apply(r)).toList());
    }

    public void handle(NetworkManager.PacketContext context) {
        Stages.receivedSync(stages);
    }

}
