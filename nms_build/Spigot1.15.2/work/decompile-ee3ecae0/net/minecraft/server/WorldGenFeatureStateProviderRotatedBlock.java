package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class WorldGenFeatureStateProviderRotatedBlock extends WorldGenFeatureStateProvider {

    private final Block b;

    public WorldGenFeatureStateProviderRotatedBlock(Block block) {
        super(WorldGenFeatureStateProviders.a);
        this.b = block;
    }

    public <T> WorldGenFeatureStateProviderRotatedBlock(Dynamic<T> dynamic) {
        this(IBlockData.a(dynamic.get("state").orElseEmptyMap()).getBlock());
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        EnumDirection.EnumAxis enumdirection_enumaxis = EnumDirection.EnumAxis.a(random);

        return (IBlockData) this.b.getBlockData().set(BlockRotatable.AXIS, enumdirection_enumaxis);
    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("type"), dynamicops.createString(IRegistry.t.getKey(this.a).toString())).put(dynamicops.createString("state"), IBlockData.a(dynamicops, this.b.getBlockData()).getValue());
        return (new Dynamic(dynamicops, dynamicops.createMap(builder.build()))).getValue();
    }
}
