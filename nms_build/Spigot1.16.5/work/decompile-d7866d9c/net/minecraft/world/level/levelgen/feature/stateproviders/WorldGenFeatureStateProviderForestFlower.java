package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureStateProviderForestFlower extends WorldGenFeatureStateProvider {

    public static final Codec<WorldGenFeatureStateProviderForestFlower> b = Codec.unit(() -> {
        return WorldGenFeatureStateProviderForestFlower.c;
    });
    private static final IBlockData[] d = new IBlockData[]{Blocks.DANDELION.getBlockData(), Blocks.POPPY.getBlockData(), Blocks.ALLIUM.getBlockData(), Blocks.AZURE_BLUET.getBlockData(), Blocks.RED_TULIP.getBlockData(), Blocks.ORANGE_TULIP.getBlockData(), Blocks.WHITE_TULIP.getBlockData(), Blocks.PINK_TULIP.getBlockData(), Blocks.OXEYE_DAISY.getBlockData(), Blocks.CORNFLOWER.getBlockData(), Blocks.LILY_OF_THE_VALLEY.getBlockData()};
    public static final WorldGenFeatureStateProviderForestFlower c = new WorldGenFeatureStateProviderForestFlower();

    public WorldGenFeatureStateProviderForestFlower() {}

    @Override
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.d;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        double d0 = MathHelper.a((1.0D + BiomeBase.f.a((double) blockposition.getX() / 48.0D, (double) blockposition.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);

        return WorldGenFeatureStateProviderForestFlower.d[(int) (d0 * (double) WorldGenFeatureStateProviderForestFlower.d.length)];
    }
}
