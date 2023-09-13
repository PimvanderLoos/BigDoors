package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeatureConfigured<FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> {

    public static final Logger LOGGER = LogManager.getLogger();
    public final F b;
    public final FC c;

    public WorldGenFeatureConfigured(F f0, FC fc) {
        this.b = f0;
        this.c = fc;
    }

    public WorldGenFeatureConfigured(F f0, Dynamic<?> dynamic) {
        this(f0, f0.b(dynamic));
    }

    public WorldGenFeatureConfigured<?, ?> a(WorldGenDecoratorConfigured<?> worldgendecoratorconfigured) {
        WorldGenerator<WorldGenFeatureCompositeConfiguration> worldgenerator = this.b instanceof WorldGenFlowers ? WorldGenerator.DECORATED_FLOWER : WorldGenerator.DECORATED;

        return worldgenerator.b((WorldGenFeatureConfiguration) (new WorldGenFeatureCompositeConfiguration(this, worldgendecoratorconfigured)));
    }

    public WorldGenFeatureRandomChoiceConfigurationWeight<FC> a(float f) {
        return new WorldGenFeatureRandomChoiceConfigurationWeight<>(this, f);
    }

    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("name"), dynamicops.createString(IRegistry.FEATURE.getKey(this.b).toString()), dynamicops.createString("config"), this.c.a(dynamicops).getValue())));
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, BlockPosition blockposition) {
        return this.b.generate(generatoraccess, chunkgenerator, random, blockposition, this.c);
    }

    public static <T> WorldGenFeatureConfigured<?, ?> a(Dynamic<T> dynamic) {
        String s = dynamic.get("name").asString("");
        WorldGenerator worldgenerator = (WorldGenerator) IRegistry.FEATURE.get(new MinecraftKey(s));

        try {
            return new WorldGenFeatureConfigured<>(worldgenerator, dynamic.get("config").orElseEmptyMap());
        } catch (RuntimeException runtimeexception) {
            WorldGenFeatureConfigured.LOGGER.warn("Error while deserializing {}", s);
            return new WorldGenFeatureConfigured<>(WorldGenerator.NO_OP, WorldGenFeatureEmptyConfiguration.e);
        }
    }
}
