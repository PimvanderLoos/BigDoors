package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.slf4j.Logger;

public class VeryBiasedToBottomHeight extends HeightProvider {

    public static final Codec<VeryBiasedToBottomHeight> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter((verybiasedtobottomheight) -> {
            return verybiasedtobottomheight.minInclusive;
        }), VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter((verybiasedtobottomheight) -> {
            return verybiasedtobottomheight.maxInclusive;
        }), Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("inner", 1).forGetter((verybiasedtobottomheight) -> {
            return verybiasedtobottomheight.inner;
        })).apply(instance, VeryBiasedToBottomHeight::new);
    });
    private static final Logger LOGGER = LogUtils.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    private final int inner;

    private VeryBiasedToBottomHeight(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1, int i) {
        this.minInclusive = verticalanchor;
        this.maxInclusive = verticalanchor1;
        this.inner = i;
    }

    public static VeryBiasedToBottomHeight of(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1, int i) {
        return new VeryBiasedToBottomHeight(verticalanchor, verticalanchor1, i);
    }

    @Override
    public int sample(RandomSource randomsource, WorldGenerationContext worldgenerationcontext) {
        int i = this.minInclusive.resolveY(worldgenerationcontext);
        int j = this.maxInclusive.resolveY(worldgenerationcontext);

        if (j - i - this.inner + 1 <= 0) {
            VeryBiasedToBottomHeight.LOGGER.warn("Empty height range: {}", this);
            return i;
        } else {
            int k = MathHelper.nextInt(randomsource, i + this.inner, j);
            int l = MathHelper.nextInt(randomsource, i, k - 1);

            return MathHelper.nextInt(randomsource, i, l - 1 + this.inner);
        }
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.VERY_BIASED_TO_BOTTOM;
    }

    public String toString() {
        return "biased[" + this.minInclusive + "-" + this.maxInclusive + " inner: " + this.inner + "]";
    }
}
