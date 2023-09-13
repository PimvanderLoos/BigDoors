package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;

public class WorldGenFeatureStateProviderPlainFlower extends WorldGenFeatureStateProvider {

    private static final IBlockData[] b = new IBlockData[]{Blocks.ORANGE_TULIP.getBlockData(), Blocks.RED_TULIP.getBlockData(), Blocks.PINK_TULIP.getBlockData(), Blocks.WHITE_TULIP.getBlockData()};
    private static final IBlockData[] c = new IBlockData[]{Blocks.POPPY.getBlockData(), Blocks.AZURE_BLUET.getBlockData(), Blocks.OXEYE_DAISY.getBlockData(), Blocks.CORNFLOWER.getBlockData()};

    public WorldGenFeatureStateProviderPlainFlower() {
        super(WorldGenFeatureStateProviders.c);
    }

    public <T> WorldGenFeatureStateProviderPlainFlower(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        double d0 = BiomeBase.e.a((double) blockposition.getX() / 200.0D, (double) blockposition.getZ() / 200.0D, false);

        return d0 < -0.8D ? WorldGenFeatureStateProviderPlainFlower.b[random.nextInt(WorldGenFeatureStateProviderPlainFlower.b.length)] : (random.nextInt(3) > 0 ? WorldGenFeatureStateProviderPlainFlower.c[random.nextInt(WorldGenFeatureStateProviderPlainFlower.c.length)] : Blocks.DANDELION.getBlockData());
    }

    @Override
    public <T> T a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("type"), dynamicops.createString(IRegistry.t.getKey(this.a).toString()));
        return (new Dynamic(dynamicops, dynamicops.createMap(builder.build()))).getValue();
    }
}
