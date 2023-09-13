package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class WorldGenFeatureRandomChoiceConfiguration implements WorldGenFeatureConfiguration {

    public final List<WorldGenFeatureRandomChoiceConfigurationWeight<?>> a;
    public final WorldGenFeatureConfigured<?, ?> b;

    public WorldGenFeatureRandomChoiceConfiguration(List<WorldGenFeatureRandomChoiceConfigurationWeight<?>> list, WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
        this.a = list;
        this.b = worldgenfeatureconfigured;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        T t0 = dynamicops.createList(this.a.stream().map((worldgenfeaturerandomchoiceconfigurationweight) -> {
            return worldgenfeaturerandomchoiceconfigurationweight.a(dynamicops).getValue();
        }));
        T t1 = this.b.a(dynamicops).getValue();

        return new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("features"), t0, dynamicops.createString("default"), t1)));
    }

    public static <T> WorldGenFeatureRandomChoiceConfiguration a(Dynamic<T> dynamic) {
        List<WorldGenFeatureRandomChoiceConfigurationWeight<?>> list = dynamic.get("features").asList(WorldGenFeatureRandomChoiceConfigurationWeight::a);
        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = WorldGenFeatureConfigured.a(dynamic.get("default").orElseEmptyMap());

        return new WorldGenFeatureRandomChoiceConfiguration(list, worldgenfeatureconfigured);
    }
}
