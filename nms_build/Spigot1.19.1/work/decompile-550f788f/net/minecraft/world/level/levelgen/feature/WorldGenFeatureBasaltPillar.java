package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureBasaltPillar extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureBasaltPillar(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        RandomSource randomsource = featureplacecontext.random();

        if (generatoraccessseed.isEmptyBlock(blockposition) && !generatoraccessseed.isEmptyBlock(blockposition.above())) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition.mutable();
            boolean flag = true;
            boolean flag1 = true;
            boolean flag2 = true;
            boolean flag3 = true;

            while (generatoraccessseed.isEmptyBlock(blockposition_mutableblockposition)) {
                if (generatoraccessseed.isOutsideBuildHeight(blockposition_mutableblockposition)) {
                    return true;
                }

                generatoraccessseed.setBlock(blockposition_mutableblockposition, Blocks.BASALT.defaultBlockState(), 2);
                flag = flag && this.placeHangOff(generatoraccessseed, randomsource, blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.NORTH));
                flag1 = flag1 && this.placeHangOff(generatoraccessseed, randomsource, blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.SOUTH));
                flag2 = flag2 && this.placeHangOff(generatoraccessseed, randomsource, blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.WEST));
                flag3 = flag3 && this.placeHangOff(generatoraccessseed, randomsource, blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.EAST));
                blockposition_mutableblockposition.move(EnumDirection.DOWN);
            }

            blockposition_mutableblockposition.move(EnumDirection.UP);
            this.placeBaseHangOff(generatoraccessseed, randomsource, blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.NORTH));
            this.placeBaseHangOff(generatoraccessseed, randomsource, blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.SOUTH));
            this.placeBaseHangOff(generatoraccessseed, randomsource, blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.WEST));
            this.placeBaseHangOff(generatoraccessseed, randomsource, blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.EAST));
            blockposition_mutableblockposition.move(EnumDirection.DOWN);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition2 = new BlockPosition.MutableBlockPosition();

            for (int i = -3; i < 4; ++i) {
                for (int j = -3; j < 4; ++j) {
                    int k = MathHelper.abs(i) * MathHelper.abs(j);

                    if (randomsource.nextInt(10) < 10 - k) {
                        blockposition_mutableblockposition2.set(blockposition_mutableblockposition.offset(i, 0, j));
                        int l = 3;

                        while (generatoraccessseed.isEmptyBlock(blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition2, EnumDirection.DOWN))) {
                            blockposition_mutableblockposition2.move(EnumDirection.DOWN);
                            --l;
                            if (l <= 0) {
                                break;
                            }
                        }

                        if (!generatoraccessseed.isEmptyBlock(blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition2, EnumDirection.DOWN))) {
                            generatoraccessseed.setBlock(blockposition_mutableblockposition2, Blocks.BASALT.defaultBlockState(), 2);
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private void placeBaseHangOff(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition) {
        if (randomsource.nextBoolean()) {
            generatoraccess.setBlock(blockposition, Blocks.BASALT.defaultBlockState(), 2);
        }

    }

    private boolean placeHangOff(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition) {
        if (randomsource.nextInt(10) != 0) {
            generatoraccess.setBlock(blockposition, Blocks.BASALT.defaultBlockState(), 2);
            return true;
        } else {
            return false;
        }
    }
}
