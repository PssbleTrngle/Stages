package com.possible_triangle.kubejs_stages.network;

import com.google.common.collect.ImmutableMap;
import com.possible_triangle.kubejs_stages.stage.Stage;
import com.possible_triangle.kubejs_stages.stage.Stages;
import dev.architectury.networking.NetworkManager;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SyncMessage {

    private final Stage stage;

    public SyncMessage(Stage stage) {
        this.stage = stage;
    }

    private static <T> BiConsumer<FriendlyByteBuf, T> writeEntry(Registry<T> registry) {
        return (buf, entry) -> buf.writeInt(registry.getId(entry));
    }

    private static <T> Function<RegistryAccess, T> readEntry(FriendlyByteBuf buf, ResourceKey<Registry<T>> registryKey) {
        var itemId = buf.readInt();
        return registries -> {
            var registry = registries.registryOrThrow(registryKey);
            var key = registry.getHolder(itemId).get().unwrapKey().get();
            return registry.get(key);
        };
    }

    public FriendlyByteBuf encode(RegistryAccess registries) {
        var itemRegistry = registries.registryOrThrow(Registry.ITEM_REGISTRY);
        var fluidRegistry = registries.registryOrThrow(Registry.FLUID_REGISTRY);
        var blockRegistry = registries.registryOrThrow(Registry.BLOCK_REGISTRY);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeCollection(stage.stacks().stream().map(ItemStack::getItem).toList(), writeEntry(itemRegistry));
        buf.writeCollection(stage.fluids().stream().map(FluidStackJS::getFluid).toList(), writeEntry(fluidRegistry));
        buf.writeMap(stage.disguisedBlocks(), writeEntry(blockRegistry), writeEntry(blockRegistry));

        return buf;
    }

    public static Function<RegistryAccess, SyncMessage> decode(FriendlyByteBuf buf) {
        var items = buf.readList(b -> readEntry(b, Registry.ITEM_REGISTRY).andThen(IngredientJS::of));
        var fluids = buf.readList(b -> readEntry(b, Registry.FLUID_REGISTRY).andThen(FluidStackJS::of));
        var blocks = buf.readMap(b -> readEntry(b, Registry.BLOCK_REGISTRY), b -> readEntry(b, Registry.BLOCK_REGISTRY));

        return r -> {
            var resolvedItems = items.stream().map(it -> it.apply(r)).toList();
            var resolvedFluids = fluids.stream().map(it -> it.apply(r)).toList();

            var resolvedBlocks = new ImmutableMap.Builder<Block, Block>();
            blocks.forEach((key, value) -> resolvedBlocks.put(key.apply(r), value.apply(r)));

            var stage = new Stage(resolvedItems, resolvedFluids, resolvedBlocks.build());
            return new SyncMessage(stage);
        };
    }

    public void handle(NetworkManager.PacketContext context) {
        Stages.notifyListeners(stage);
    }

}
