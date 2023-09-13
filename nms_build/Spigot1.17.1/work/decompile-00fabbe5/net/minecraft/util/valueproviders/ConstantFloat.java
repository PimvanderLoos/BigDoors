package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;

public class ConstantFloat extends FloatProvider {

    public static final ConstantFloat ZERO = new ConstantFloat(0.0F);
    public static final Codec<ConstantFloat> CODEC = Codec.either(Codec.FLOAT, RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("value").forGetter((constantfloat) -> {
            return constantfloat.value;
        })).apply(instance, ConstantFloat::new);
    })).xmap((either) -> {
        return (ConstantFloat) either.map(ConstantFloat::a, (constantfloat) -> {
            return constantfloat;
        });
    }, (constantfloat) -> {
        return Either.left(constantfloat.value);
    });
    private final float value;

    public static ConstantFloat a(float f) {
        return f == 0.0F ? ConstantFloat.ZERO : new ConstantFloat(f);
    }

    private ConstantFloat(float f) {
        this.value = f;
    }

    public float d() {
        return this.value;
    }

    @Override
    public float a(Random random) {
        return this.value;
    }

    @Override
    public float a() {
        return this.value;
    }

    @Override
    public float b() {
        return this.value + 1.0F;
    }

    @Override
    public FloatProviderType<?> c() {
        return FloatProviderType.CONSTANT;
    }

    public String toString() {
        return Float.toString(this.value);
    }
}
