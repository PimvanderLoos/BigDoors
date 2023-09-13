package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import org.apache.commons.lang3.mutable.MutableBoolean;

public record PlacedFeature(Holder<WorldGenFeatureConfigured<?, ?>> feature, List<PlacementModifier> placement) {

    public static final Codec<PlacedFeature> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.CODEC.fieldOf("feature").forGetter((placedfeature) -> {
            return placedfeature.feature;
        }), PlacementModifier.CODEC.listOf().fieldOf("placement").forGetter((placedfeature) -> {
            return placedfeature.placement;
        })).apply(instance, PlacedFeature::new);
    });
    public static final Codec<Holder<PlacedFeature>> CODEC = RegistryFileCodec.create(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC);
    public static final Codec<HolderSet<PlacedFeature>> LIST_CODEC = RegistryCodecs.homogeneousList(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC);
    public static final Codec<List<HolderSet<PlacedFeature>>> LIST_OF_LISTS_CODEC = RegistryCodecs.homogeneousList(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC, true).listOf();

    public boolean place(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, RandomSource randomsource, BlockPosition blockposition) {
        return this.placeWithContext(new PlacementContext(generatoraccessseed, chunkgenerator, Optional.empty()), randomsource, blockposition);
    }

    public boolean placeWithBiomeCheck(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, RandomSource randomsource, BlockPosition blockposition) {
        return this.placeWithContext(new PlacementContext(generatoraccessseed, chunkgenerator, Optional.of(this)), randomsource, blockposition);
    }

    private boolean placeWithContext(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        Stream<BlockPosition> stream = Stream.of(blockposition);

        PlacementModifier placementmodifier;

        for (Iterator iterator = this.placement.iterator(); iterator.hasNext();stream = stream.flatMap((blockposition1) -> {
            return placementmodifier.getPositions(placementcontext, randomsource, blockposition1);
        })) {
            placementmodifier = (PlacementModifier) iterator.next();
        }

        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) this.feature.value();
        MutableBoolean mutableboolean = new MutableBoolean();

        stream.forEach((blockposition1) -> {
            if (worldgenfeatureconfigured.place(placementcontext.getLevel(), placementcontext.generator(), randomsource, blockposition1)) {
                mutableboolean.setTrue();
            }

        });
        return mutableboolean.isTrue();
    }

    public Stream<WorldGenFeatureConfigured<?, ?>> getFeatures() {
        return ((WorldGenFeatureConfigured) this.feature.value()).getFeatures();
    }

    public String toString() {
        return "Placed " + this.feature;
    }
}
