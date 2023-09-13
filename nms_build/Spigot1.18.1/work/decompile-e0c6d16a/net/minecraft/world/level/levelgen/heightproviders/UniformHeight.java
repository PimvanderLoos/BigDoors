package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UniformHeight extends HeightProvider {

    public static final Codec<UniformHeight> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter((uniformheight) -> {
            return uniformheight.minInclusive;
        }), VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter((uniformheight) -> {
            return uniformheight.maxInclusive;
        })).apply(instance, UniformHeight::new);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    private final LongSet warnedFor = new LongOpenHashSet();

    private UniformHeight(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1) {
        this.minInclusive = verticalanchor;
        this.maxInclusive = verticalanchor1;
    }

    public static UniformHeight of(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1) {
        return new UniformHeight(verticalanchor, verticalanchor1);
    }

    @Override
    public int sample(Random random, WorldGenerationContext worldgenerationcontext) {
        int i = this.minInclusive.resolveY(worldgenerationcontext);
        int j = this.maxInclusive.resolveY(worldgenerationcontext);

        if (i > j) {
            if (this.warnedFor.add((long) i << 32 | (long) j)) {
                UniformHeight.LOGGER.warn("Empty height range: {}", this);
            }

            return i;
        } else {
            return MathHelper.randomBetweenInclusive(random, i, j);
        }
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.UNIFORM;
    }

    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}
