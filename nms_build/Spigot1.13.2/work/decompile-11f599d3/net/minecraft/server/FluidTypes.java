package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;

public class FluidTypes {

    private static final Set<FluidType> f;
    public static final FluidType EMPTY;
    public static final FluidTypeFlowing FLOWING_WATER;
    public static final FluidTypeFlowing WATER;
    public static final FluidTypeFlowing FLOWING_LAVA;
    public static final FluidTypeFlowing LAVA;

    private static FluidType a(String s) {
        FluidType fluidtype = (FluidType) IRegistry.FLUID.getOrDefault(new MinecraftKey(s));

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
            EMPTY = a("empty");
            FLOWING_WATER = (FluidTypeFlowing) a("flowing_water");
            WATER = (FluidTypeFlowing) a("water");
            FLOWING_LAVA = (FluidTypeFlowing) a("flowing_lava");
            LAVA = (FluidTypeFlowing) a("lava");
            FluidTypes.f.clear();
        }
    }
}
