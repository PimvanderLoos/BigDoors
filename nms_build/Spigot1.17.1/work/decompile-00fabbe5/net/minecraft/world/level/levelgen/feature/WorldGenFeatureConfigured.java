package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.IDecoratable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCompositeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.WorldGenDecoratorConfigured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeatureConfigured<FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> implements IDecoratable<WorldGenFeatureConfigured<?, ?>> {

    public static final Codec<WorldGenFeatureConfigured<?, ?>> DIRECT_CODEC = IRegistry.FEATURE.dispatch((worldgenfeatureconfigured) -> {
        return worldgenfeatureconfigured.feature;
    }, WorldGenerator::a);
    public static final Codec<Supplier<WorldGenFeatureConfigured<?, ?>>> CODEC = RegistryFileCodec.a(IRegistry.CONFIGURED_FEATURE_REGISTRY, WorldGenFeatureConfigured.DIRECT_CODEC);
    public static final Codec<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> LIST_CODEC = RegistryFileCodec.b(IRegistry.CONFIGURED_FEATURE_REGISTRY, WorldGenFeatureConfigured.DIRECT_CODEC);
    public static final Logger LOGGER = LogManager.getLogger();
    public final F feature;
    public final FC config;

    public WorldGenFeatureConfigured(F f0, FC fc) {
        this.feature = f0;
        this.config = fc;
    }

    public F b() {
        return this.feature;
    }

    public FC c() {
        return this.config;
    }

    @Override
    public WorldGenFeatureConfigured<?, ?> a(WorldGenDecoratorConfigured<?> worldgendecoratorconfigured) {
        return WorldGenerator.DECORATED.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCompositeConfiguration(() -> {
            return this;
        }, worldgendecoratorconfigured)));
    }

    public WorldGenFeatureRandomChoiceConfigurationWeight a(float f) {
        return new WorldGenFeatureRandomChoiceConfigurationWeight(this, f);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return this.feature.generate(new FeaturePlaceContext<>(generatoraccessseed, chunkgenerator, random, blockposition, this.config));
    }

    public Stream<WorldGenFeatureConfigured<?, ?>> d() {
        return Stream.concat(Stream.of(this), this.config.ab_());
    }

    public String toString() {
        return (String) RegistryGeneration.CONFIGURED_FEATURE.c((Object) this).map(Objects::toString).orElseGet(() -> {
            return WorldGenFeatureConfigured.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, this).toString();
        });
    }
}
