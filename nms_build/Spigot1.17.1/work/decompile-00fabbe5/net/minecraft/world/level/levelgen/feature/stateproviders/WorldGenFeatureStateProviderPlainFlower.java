package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderPlainFlower extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderPlainFlower> CODEC = Codec.unit(() -> {
        return WorldGenFeatureStateProviderPlainFlower.INSTANCE;
    });
    public static final WorldGenFeatureStateProviderPlainFlower INSTANCE = new WorldGenFeatureStateProviderPlainFlower();
    private static final IBlockData[] LOW_NOISE_FLOWERS = new IBlockData[]{Blocks.ORANGE_TULIP.getBlockData(), Blocks.RED_TULIP.getBlockData(), Blocks.PINK_TULIP.getBlockData(), Blocks.WHITE_TULIP.getBlockData()};
    private static final IBlockData[] HIGH_NOISE_FLOWERS = new IBlockData[]{Blocks.POPPY.getBlockData(), Blocks.AZURE_BLUET.getBlockData(), Blocks.OXEYE_DAISY.getBlockData(), Blocks.CORNFLOWER.getBlockData()};

    public WorldGenFeatureStateProviderPlainFlower() {}

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.PLAIN_FLOWER_PROVIDER;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        double d0 = BiomeBase.BIOME_INFO_NOISE.a((double) blockposition.getX() / 200.0D, (double) blockposition.getZ() / 200.0D, false);

        return d0 < -0.8D ? (IBlockData) SystemUtils.a((Object[]) WorldGenFeatureStateProviderPlainFlower.LOW_NOISE_FLOWERS, random) : (random.nextInt(3) > 0 ? (IBlockData) SystemUtils.a((Object[]) WorldGenFeatureStateProviderPlainFlower.HIGH_NOISE_FLOWERS, random) : Blocks.DANDELION.getBlockData());
    }
}
