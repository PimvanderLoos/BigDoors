package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrapezoidHeight extends HeightProvider {

    public static final Codec<TrapezoidHeight> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter((trapezoidheight) -> {
            return trapezoidheight.minInclusive;
        }), VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter((trapezoidheight) -> {
            return trapezoidheight.maxInclusive;
        }), Codec.INT.optionalFieldOf("plateau", 0).forGetter((trapezoidheight) -> {
            return trapezoidheight.plateau;
        })).apply(instance, TrapezoidHeight::new);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    private final int plateau;

    private TrapezoidHeight(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1, int i) {
        this.minInclusive = verticalanchor;
        this.maxInclusive = verticalanchor1;
        this.plateau = i;
    }

    public static TrapezoidHeight of(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1, int i) {
        return new TrapezoidHeight(verticalanchor, verticalanchor1, i);
    }

    public static TrapezoidHeight of(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1) {
        return of(verticalanchor, verticalanchor1, 0);
    }

    @Override
    public int sample(Random random, WorldGenerationContext worldgenerationcontext) {
        int i = this.minInclusive.resolveY(worldgenerationcontext);
        int j = this.maxInclusive.resolveY(worldgenerationcontext);

        if (i > j) {
            TrapezoidHeight.LOGGER.warn("Empty height range: {}", this);
            return i;
        } else {
            int k = j - i;

            if (this.plateau >= k) {
                return MathHelper.randomBetweenInclusive(random, i, j);
            } else {
                int l = (k - this.plateau) / 2;
                int i1 = k - l;

                return i + MathHelper.randomBetweenInclusive(random, 0, i1) + MathHelper.randomBetweenInclusive(random, 0, l);
            }
        }
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.TRAPEZOID;
    }

    public String toString() {
        return this.plateau == 0 ? "triangle (" + this.minInclusive + "-" + this.maxInclusive + ")" : "trapezoid(" + this.plateau + ") in [" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}
