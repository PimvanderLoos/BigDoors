package net.minecraft.server;

import java.util.function.Predicate;

public enum FluidCollisionOption {

    NEVER((fluid) -> {
        return false;
    }), SOURCE_ONLY(Fluid::d), ALWAYS((fluid) -> {
        return !fluid.e();
    });

    public final Predicate<Fluid> d;

    private FluidCollisionOption(Predicate predicate) {
        this.d = predicate;
    }
}
