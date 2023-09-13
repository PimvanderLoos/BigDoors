package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureMushroomConfiguration;

public abstract class WorldGenMushrooms extends WorldGenerator<WorldGenFeatureMushroomConfiguration> {

    public WorldGenMushrooms(Codec<WorldGenFeatureMushroomConfiguration> codec) {
        super(codec);
    }

    protected void placeTrunk(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        for (int j = 0; j < i; ++j) {
            blockposition_mutableblockposition.set(blockposition).move(EnumDirection.UP, j);
            if (!generatoraccess.getBlockState(blockposition_mutableblockposition).isSolidRender(generatoraccess, blockposition_mutableblockposition)) {
                this.setBlock(generatoraccess, blockposition_mutableblockposition, worldgenfeaturemushroomconfiguration.stemProvider.getState(randomsource, blockposition));
            }
        }

    }

    protected int getTreeHeight(RandomSource randomsource) {
        int i = randomsource.nextInt(3) + 4;

        if (randomsource.nextInt(12) == 0) {
            i *= 2;
        }

        return i;
    }

    protected boolean isValidPosition(GeneratorAccess generatoraccess, BlockPosition blockposition, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration) {
        int j = blockposition.getY();

        if (j >= generatoraccess.getMinBuildHeight() + 1 && j + i + 1 < generatoraccess.getMaxBuildHeight()) {
            IBlockData iblockdata = generatoraccess.getBlockState(blockposition.below());

            if (!isDirt(iblockdata) && !iblockdata.is(TagsBlock.MUSHROOM_GROW_BLOCK)) {
                return false;
            } else {
                for (int k = 0; k <= i; ++k) {
                    int l = this.getTreeRadiusForHeight(-1, -1, worldgenfeaturemushroomconfiguration.foliageRadius, k);

                    for (int i1 = -l; i1 <= l; ++i1) {
                        for (int j1 = -l; j1 <= l; ++j1) {
                            IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition_mutableblockposition.setWithOffset(blockposition, i1, k, j1));

                            if (!iblockdata1.isAir() && !iblockdata1.is(TagsBlock.LEAVES)) {
                                return false;
                            }
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureMushroomConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        RandomSource randomsource = featureplacecontext.random();
        WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration = (WorldGenFeatureMushroomConfiguration) featureplacecontext.config();
        int i = this.getTreeHeight(randomsource);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        if (!this.isValidPosition(generatoraccessseed, blockposition, i, blockposition_mutableblockposition, worldgenfeaturemushroomconfiguration)) {
            return false;
        } else {
            this.makeCap(generatoraccessseed, randomsource, blockposition, i, blockposition_mutableblockposition, worldgenfeaturemushroomconfiguration);
            this.placeTrunk(generatoraccessseed, randomsource, blockposition, worldgenfeaturemushroomconfiguration, i, blockposition_mutableblockposition);
            return true;
        }
    }

    protected abstract int getTreeRadiusForHeight(int i, int j, int k, int l);

    protected abstract void makeCap(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration);
}
