package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;

public class ClampedNormalFloat extends FloatProvider {

    public static final Codec<ClampedNormalFloat> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("mean").forGetter((clampednormalfloat) -> {
            return clampednormalfloat.mean;
        }), Codec.FLOAT.fieldOf("deviation").forGetter((clampednormalfloat) -> {
            return clampednormalfloat.deviation;
        }), Codec.FLOAT.fieldOf("min").forGetter((clampednormalfloat) -> {
            return clampednormalfloat.min;
        }), Codec.FLOAT.fieldOf("max").forGetter((clampednormalfloat) -> {
            return clampednormalfloat.max;
        })).apply(instance, ClampedNormalFloat::new);
    }).comapFlatMap((clampednormalfloat) -> {
        return clampednormalfloat.max < clampednormalfloat.min ? DataResult.error(() -> {
            return "Max must be larger than min: [" + clampednormalfloat.min + ", " + clampednormalfloat.max + "]";
        }) : DataResult.success(clampednormalfloat);
    }, Function.identity());
    private final float mean;
    private final float deviation;
    private final float min;
    private final float max;

    public static ClampedNormalFloat of(float f, float f1, float f2, float f3) {
        return new ClampedNormalFloat(f, f1, f2, f3);
    }

    private ClampedNormalFloat(float f, float f1, float f2, float f3) {
        this.mean = f;
        this.deviation = f1;
        this.min = f2;
        this.max = f3;
    }

    @Override
    public float sample(RandomSource randomsource) {
        return sample(randomsource, this.mean, this.deviation, this.min, this.max);
    }

    public static float sample(RandomSource randomsource, float f, float f1, float f2, float f3) {
        return MathHelper.clamp(MathHelper.normal(randomsource, f, f1), f2, f3);
    }

    @Override
    public float getMinValue() {
        return this.min;
    }

    @Override
    public float getMaxValue() {
        return this.max;
    }

    @Override
    public FloatProviderType<?> getType() {
        return FloatProviderType.CLAMPED_NORMAL;
    }

    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
    }
}
