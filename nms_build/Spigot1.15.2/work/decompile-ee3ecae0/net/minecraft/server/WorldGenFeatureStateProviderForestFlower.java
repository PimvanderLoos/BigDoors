package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class WorldGenFeatureStateProviderForestFlower extends WorldGenFeatureStateProvider {

    private static final IBlockData[] b = new IBlockData[]{Blocks.DANDELION.getBlockData(), Blocks.POPPY.getBlockData(), Blocks.ALLIUM.getBlockData(), Blocks.AZURE_BLUET.getBlockData(), Blocks.RED_TULIP.getBlockData(), Blocks.ORANGE_TULIP.getBlockData(), Blocks.WHITE_TULIP.getBlockData(), Blocks.PINK_TULIP.getBlockData(), Blocks.OXEYE_DAISY.getBlockData(), Blocks.CORNFLOWER.getBlockData(), Blocks.LILY_OF_THE_VALLEY.getBlockData()};

    public WorldGenFeatureStateProviderForestFlower() {
        super(WorldGenFeatureStateProviders.d);
    }

    public <T> WorldGenFeatureStateProviderForestFlower(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        double d0 = MathHelper.a((1.0D + BiomeBase.e.a((double) blockposition.getX() / 48.0D, (double) blockposition.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);

        return WorldGenFeatureStateProviderForestFlower.b[(int) (d0 * (double) WorldGenFeatureStateProviderForestFlower.b.length)];
    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("type"), dynamicops.createString(IRegistry.t.getKey(this.a).toString()));
        return (new Dynamic(dynamicops, dynamicops.createMap(builder.build()))).getValue();
    }
}
