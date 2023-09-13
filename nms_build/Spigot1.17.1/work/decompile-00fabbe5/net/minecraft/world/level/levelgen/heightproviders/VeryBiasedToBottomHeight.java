package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    private final int inner;

    private VeryBiasedToBottomHeight(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1, int i) {
        this.minInclusive = verticalanchor;
        this.maxInclusive = verticalanchor1;
        this.inner = i;
    }

    public static VeryBiasedToBottomHeight a(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1, int i) {
        return new VeryBiasedToBottomHeight(verticalanchor, verticalanchor1, i);
    }

    @Override
    public int a(Random random, WorldGenerationContext worldgenerationcontext) {
        int i = this.minInclusive.a(worldgenerationcontext);
        int j = this.maxInclusive.a(worldgenerationcontext);

        if (j - i - this.inner + 1 <= 0) {
            VeryBiasedToBottomHeight.LOGGER.warn("Empty height range: {}", this);
            return i;
        } else {
            int k = MathHelper.nextInt(random, i + this.inner, j);
            int l = MathHelper.nextInt(random, i, k - 1);

            return MathHelper.nextInt(random, i, l - 1 + this.inner);
        }
    }

    @Override
    public HeightProviderType<?> a() {
        return HeightProviderType.VERY_BIASED_TO_BOTTOM;
    }

    public String toString() {
        return "biased[" + this.minInclusive + "-" + this.maxInclusive + " inner: " + this.inner + "]";
    }
}
