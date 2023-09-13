package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class PlacedFeature {

    public static final Codec<PlacedFeature> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.CODEC.fieldOf("feature").forGetter((placedfeature) -> {
            return placedfeature.feature;
        }), PlacementModifier.CODEC.listOf().fieldOf("placement").forGetter((placedfeature) -> {
            return placedfeature.placement;
        })).apply(instance, PlacedFeature::new);
    });
    public static final Codec<Supplier<PlacedFeature>> CODEC = RegistryFileCodec.create(IRegistry.PLACED_FEATURE_REGISTRY, PlacedFeature.DIRECT_CODEC);
    public static final Codec<List<Supplier<PlacedFeature>>> LIST_CODEC = RegistryFileCodec.homogeneousList(IRegistry.PLACED_FEATURE_REGISTRY, PlacedFeature.DIRECT_CODEC);
    private final Supplier<WorldGenFeatureConfigured<?, ?>> feature;
    private final List<PlacementModifier> placement;

    public PlacedFeature(Supplier<WorldGenFeatureConfigured<?, ?>> supplier, List<PlacementModifier> list) {
        this.feature = supplier;
        this.placement = list;
    }

    public boolean place(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return this.placeWithContext(new PlacementContext(generatoraccessseed, chunkgenerator, Optional.empty()), random, blockposition);
    }

    public boolean placeWithBiomeCheck(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return this.placeWithContext(new PlacementContext(generatoraccessseed, chunkgenerator, Optional.of(this)), random, blockposition);
    }

    private boolean placeWithContext(PlacementContext placementcontext, Random random, BlockPosition blockposition) {
        Stream<BlockPosition> stream = Stream.of(blockposition);

        PlacementModifier placementmodifier;

        for (Iterator iterator = this.placement.iterator(); iterator.hasNext();stream = stream.flatMap((blockposition1) -> {
            return placementmodifier.getPositions(placementcontext, random, blockposition1);
        })) {
            placementmodifier = (PlacementModifier) iterator.next();
        }

        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) this.feature.get();
        MutableBoolean mutableboolean = new MutableBoolean();

        stream.forEach((blockposition1) -> {
            if (worldgenfeatureconfigured.place(placementcontext.getLevel(), placementcontext.generator(), random, blockposition1)) {
                mutableboolean.setTrue();
            }

        });
        return mutableboolean.isTrue();
    }

    public Stream<WorldGenFeatureConfigured<?, ?>> getFeatures() {
        return ((WorldGenFeatureConfigured) this.feature.get()).getFeatures();
    }

    @VisibleForDebug
    public List<PlacementModifier> getPlacement() {
        return this.placement;
    }

    public String toString() {
        IRegistry iregistry = IRegistry.FEATURE;

        return "Placed " + iregistry.getKey(((WorldGenFeatureConfigured) this.feature.get()).feature());
    }
}
