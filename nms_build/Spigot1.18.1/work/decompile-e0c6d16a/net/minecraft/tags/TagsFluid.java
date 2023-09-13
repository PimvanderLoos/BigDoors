package net.minecraft.tags;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.material.FluidType;

public final class TagsFluid {

    protected static final TagUtil<FluidType> HELPER = TagStatic.create(IRegistry.FLUID_REGISTRY, "tags/fluids");
    private static final List<Tag<FluidType>> KNOWN_TAGS = Lists.newArrayList();
    public static final Tag.e<FluidType> WATER = bind("water");
    public static final Tag.e<FluidType> LAVA = bind("lava");

    private TagsFluid() {}

    private static Tag.e<FluidType> bind(String s) {
        Tag.e<FluidType> tag_e = TagsFluid.HELPER.bind(s);

        TagsFluid.KNOWN_TAGS.add(tag_e);
        return tag_e;
    }

    public static Tags<FluidType> getAllTags() {
        return TagsFluid.HELPER.getAllTags();
    }

    /** @deprecated */
    @Deprecated
    public static List<Tag<FluidType>> getStaticTags() {
        return TagsFluid.KNOWN_TAGS;
    }
}
