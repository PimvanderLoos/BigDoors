package net.minecraft.server;

import java.util.List;
import java.util.Set;

public class TagsFluid {

    private static final TagUtil<FluidType> c = new TagUtil<>();
    public static final Tag.e<FluidType> WATER = a("water");
    public static final Tag.e<FluidType> LAVA = a("lava");

    private static Tag.e<FluidType> a(String s) {
        return TagsFluid.c.a(s);
    }

    public static void a(Tags<FluidType> tags) {
        TagsFluid.c.a(tags);
    }

    public static Tags<FluidType> b() {
        return TagsFluid.c.b();
    }

    public static List<TagUtil.a<FluidType>> c() {
        return TagsFluid.c.c();
    }

    public static Set<MinecraftKey> b(Tags<FluidType> tags) {
        return TagsFluid.c.b(tags);
    }
}
