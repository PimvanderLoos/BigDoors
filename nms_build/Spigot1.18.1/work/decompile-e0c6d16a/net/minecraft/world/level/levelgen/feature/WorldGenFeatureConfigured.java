package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeatureConfigured<FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> {

    public static final Codec<WorldGenFeatureConfigured<?, ?>> DIRECT_CODEC = IRegistry.FEATURE.byNameCodec().dispatch((worldgenfeatureconfigured) -> {
        return worldgenfeatureconfigured.feature;
    }, WorldGenerator::configuredCodec);
    public static final Codec<Supplier<WorldGenFeatureConfigured<?, ?>>> CODEC = RegistryFileCodec.create(IRegistry.CONFIGURED_FEATURE_REGISTRY, WorldGenFeatureConfigured.DIRECT_CODEC);
    public static final Codec<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> LIST_CODEC = RegistryFileCodec.homogeneousList(IRegistry.CONFIGURED_FEATURE_REGISTRY, WorldGenFeatureConfigured.DIRECT_CODEC);
    public static final Logger LOGGER = LogManager.getLogger();
    public final F feature;
    public final FC config;

    public WorldGenFeatureConfigured(F f0, FC fc) {
        this.feature = f0;
        this.config = fc;
    }

    public F feature() {
        return this.feature;
    }

    public FC config() {
        return this.config;
    }

    public PlacedFeature placed(List<PlacementModifier> list) {
        return new PlacedFeature(() -> {
            return this;
        }, list);
    }

    public PlacedFeature placed(PlacementModifier... aplacementmodifier) {
        return this.placed(List.of(aplacementmodifier));
    }

    public PlacedFeature filteredByBlockSurvival(Block block) {
        return this.filtered(BlockPredicate.wouldSurvive(block.defaultBlockState(), BlockPosition.ZERO));
    }

    public PlacedFeature onlyWhenEmpty() {
        return this.filtered(BlockPredicate.matchesBlock(Blocks.AIR, BlockPosition.ZERO));
    }

    public PlacedFeature filtered(BlockPredicate blockpredicate) {
        return this.placed(BlockPredicateFilter.forPredicate(blockpredicate));
    }

    public boolean place(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return generatoraccessseed.ensureCanWrite(blockposition) ? this.feature.place(new FeaturePlaceContext<>(Optional.empty(), generatoraccessseed, chunkgenerator, random, blockposition, this.config)) : false;
    }

    public Stream<WorldGenFeatureConfigured<?, ?>> getFeatures() {
        return Stream.concat(Stream.of(this), this.config.getFeatures());
    }

    public String toString() {
        return (String) RegistryGeneration.CONFIGURED_FEATURE.getResourceKey(this).map(Objects::toString).orElseGet(() -> {
            return WorldGenFeatureConfigured.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, this).toString();
        });
    }
}
