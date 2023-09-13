package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockPileConfiguration;

public class WorldGenFeatureBlockPile extends WorldGenerator<WorldGenFeatureBlockPileConfiguration> {

    public WorldGenFeatureBlockPile(Codec<WorldGenFeatureBlockPileConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureBlockPileConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        RandomSource randomsource = featureplacecontext.random();
        WorldGenFeatureBlockPileConfiguration worldgenfeatureblockpileconfiguration = (WorldGenFeatureBlockPileConfiguration) featureplacecontext.config();

        if (blockposition.getY() < generatoraccessseed.getMinBuildHeight() + 5) {
            return false;
        } else {
            int i = 2 + randomsource.nextInt(2);
            int j = 2 + randomsource.nextInt(2);
            Iterator iterator = BlockPosition.betweenClosed(blockposition.offset(-i, 0, -j), blockposition.offset(i, 1, j)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                int k = blockposition.getX() - blockposition1.getX();
                int l = blockposition.getZ() - blockposition1.getZ();

                if ((float) (k * k + l * l) <= randomsource.nextFloat() * 10.0F - randomsource.nextFloat() * 6.0F) {
                    this.tryPlaceBlock(generatoraccessseed, blockposition1, randomsource, worldgenfeatureblockpileconfiguration);
                } else if ((double) randomsource.nextFloat() < 0.031D) {
                    this.tryPlaceBlock(generatoraccessseed, blockposition1, randomsource, worldgenfeatureblockpileconfiguration);
                }
            }

            return true;
        }
    }

    private boolean mayPlaceOn(GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource) {
        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition1);

        return iblockdata.is(Blocks.DIRT_PATH) ? randomsource.nextBoolean() : iblockdata.isFaceSturdy(generatoraccess, blockposition1, EnumDirection.UP);
    }

    private void tryPlaceBlock(GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource, WorldGenFeatureBlockPileConfiguration worldgenfeatureblockpileconfiguration) {
        if (generatoraccess.isEmptyBlock(blockposition) && this.mayPlaceOn(generatoraccess, blockposition, randomsource)) {
            generatoraccess.setBlock(blockposition, worldgenfeatureblockpileconfiguration.stateProvider.getState(randomsource, blockposition), 4);
        }

    }
}
