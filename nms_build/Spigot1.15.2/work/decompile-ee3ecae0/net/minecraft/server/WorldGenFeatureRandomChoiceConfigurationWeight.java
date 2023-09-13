package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class WorldGenFeatureRandomChoiceConfigurationWeight<FC extends WorldGenFeatureConfiguration> {

    public final WorldGenFeatureConfigured<FC, ?> a;
    public final float b;

    public WorldGenFeatureRandomChoiceConfigurationWeight(WorldGenFeatureConfigured<FC, ?> worldgenfeatureconfigured, float f) {
        this.a = worldgenfeatureconfigured;
        this.b = f;
    }

    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("name"), dynamicops.createString(IRegistry.FEATURE.getKey(this.a.b).toString()), dynamicops.createString("config"), this.a.c.a(dynamicops).getValue(), dynamicops.createString("chance"), dynamicops.createFloat(this.b))));
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, BlockPosition blockposition) {
        return this.a.a(generatoraccess, chunkgenerator, random, blockposition);
    }

    public static <T> WorldGenFeatureRandomChoiceConfigurationWeight<?> a(Dynamic<T> dynamic) {
        return WorldGenFeatureConfigured.a(dynamic).a(dynamic.get("chance").asFloat(0.0F));
    }
}
