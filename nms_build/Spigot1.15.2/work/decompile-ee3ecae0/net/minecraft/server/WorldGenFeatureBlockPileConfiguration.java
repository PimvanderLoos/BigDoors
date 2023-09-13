package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class WorldGenFeatureBlockPileConfiguration implements WorldGenFeatureConfiguration {

    public final WorldGenFeatureStateProvider a;

    public WorldGenFeatureBlockPileConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        this.a = worldgenfeaturestateprovider;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("state_provider"), this.a.a(dynamicops));
        return new Dynamic(dynamicops, dynamicops.createMap(builder.build()));
    }

    public static <T> WorldGenFeatureBlockPileConfiguration a(Dynamic<T> dynamic) {
        WorldGenFeatureStateProviders<?> worldgenfeaturestateproviders = (WorldGenFeatureStateProviders) IRegistry.t.get(new MinecraftKey((String) dynamic.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));

        return new WorldGenFeatureBlockPileConfiguration(worldgenfeaturestateproviders.a(dynamic.get("state_provider").orElseEmptyMap()));
    }
}
