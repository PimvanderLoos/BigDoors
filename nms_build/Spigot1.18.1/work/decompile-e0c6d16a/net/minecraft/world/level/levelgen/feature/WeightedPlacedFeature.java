package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WeightedPlacedFeature {

    public static final Codec<WeightedPlacedFeature> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(PlacedFeature.CODEC.fieldOf("feature").flatXmap(ExtraCodecs.nonNullSupplierCheck(), ExtraCodecs.nonNullSupplierCheck()).forGetter((weightedplacedfeature) -> {
            return weightedplacedfeature.feature;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter((weightedplacedfeature) -> {
            return weightedplacedfeature.chance;
        })).apply(instance, WeightedPlacedFeature::new);
    });
    public final Supplier<PlacedFeature> feature;
    public final float chance;

    public WeightedPlacedFeature(PlacedFeature placedfeature, float f) {
        this(() -> {
            return placedfeature;
        }, f);
    }

    private WeightedPlacedFeature(Supplier<PlacedFeature> supplier, float f) {
        this.feature = supplier;
        this.chance = f;
    }

    public boolean place(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return ((PlacedFeature) this.feature.get()).place(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}
