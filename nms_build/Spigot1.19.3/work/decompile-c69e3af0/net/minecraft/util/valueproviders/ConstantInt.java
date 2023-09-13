package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;

public class ConstantInt extends IntProvider {

    public static final ConstantInt ZERO = new ConstantInt(0);
    public static final Codec<ConstantInt> CODEC = Codec.either(Codec.INT, RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("value").forGetter((constantint) -> {
            return constantint.value;
        })).apply(instance, ConstantInt::new);
    })).xmap((either) -> {
        return (ConstantInt) either.map(ConstantInt::of, (constantint) -> {
            return constantint;
        });
    }, (constantint) -> {
        return Either.left(constantint.value);
    });
    private final int value;

    public static ConstantInt of(int i) {
        return i == 0 ? ConstantInt.ZERO : new ConstantInt(i);
    }

    private ConstantInt(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int sample(RandomSource randomsource) {
        return this.value;
    }

    @Override
    public int getMinValue() {
        return this.value;
    }

    @Override
    public int getMaxValue() {
        return this.value;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CONSTANT;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}
