package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenLightStone1 extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenLightStone1(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        RandomSource randomsource = featureplacecontext.random();

        if (!generatoraccessseed.isEmptyBlock(blockposition)) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition.above());

            if (!iblockdata.is(Blocks.NETHERRACK) && !iblockdata.is(Blocks.BASALT) && !iblockdata.is(Blocks.BLACKSTONE)) {
                return false;
            } else {
                generatoraccessseed.setBlock(blockposition, Blocks.GLOWSTONE.defaultBlockState(), 2);

                for (int i = 0; i < 1500; ++i) {
                    BlockPosition blockposition1 = blockposition.offset(randomsource.nextInt(8) - randomsource.nextInt(8), -randomsource.nextInt(12), randomsource.nextInt(8) - randomsource.nextInt(8));

                    if (generatoraccessseed.getBlockState(blockposition1).isAir()) {
                        int j = 0;
                        EnumDirection[] aenumdirection = EnumDirection.values();
                        int k = aenumdirection.length;

                        for (int l = 0; l < k; ++l) {
                            EnumDirection enumdirection = aenumdirection[l];

                            if (generatoraccessseed.getBlockState(blockposition1.relative(enumdirection)).is(Blocks.GLOWSTONE)) {
                                ++j;
                            }

                            if (j > 1) {
                                break;
                            }
                        }

                        if (j == 1) {
                            generatoraccessseed.setBlock(blockposition1, Blocks.GLOWSTONE.defaultBlockState(), 2);
                        }
                    }
                }

                return true;
            }
        }
    }
}
