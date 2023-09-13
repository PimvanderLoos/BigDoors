package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;

public class FluidTypes {

    private static final Set<FluidType> f;
    public static final FluidType a;
    public static final FluidTypeFlowing b;
    public static final FluidTypeFlowing c;
    public static final FluidTypeFlowing d;
    public static final FluidTypeFlowing e;

    private static FluidType a(String s) {
        FluidType fluidtype = (FluidType) FluidType.c.get(new MinecraftKey(s));

        if (!FluidTypes.f.add(fluidtype)) {
            throw new IllegalStateException("Invalid Fluid requested: " + s);
        } else {
            return fluidtype;
        }
    }

    static {
        if (!DispenserRegistry.a()) {
            throw new RuntimeException("Accessed Fluids before Bootstrap!");
        } else {
            f = Sets.newHashSet(new FluidType[] { (FluidType) null});
            a = a("empty");
            b = (FluidTypeFlowing) a("flowing_water");
            c = (FluidTypeFlowing) a("water");
            d = (FluidTypeFlowing) a("flowing_lava");
            e = (FluidTypeFlowing) a("lava");
            FluidTypes.f.clear();
        }
    }
}
