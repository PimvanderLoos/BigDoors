package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WeightedPlacedFeature {

    public static final Codec<WeightedPlacedFeature> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(PlacedFeature.CODEC.fieldOf("feature").forGetter((weightedplacedfeature) -> {
            return weightedplacedfeature.feature;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter((weightedplacedfeature) -> {
            return weightedplacedfeature.chance;
        })).apply(instance, WeightedPlacedFeature::new);
    });
    public final Holder<PlacedFeature> feature;
    public final float chance;

    public WeightedPlacedFeature(Holder<PlacedFeature> holder, float f) {
        this.feature = holder;
        this.chance = f;
    }

    public boolean place(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, RandomSource randomsource, BlockPosition blockposition) {
        return ((PlacedFeature) this.feature.value()).place(generatoraccessseed, chunkgenerator, randomsource, blockposition);
    }
}
