package net.minecraft.tags;

import java.util.List;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.material.FluidType;

public final class TagsFluid {

    protected static final TagUtil<FluidType> a = TagStatic.a(new MinecraftKey("fluid"), ITagRegistry::getFluidTags);
    public static final Tag.e<FluidType> WATER = a("water");
    public static final Tag.e<FluidType> LAVA = a("lava");

    private static Tag.e<FluidType> a(String s) {
        return TagsFluid.a.a(s);
    }

    public static List<? extends Tag.e<FluidType>> b() {
        return TagsFluid.a.c();
    }
}
