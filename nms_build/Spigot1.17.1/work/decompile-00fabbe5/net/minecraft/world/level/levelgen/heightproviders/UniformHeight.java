package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    private UniformHeight(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1) {
        this.minInclusive = verticalanchor;
        this.maxInclusive = verticalanchor1;
    }

    public static UniformHeight a(VerticalAnchor verticalanchor, VerticalAnchor verticalanchor1) {
        return new UniformHeight(verticalanchor, verticalanchor1);
    }

    @Override
    public int a(Random random, WorldGenerationContext worldgenerationcontext) {
        int i = this.minInclusive.a(worldgenerationcontext);
        int j = this.maxInclusive.a(worldgenerationcontext);

        if (i > j) {
            UniformHeight.LOGGER.warn("Empty height range: {}", this);
            return i;
        } else {
            return MathHelper.b(random, i, j);
        }
    }

    @Override
    public HeightProviderType<?> a() {
        return HeightProviderType.UNIFORM;
    }

    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}
