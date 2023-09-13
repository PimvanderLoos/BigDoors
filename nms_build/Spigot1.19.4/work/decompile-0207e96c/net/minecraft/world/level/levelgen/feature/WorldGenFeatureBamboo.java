package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockBamboo;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyBambooSize;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;

public class WorldGenFeatureBamboo extends WorldGenerator<WorldGenFeatureConfigurationChance> {

    private static final IBlockData BAMBOO_TRUNK = (IBlockData) ((IBlockData) ((IBlockData) Blocks.BAMBOO.defaultBlockState().setValue(BlockBamboo.AGE, 1)).setValue(BlockBamboo.LEAVES, BlockPropertyBambooSize.NONE)).setValue(BlockBamboo.STAGE, 0);
    private static final IBlockData BAMBOO_FINAL_LARGE = (IBlockData) ((IBlockData) WorldGenFeatureBamboo.BAMBOO_TRUNK.setValue(BlockBamboo.LEAVES, BlockPropertyBambooSize.LARGE)).setValue(BlockBamboo.STAGE, 1);
    private static final IBlockData BAMBOO_TOP_LARGE = (IBlockData) WorldGenFeatureBamboo.BAMBOO_TRUNK.setValue(BlockBamboo.LEAVES, BlockPropertyBambooSize.LARGE);
    private static final IBlockData BAMBOO_TOP_SMALL = (IBlockData) WorldGenFeatureBamboo.BAMBOO_TRUNK.setValue(BlockBamboo.LEAVES, BlockPropertyBambooSize.SMALL);

    public WorldGenFeatureBamboo(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureConfigurationChance> featureplacecontext) {
        int i = 0;
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        RandomSource randomsource = featureplacecontext.random();
        WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance = (WorldGenFeatureConfigurationChance) featureplacecontext.config();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition.mutable();

        if (generatoraccessseed.isEmptyBlock(blockposition_mutableblockposition)) {
            if (Blocks.BAMBOO.defaultBlockState().canSurvive(generatoraccessseed, blockposition_mutableblockposition)) {
                int j = randomsource.nextInt(12) + 5;
                int k;

                if (randomsource.nextFloat() < worldgenfeatureconfigurationchance.probability) {
                    k = randomsource.nextInt(4) + 1;

                    for (int l = blockposition.getX() - k; l <= blockposition.getX() + k; ++l) {
                        for (int i1 = blockposition.getZ() - k; i1 <= blockposition.getZ() + k; ++i1) {
                            int j1 = l - blockposition.getX();
                            int k1 = i1 - blockposition.getZ();

                            if (j1 * j1 + k1 * k1 <= k * k) {
                                blockposition_mutableblockposition1.set(l, generatoraccessseed.getHeight(HeightMap.Type.WORLD_SURFACE, l, i1) - 1, i1);
                                if (isDirt(generatoraccessseed.getBlockState(blockposition_mutableblockposition1))) {
                                    generatoraccessseed.setBlock(blockposition_mutableblockposition1, Blocks.PODZOL.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
                }

                for (k = 0; k < j && generatoraccessseed.isEmptyBlock(blockposition_mutableblockposition); ++k) {
                    generatoraccessseed.setBlock(blockposition_mutableblockposition, WorldGenFeatureBamboo.BAMBOO_TRUNK, 2);
                    blockposition_mutableblockposition.move(EnumDirection.UP, 1);
                }

                if (blockposition_mutableblockposition.getY() - blockposition.getY() >= 3) {
                    generatoraccessseed.setBlock(blockposition_mutableblockposition, WorldGenFeatureBamboo.BAMBOO_FINAL_LARGE, 2);
                    generatoraccessseed.setBlock(blockposition_mutableblockposition.move(EnumDirection.DOWN, 1), WorldGenFeatureBamboo.BAMBOO_TOP_LARGE, 2);
                    generatoraccessseed.setBlock(blockposition_mutableblockposition.move(EnumDirection.DOWN, 1), WorldGenFeatureBamboo.BAMBOO_TOP_SMALL, 2);
                }
            }

            ++i;
        }

        return i > 0;
    }
}
