package com.possible_triangle.stages.network;

import com.google.common.collect.ImmutableMap;
import com.possible_triangle.stages.CommonClass;
import com.possible_triangle.stages.platform.FluidStack;
import com.possible_triangle.stages.ClientStagesAccess;
import com.possible_triangle.stages.Stage;
import com.possible_triangle.stages.Stages;
import com.possible_triangle.stages.ThreeState;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.function.Function;

public class SyncMessage {

    public final Stage content;
    public final Collection<String> stages;
    private final RegistryAccess registries;

    public SyncMessage(Stage content, Collection<String> stages, RegistryAccess registries) {
        this.content = content;
        this.stages = stages;
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
        var itemRegistry = registries.registryOrThrow(Registry.ITEM_REGISTRY);
        var fluidRegistry = registries.registryOrThrow(Registry.FLUID_REGISTRY);
        var blockRegistry = registries.registryOrThrow(Registry.BLOCK_REGISTRY);

        buf.writeCollection(stages, FriendlyByteBuf::writeUtf);

        buf.writeCollection(content.stacks().stream().map(ItemStack::getItem).toList(), writeEntry(itemRegistry));
        buf.writeCollection(content.fluids().stream().map(FluidStack::fluid).toList(), writeEntry(fluidRegistry));
        buf.writeCollection(content.categories(), FriendlyByteBuf::writeUtf);
        buf.writeMap(content.disguisedBlocks(), writeEntry(blockRegistry), writeEntry(blockRegistry));
        buf.writeCollection(content.recipes(), (b, id) -> b.writeUtf(id.toString()));

        return buf;
    }

    public static SyncMessage decode(FriendlyByteBuf buf) {
        var stages = buf.readList(FriendlyByteBuf::readUtf);

        var items = buf.readList(b -> readEntry(b, Registry.ITEM_REGISTRY).andThen(Ingredient::of));
        var fluids = buf.readList(b -> readEntry(b, Registry.FLUID_REGISTRY).andThen(FluidStack::of));
        var categories = buf.readList(FriendlyByteBuf::readUtf);
        var disguisedBlocks = buf.readMap(b -> readEntry(b, Registry.BLOCK_REGISTRY), b -> readEntry(b, Registry.BLOCK_REGISTRY));
        var recipes = buf.readList(b -> new ResourceLocation(b.readUtf()));

        var registries = RegistryAccess.BUILTIN.get();

        var resolvedItems = items.stream().map(it -> it.apply(registries)).toList();
        var resolvedFluids = fluids.stream().map(it -> it.apply(registries)).toList();

        var resolvedBlocks = new ImmutableMap.Builder<Block, Block>();
        disguisedBlocks.forEach((key, value) -> resolvedBlocks.put(key.apply(registries), value.apply(registries)));

        var content = new Stage(ThreeState.UNSET, resolvedItems, resolvedFluids, categories, resolvedBlocks.build(), recipes);
        return new SyncMessage(content, stages, registries);
    }

    public void handle() {
        CommonClass.LOGGER.debug("Synced stage with {}", content.info());
        if (Stages.requireAccess() instanceof ClientStagesAccess access) {
            access.receiveSync(this);
        }
    }

}
