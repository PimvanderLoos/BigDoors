package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderPlainFlower extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderPlainFlower> b = Codec.unit(() -> {
        return WorldGenFeatureStateProviderPlainFlower.c;
    });
    public static final WorldGenFeatureStateProviderPlainFlower c = new WorldGenFeatureStateProviderPlainFlower();
    private static final IBlockData[] d = new IBlockData[]{Blocks.ORANGE_TULIP.getBlockData(), Blocks.RED_TULIP.getBlockData(), Blocks.PINK_TULIP.getBlockData(), Blocks.WHITE_TULIP.getBlockData()};
    private static final IBlockData[] e = new IBlockData[]{Blocks.POPPY.getBlockData(), Blocks.AZURE_BLUET.getBlockData(), Blocks.OXEYE_DAISY.getBlockData(), Blocks.CORNFLOWER.getBlockData()};

    public WorldGenFeatureStateProviderPlainFlower() {}

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.c;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        double d0 = BiomeBase.f.a((double) blockposition.getX() / 200.0D, (double) blockposition.getZ() / 200.0D, false);

        return d0 < -0.8D ? (IBlockData) SystemUtils.a((Object[]) WorldGenFeatureStateProviderPlainFlower.d, random) : (random.nextInt(3) > 0 ? (IBlockData) SystemUtils.a((Object[]) WorldGenFeatureStateProviderPlainFlower.e, random) : Blocks.DANDELION.getBlockData());
    }
}
