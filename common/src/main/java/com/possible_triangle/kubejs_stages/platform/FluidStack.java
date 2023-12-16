package com.possible_triangle.kubejs_stages.platform;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

import javax.annotationgit chg.Nullable;

public record FluidStack(Fluid fluid, int amount, @Nullable CompoundTag nbt) {

    public static FluidStack of(Fluid fluid) {
        return of(fluid, 1000);
    }

    public static FluidStack of(Fluid fluid, int amount) {
        return new FluidStack(fluid, amount, null);
    }

}
