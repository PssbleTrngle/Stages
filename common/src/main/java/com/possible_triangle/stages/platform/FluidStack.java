package com.possible_triangle.stages.platform;

import com.google.gson.JsonElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public record FluidStack(Fluid fluid, int amount, @Nullable CompoundTag nbt) {

    public static FluidStack of(Fluid fluid) {
        return of(fluid, 1000);
    }

    public static FluidStack of(Fluid fluid, int amount) {
        return new FluidStack(fluid, amount, null);
    }

    public static FluidStack fromJson(JsonElement element) {
        if (element.isJsonObject()) {
            var json = element.getAsJsonObject();
            var key = ResourceKey.create(Registries.FLUID, new ResourceLocation(json.get("fluid").getAsString()));
            var fluid = BuiltInRegistries.FLUID.getOrThrow(key);
            var amount = json.has("amount") ? json.get("amount").getAsInt() : 1;
            return FluidStack.of(fluid, amount);
        } else {
            var key = ResourceKey.create(Registries.FLUID, new ResourceLocation(element.getAsString()));
            var fluid = BuiltInRegistries.FLUID.getOrThrow(key);
            return FluidStack.of(fluid);
        }
    }
}
