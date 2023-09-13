package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class WorldGenFeatureMushroomConfiguration implements WorldGenFeatureConfiguration {

    public final WorldGenFeatureStateProvider a;
    public final WorldGenFeatureStateProvider b;
    public final int c;

    public WorldGenFeatureMushroomConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, int i) {
        this.a = worldgenfeaturestateprovider;
        this.b = worldgenfeaturestateprovider1;
        this.c = i;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("cap_provider"), this.a.a(dynamicops)).put(dynamicops.createString("stem_provider"), this.b.a(dynamicops)).put(dynamicops.createString("foliage_radius"), dynamicops.createInt(this.c));
        return new Dynamic(dynamicops, dynamicops.createMap(builder.build()));
    }

    public static <T> WorldGenFeatureMushroomConfiguration a(Dynamic<T> dynamic) {
        WorldGenFeatureStateProviders<?> worldgenfeaturestateproviders = (WorldGenFeatureStateProviders) IRegistry.t.get(new MinecraftKey((String) dynamic.get("cap_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        WorldGenFeatureStateProviders<?> worldgenfeaturestateproviders1 = (WorldGenFeatureStateProviders) IRegistry.t.get(new MinecraftKey((String) dynamic.get("stem_provider").get("type").asString().orElseThrow(RuntimeException::new)));

        return new WorldGenFeatureMushroomConfiguration(worldgenfeaturestateproviders.a(dynamic.get("cap_provider").orElseEmptyMap()), worldgenfeaturestateproviders1.a(dynamic.get("stem_provider").orElseEmptyMap()), dynamic.get("foliage_radius").asInt(2));
    }
}
