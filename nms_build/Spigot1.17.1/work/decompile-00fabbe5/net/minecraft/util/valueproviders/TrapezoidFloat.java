package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;

public class TrapezoidFloat extends FloatProvider {

    public static final Codec<TrapezoidFloat> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("min").forGetter((trapezoidfloat) -> {
            return trapezoidfloat.min;
        }), Codec.FLOAT.fieldOf("max").forGetter((trapezoidfloat) -> {
            return trapezoidfloat.max;
        }), Codec.FLOAT.fieldOf("plateau").forGetter((trapezoidfloat) -> {
            return trapezoidfloat.plateau;
        })).apply(instance, TrapezoidFloat::new);
    }).comapFlatMap((trapezoidfloat) -> {
        return trapezoidfloat.max < trapezoidfloat.min ? DataResult.error("Max must be larger than min: [" + trapezoidfloat.min + ", " + trapezoidfloat.max + "]") : (trapezoidfloat.plateau > trapezoidfloat.max - trapezoidfloat.min ? DataResult.error("Plateau can at most be the full span: [" + trapezoidfloat.min + ", " + trapezoidfloat.max + "]") : DataResult.success(trapezoidfloat));
    }, Function.identity());
    private final float min;
    private final float max;
    private final float plateau;

    public static TrapezoidFloat a(float f, float f1, float f2) {
        return new TrapezoidFloat(f, f1, f2);
    }

    private TrapezoidFloat(float f, float f1, float f2) {
        this.min = f;
        this.max = f1;
        this.plateau = f2;
    }

    @Override
    public float a(Random random) {
        float f = this.max - this.min;
        float f1 = (f - this.plateau) / 2.0F;
        float f2 = f - f1;

        return this.min + random.nextFloat() * f2 + random.nextFloat() * f1;
    }

    @Override
    public float a() {
        return this.min;
    }

    @Override
    public float b() {
        return this.max;
    }

    @Override
    public FloatProviderType<?> c() {
        return FloatProviderType.TRAPEZOID;
    }

    public String toString() {
        return "trapezoid(" + this.plateau + ") in [" + this.min + "-" + this.max + "]";
    }
}
