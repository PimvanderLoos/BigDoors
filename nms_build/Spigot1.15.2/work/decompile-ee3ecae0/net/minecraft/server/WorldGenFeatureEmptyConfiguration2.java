package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class WorldGenFeatureEmptyConfiguration2 implements WorldGenFeatureDecoratorConfiguration {

    public WorldGenFeatureEmptyConfiguration2() {}

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        return new Dynamic(dynamicops, dynamicops.emptyMap());
    }

    public static WorldGenFeatureEmptyConfiguration2 a(Dynamic<?> dynamic) {
        return new WorldGenFeatureEmptyConfiguration2();
    }
}
