package net.minecraft.util;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.function.Function;

public interface ToFloatFunction<C> {

    ToFloatFunction<Float> IDENTITY = createUnlimited((f) -> {
        return f;
    });

    float apply(C c0);

    float minValue();

    float maxValue();

    static ToFloatFunction<Float> createUnlimited(final Float2FloatFunction float2floatfunction) {
        return new ToFloatFunction<Float>() {
            public float apply(Float ofloat) {
                return (Float) float2floatfunction.apply(ofloat);
            }

            @Override
            public float minValue() {
                return Float.NEGATIVE_INFINITY;
            }

            @Override
            public float maxValue() {
                return Float.POSITIVE_INFINITY;
            }
        };
    }

    default <C2> ToFloatFunction<C2> comap(final Function<C2, C> function) {
        return new ToFloatFunction<C2>() {
            @Override
            public float apply(C2 c2) {
                return ToFloatFunction.this.apply(function.apply(c2));
            }

            @Override
            public float minValue() {
                return ToFloatFunction.this.minValue();
            }

            @Override
            public float maxValue() {
                return ToFloatFunction.this.maxValue();
            }
        };
    }
}
