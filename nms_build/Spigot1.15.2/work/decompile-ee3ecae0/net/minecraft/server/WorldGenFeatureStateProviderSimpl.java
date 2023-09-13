package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class WorldGenFeatureStateProviderSimpl extends WorldGenFeatureStateProvider {

    private final IBlockData b;

    public WorldGenFeatureStateProviderSimpl(IBlockData iblockdata) {
        super(WorldGenFeatureStateProviders.a);
        this.b = iblockdata;
    }

    public <T> WorldGenFeatureStateProviderSimpl(Dynamic<T> dynamic) {
        this(IBlockData.a(dynamic.get("state").orElseEmptyMap()));
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        return this.b;
    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("type"), dynamicops.createString(IRegistry.t.getKey(this.a).toString())).put(dynamicops.createString("state"), IBlockData.a(dynamicops, this.b).getValue());
        return (new Dynamic(dynamicops, dynamicops.createMap(builder.build()))).getValue();
    }
}
