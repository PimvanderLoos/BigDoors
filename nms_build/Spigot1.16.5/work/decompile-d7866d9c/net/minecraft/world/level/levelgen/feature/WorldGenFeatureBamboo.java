package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockBamboo;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyBambooSize;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;

public class WorldGenFeatureBamboo extends WorldGenerator<WorldGenFeatureConfigurationChance> {

    private static final IBlockData a = (IBlockData) ((IBlockData) ((IBlockData) Blocks.BAMBOO.getBlockData().set(BlockBamboo.d, 1)).set(BlockBamboo.e, BlockPropertyBambooSize.NONE)).set(BlockBamboo.f, 0);
    private static final IBlockData ab = (IBlockData) ((IBlockData) WorldGenFeatureBamboo.a.set(BlockBamboo.e, BlockPropertyBambooSize.LARGE)).set(BlockBamboo.f, 1);
    private static final IBlockData ac = (IBlockData) WorldGenFeatureBamboo.a.set(BlockBamboo.e, BlockPropertyBambooSize.LARGE);
    private static final IBlockData ad = (IBlockData) WorldGenFeatureBamboo.a.set(BlockBamboo.e, BlockPropertyBambooSize.SMALL);

    public WorldGenFeatureBamboo(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance) {
        int i = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition.i();

        if (generatoraccessseed.isEmpty(blockposition_mutableblockposition)) {
            if (Blocks.BAMBOO.getBlockData().canPlace(generatoraccessseed, blockposition_mutableblockposition)) {
                int j = random.nextInt(12) + 5;
                int k;

                if (random.nextFloat() < worldgenfeatureconfigurationchance.c) {
                    k = random.nextInt(4) + 1;

                    for (int l = blockposition.getX() - k; l <= blockposition.getX() + k; ++l) {
                        for (int i1 = blockposition.getZ() - k; i1 <= blockposition.getZ() + k; ++i1) {
                            int j1 = l - blockposition.getX();
                            int k1 = i1 - blockposition.getZ();

                            if (j1 * j1 + k1 * k1 <= k * k) {
                                blockposition_mutableblockposition1.d(l, generatoraccessseed.a(HeightMap.Type.WORLD_SURFACE, l, i1) - 1, i1);
                                if (b(generatoraccessseed.getType(blockposition_mutableblockposition1).getBlock())) {
                                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition1, Blocks.PODZOL.getBlockData(), 2);
                                }
                            }
                        }
                    }
                }

                for (k = 0; k < j && generatoraccessseed.isEmpty(blockposition_mutableblockposition); ++k) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, WorldGenFeatureBamboo.a, 2);
                    blockposition_mutableblockposition.c(EnumDirection.UP, 1);
                }

                if (blockposition_mutableblockposition.getY() - blockposition.getY() >= 3) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, WorldGenFeatureBamboo.ab, 2);
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition.c(EnumDirection.DOWN, 1), WorldGenFeatureBamboo.ac, 2);
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition.c(EnumDirection.DOWN, 1), WorldGenFeatureBamboo.ad, 2);
                }
            }

            ++i;
        }

        return i > 0;
    }
}
