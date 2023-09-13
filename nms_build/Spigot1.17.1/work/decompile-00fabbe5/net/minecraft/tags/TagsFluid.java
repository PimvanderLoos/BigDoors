package net.minecraft.tags;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.material.FluidType;

public final class TagsFluid {

    protected static final TagUtil<FluidType> HELPER = TagStatic.a(IRegistry.FLUID_REGISTRY, "tags/fluids");
    private static final List<Tag<FluidType>> KNOWN_TAGS = Lists.newArrayList();
    public static final Tag.e<FluidType> WATER = a("water");
    public static final Tag.e<FluidType> LAVA = a("lava");

    private TagsFluid() {}

    private static Tag.e<FluidType> a(String s) {
        Tag.e<FluidType> tag_e = TagsFluid.HELPER.a(s);

        TagsFluid.KNOWN_TAGS.add(tag_e);
        return tag_e;
    }

    public static Tags<FluidType> a() {
        return TagsFluid.HELPER.b();
    }

    @Deprecated
    public static List<Tag<FluidType>> b() {
        return TagsFluid.KNOWN_TAGS;
    }
}
