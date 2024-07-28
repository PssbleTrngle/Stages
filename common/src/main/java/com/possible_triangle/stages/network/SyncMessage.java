package com.possible_triangle.stages.network;

import com.google.common.collect.ImmutableMap;
import com.possible_triangle.stages.ClientStagesAccess;
import com.possible_triangle.stages.CommonClass;
import com.possible_triangle.stages.Stage;
import com.possible_triangle.stages.Stages;
import com.possible_triangle.stages.ThreeState;
import com.possible_triangle.stages.platform.FluidStack;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class SyncMessage {

    public final Stage content;
    private final RegistryAccess registries;

    public SyncMessage(Stage content, RegistryAccess registries) {
        this.content = content;
        this.registries = registries;
    }

    private static <T> FriendlyByteBuf.Writer<T> writeEntry(Registry<T> registry) {
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

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        var itemRegistry = registries.registryOrThrow(Registries.ITEM);
        var fluidRegistry = registries.registryOrThrow(Registries.FLUID);
        var blockRegistry = registries.registryOrThrow(Registries.BLOCK);

        buf.writeCollection(content.parents(), (b, id) -> b.writeUtf(id.toString()));

        buf.writeCollection(content.stacks().stream().map(ItemStack::getItem).toList(), writeEntry(itemRegistry));
        buf.writeCollection(content.fluids().stream().map(FluidStack::fluid).toList(), writeEntry(fluidRegistry));
        buf.writeCollection(content.categories(), FriendlyByteBuf::writeUtf);
        buf.writeMap(content.disguisedBlocks(), writeEntry(blockRegistry), writeEntry(blockRegistry));
        buf.writeCollection(content.recipes(), (b, id) -> b.writeUtf(id.toString()));

        return buf;
    }

    public static SyncMessage decode(FriendlyByteBuf buf) {
        var stages = buf.readList(it -> new ResourceLocation(it.readUtf()));

        var items = buf.readList(b -> readEntry(b, Registries.ITEM).andThen(Ingredient::of));
        var fluids = buf.readList(b -> readEntry(b, Registries.FLUID).andThen(FluidStack::of));
        var categories = buf.readList(FriendlyByteBuf::readUtf);
        var disguisedBlocks = buf.readMap(b -> readEntry(b, Registries.BLOCK), b -> readEntry(b, Registries.BLOCK));
        var recipes = buf.readList(b -> new ResourceLocation(b.readUtf()));

        var registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

        var resolvedItems = items.stream().map(it -> it.apply(registries)).toList();
        var resolvedFluids = fluids.stream().map(it -> it.apply(registries)).toList();

        var resolvedBlocks = new ImmutableMap.Builder<Block, Block>();
        disguisedBlocks.forEach((key, value) -> resolvedBlocks.put(key.apply(registries), value.apply(registries)));

        // TODO can move stages into parents
        var content = new Stage(ThreeState.UNSET, resolvedItems, resolvedFluids, categories, resolvedBlocks.build(), recipes, stages);
        return new SyncMessage(content, registries);
    }

    public void handle() {
        CommonClass.LOGGER.debug("Synced stage with {}", content.info());
        if (Stages.requireAccess() instanceof ClientStagesAccess access) {
            access.receiveSync(this);
        }
    }

}
