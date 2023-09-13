package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeatureConfigured<FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> {

    public static final WorldGenFeatureConfigured<?, ?> a = new WorldGenFeatureConfigured<>(WorldGenerator.NO_OP, WorldGenFeatureEmptyConfiguration.k);
    public static final Codec<WorldGenFeatureConfigured<?, ?>> b = IRegistry.FEATURE.dispatch("name", (worldgenfeatureconfigured) -> {
        return worldgenfeatureconfigured.d;
    }, WorldGenerator::a).withDefault(WorldGenFeatureConfigured.a);
    public static final Logger LOGGER = LogManager.getLogger();
    public final F d;
    public final FC e;

    public WorldGenFeatureConfigured(F f0, FC fc) {
        this.d = f0;
        this.e = fc;
    }

    public WorldGenFeatureConfigured<?, ?> a(WorldGenDecoratorConfigured<?> worldgendecoratorconfigured) {
        WorldGenerator<WorldGenFeatureCompositeConfiguration> worldgenerator = this.d instanceof WorldGenFlowers ? WorldGenerator.DECORATED_FLOWER : WorldGenerator.DECORATED;

        return worldgenerator.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCompositeConfiguration(this, worldgendecoratorconfigured)));
    }

    public WorldGenFeatureRandomChoiceConfigurationWeight<FC> a(float f) {
        return new WorldGenFeatureRandomChoiceConfigurationWeight<>(this, f);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return this.d.generate(generatoraccessseed, structuremanager, chunkgenerator, random, blockposition, this.e);
    }
}
