package com.possible_triangle.kubejs_stages.platform;

import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;

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
            var key = ResourceKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(json.get("fluid").getAsString()));
            var fluid = Registry.FLUID.getOrThrow(key);
            var amount = json.has("amount") ? json.get("amount").getAsInt() : 1;
            return FluidStack.of(fluid, amount);
        } else {
            var key = ResourceKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(element.getAsString()));
            var fluid = Registry.FLUID.getOrThrow(key);
            return FluidStack.of(fluid);
        }
    }
}
