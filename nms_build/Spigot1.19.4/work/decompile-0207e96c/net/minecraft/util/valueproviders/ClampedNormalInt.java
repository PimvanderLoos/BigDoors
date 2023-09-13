package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;

public class ClampedNormalInt extends IntProvider {

    public static final Codec<ClampedNormalInt> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("mean").forGetter((clampednormalint) -> {
            return clampednormalint.mean;
        }), Codec.FLOAT.fieldOf("deviation").forGetter((clampednormalint) -> {
            return clampednormalint.deviation;
        }), Codec.INT.fieldOf("min_inclusive").forGetter((clampednormalint) -> {
            return clampednormalint.min_inclusive;
        }), Codec.INT.fieldOf("max_inclusive").forGetter((clampednormalint) -> {
            return clampednormalint.max_inclusive;
        })).apply(instance, ClampedNormalInt::new);
    }).comapFlatMap((clampednormalint) -> {
        return clampednormalint.max_inclusive < clampednormalint.min_inclusive ? DataResult.error(() -> {
            return "Max must be larger than min: [" + clampednormalint.min_inclusive + ", " + clampednormalint.max_inclusive + "]";
        }) : DataResult.success(clampednormalint);
    }, Function.identity());
    private final float mean;
    private final float deviation;
    private final int min_inclusive;
    private final int max_inclusive;

    public static ClampedNormalInt of(float f, float f1, int i, int j) {
        return new ClampedNormalInt(f, f1, i, j);
    }

    private ClampedNormalInt(float f, float f1, int i, int j) {
        this.mean = f;
        this.deviation = f1;
        this.min_inclusive = i;
        this.max_inclusive = j;
    }

    @Override
    public int sample(RandomSource randomsource) {
        return sample(randomsource, this.mean, this.deviation, (float) this.min_inclusive, (float) this.max_inclusive);
    }

    public static int sample(RandomSource randomsource, float f, float f1, float f2, float f3) {
        return (int) MathHelper.clamp(MathHelper.normal(randomsource, f, f1), f2, f3);
    }

    @Override
    public int getMinValue() {
        return this.min_inclusive;
    }

    @Override
    public int getMaxValue() {
        return this.max_inclusive;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CLAMPED_NORMAL;
    }

    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min_inclusive + "-" + this.max_inclusive + "]";
    }
}
