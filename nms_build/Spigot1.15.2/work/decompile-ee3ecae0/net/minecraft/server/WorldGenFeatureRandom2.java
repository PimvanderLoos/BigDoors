package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class WorldGenFeatureRandom2 implements WorldGenFeatureConfiguration {

    public final List<WorldGenFeatureConfigured<?, ?>> a;

    public WorldGenFeatureRandom2(List<WorldGenFeatureConfigured<?, ?>> list) {
        this.a = list;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("features"), dynamicops.createList(this.a.stream().map((worldgenfeatureconfigured) -> {
            return worldgenfeatureconfigured.a(dynamicops).getValue();
        })))));
    }

    public static <T> WorldGenFeatureRandom2 a(Dynamic<T> dynamic) {
        List<WorldGenFeatureConfigured<?, ?>> list = dynamic.get("features").asList(WorldGenFeatureConfigured::a);

        return new WorldGenFeatureRandom2(list);
    }
}
