package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface WorldGenFeatureDecoratorConfiguration {

    WorldGenFeatureEmptyConfiguration2 e = new WorldGenFeatureEmptyConfiguration2();

    <T> Dynamic<T> a(DynamicOps<T> dynamicops);
}
