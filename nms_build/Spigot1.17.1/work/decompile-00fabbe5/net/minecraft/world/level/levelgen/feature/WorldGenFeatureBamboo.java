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
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;

public class WorldGenFeatureBamboo extends WorldGenerator<WorldGenFeatureConfigurationChance> {

    private static final IBlockData BAMBOO_TRUNK = (IBlockData) ((IBlockData) ((IBlockData) Blocks.BAMBOO.getBlockData().set(BlockBamboo.AGE, 1)).set(BlockBamboo.LEAVES, BlockPropertyBambooSize.NONE)).set(BlockBamboo.STAGE, 0);
    private static final IBlockData BAMBOO_FINAL_LARGE = (IBlockData) ((IBlockData) WorldGenFeatureBamboo.BAMBOO_TRUNK.set(BlockBamboo.LEAVES, BlockPropertyBambooSize.LARGE)).set(BlockBamboo.STAGE, 1);
    private static final IBlockData BAMBOO_TOP_LARGE = (IBlockData) WorldGenFeatureBamboo.BAMBOO_TRUNK.set(BlockBamboo.LEAVES, BlockPropertyBambooSize.LARGE);
    private static final IBlockData BAMBOO_TOP_SMALL = (IBlockData) WorldGenFeatureBamboo.BAMBOO_TRUNK.set(BlockBamboo.LEAVES, BlockPropertyBambooSize.SMALL);

    public WorldGenFeatureBamboo(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureConfigurationChance> featureplacecontext) {
        int i = 0;
        BlockPosition blockposition = featureplacecontext.d();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        Random random = featureplacecontext.c();
        WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance = (WorldGenFeatureConfigurationChance) featureplacecontext.e();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition.i();

        if (generatoraccessseed.isEmpty(blockposition_mutableblockposition)) {
            if (Blocks.BAMBOO.getBlockData().canPlace(generatoraccessseed, blockposition_mutableblockposition)) {
                int j = random.nextInt(12) + 5;
                int k;

                if (random.nextFloat() < worldgenfeatureconfigurationchance.probability) {
                    k = random.nextInt(4) + 1;

                    for (int l = blockposition.getX() - k; l <= blockposition.getX() + k; ++l) {
                        for (int i1 = blockposition.getZ() - k; i1 <= blockposition.getZ() + k; ++i1) {
                            int j1 = l - blockposition.getX();
                            int k1 = i1 - blockposition.getZ();

                            if (j1 * j1 + k1 * k1 <= k * k) {
                                blockposition_mutableblockposition1.d(l, generatoraccessseed.a(HeightMap.Type.WORLD_SURFACE, l, i1) - 1, i1);
                                if (b(generatoraccessseed.getType(blockposition_mutableblockposition1))) {
                                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition1, Blocks.PODZOL.getBlockData(), 2);
                                }
                            }
                        }
                    }
                }

                for (k = 0; k < j && generatoraccessseed.isEmpty(blockposition_mutableblockposition); ++k) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, WorldGenFeatureBamboo.BAMBOO_TRUNK, 2);
                    blockposition_mutableblockposition.c(EnumDirection.UP, 1);
                }

                if (blockposition_mutableblockposition.getY() - blockposition.getY() >= 3) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, WorldGenFeatureBamboo.BAMBOO_FINAL_LARGE, 2);
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition.c(EnumDirection.DOWN, 1), WorldGenFeatureBamboo.BAMBOO_TOP_LARGE, 2);
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition.c(EnumDirection.DOWN, 1), WorldGenFeatureBamboo.BAMBOO_TOP_SMALL, 2);
                }
            }

            ++i;
        }

        return i > 0;
    }
}
