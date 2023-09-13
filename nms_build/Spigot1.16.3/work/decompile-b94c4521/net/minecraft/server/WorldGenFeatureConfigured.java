package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeatureConfigured<FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> implements IDecoratable<WorldGenFeatureConfigured<?, ?>> {

    public static final Codec<WorldGenFeatureConfigured<?, ?>> a = IRegistry.FEATURE.dispatch((worldgenfeatureconfigured) -> {
        return worldgenfeatureconfigured.e;
    }, WorldGenerator::a);
    public static final Codec<Supplier<WorldGenFeatureConfigured<?, ?>>> b = RegistryFileCodec.a(IRegistry.au, WorldGenFeatureConfigured.a);
    public static final Codec<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> c = RegistryFileCodec.b(IRegistry.au, WorldGenFeatureConfigured.a);
    public static final Logger LOGGER = LogManager.getLogger();
    public final F e;
    public final FC f;

    public WorldGenFeatureConfigured(F f0, FC fc) {
        this.e = f0;
        this.f = fc;
    }

    public F b() {
        return this.e;
    }

    public FC c() {
        return this.f;
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
        return this.e.generate(generatoraccessseed, chunkgenerator, random, blockposition, this.f);
    }

    public Stream<WorldGenFeatureConfigured<?, ?>> d() {
        return Stream.concat(Stream.of(this), this.f.an_());
    }
}
