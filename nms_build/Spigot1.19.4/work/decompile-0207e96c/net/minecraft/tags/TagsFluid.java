package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.material.FluidType;

public final class TagsFluid {

    public static final TagKey<FluidType> WATER = create("water");
    public static final TagKey<FluidType> LAVA = create("lava");

    private TagsFluid() {}

    private static TagKey<FluidType> create(String s) {
        return TagKey.create(Registries.FLUID, new MinecraftKey(s));
    }
}
