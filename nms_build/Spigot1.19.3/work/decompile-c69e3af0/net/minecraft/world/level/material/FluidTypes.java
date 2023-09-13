package net.minecraft.world.level.material;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FluidTypes {

    public static final FluidType EMPTY = register("empty", new FluidTypeEmpty());
    public static final FluidTypeFlowing FLOWING_WATER = (FluidTypeFlowing) register("flowing_water", new FluidTypeWater.a());
    public static final FluidTypeFlowing WATER = (FluidTypeFlowing) register("water", new FluidTypeWater.b());
    public static final FluidTypeFlowing FLOWING_LAVA = (FluidTypeFlowing) register("flowing_lava", new FluidTypeLava.a());
    public static final FluidTypeFlowing LAVA = (FluidTypeFlowing) register("lava", new FluidTypeLava.b());

    public FluidTypes() {}

    private static <T extends FluidType> T register(String s, T t0) {
        return (FluidType) IRegistry.register(BuiltInRegistries.FLUID, s, t0);
    }

    static {
        Iterator iterator = BuiltInRegistries.FLUID.iterator();

        while (iterator.hasNext()) {
            FluidType fluidtype = (FluidType) iterator.next();
            UnmodifiableIterator unmodifiableiterator = fluidtype.getStateDefinition().getPossibleStates().iterator();

            while (unmodifiableiterator.hasNext()) {
                Fluid fluid = (Fluid) unmodifiableiterator.next();

                FluidType.FLUID_STATE_REGISTRY.add(fluid);
            }
        }

    }
}
