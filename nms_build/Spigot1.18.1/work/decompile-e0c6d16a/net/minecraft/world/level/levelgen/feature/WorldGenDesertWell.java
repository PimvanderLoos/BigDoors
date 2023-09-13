package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenDesertWell extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final BlockStatePredicate IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
    private final IBlockData sandSlab;
    private final IBlockData sandstone;
    private final IBlockData water;

    public WorldGenDesertWell(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
        this.sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
        this.sandstone = Blocks.SANDSTONE.defaultBlockState();
        this.water = Blocks.WATER.defaultBlockState();
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();

        for (blockposition = blockposition.above(); generatoraccessseed.isEmptyBlock(blockposition) && blockposition.getY() > generatoraccessseed.getMinBuildHeight() + 2; blockposition = blockposition.below()) {
            ;
        }

        if (!WorldGenDesertWell.IS_SAND.test(generatoraccessseed.getBlockState(blockposition))) {
            return false;
        } else {
            int i;
            int j;

            for (i = -2; i <= 2; ++i) {
                for (j = -2; j <= 2; ++j) {
                    if (generatoraccessseed.isEmptyBlock(blockposition.offset(i, -1, j)) && generatoraccessseed.isEmptyBlock(blockposition.offset(i, -2, j))) {
                        return false;
                    }
                }
            }

            for (i = -1; i <= 0; ++i) {
                for (j = -2; j <= 2; ++j) {
                    for (int k = -2; k <= 2; ++k) {
                        generatoraccessseed.setBlock(blockposition.offset(j, i, k), this.sandstone, 2);
                    }
                }
            }

            generatoraccessseed.setBlock(blockposition, this.water, 2);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                generatoraccessseed.setBlock(blockposition.relative(enumdirection), this.water, 2);
            }

            for (i = -2; i <= 2; ++i) {
                for (j = -2; j <= 2; ++j) {
                    if (i == -2 || i == 2 || j == -2 || j == 2) {
                        generatoraccessseed.setBlock(blockposition.offset(i, 1, j), this.sandstone, 2);
                    }
                }
            }

            generatoraccessseed.setBlock(blockposition.offset(2, 1, 0), this.sandSlab, 2);
            generatoraccessseed.setBlock(blockposition.offset(-2, 1, 0), this.sandSlab, 2);
            generatoraccessseed.setBlock(blockposition.offset(0, 1, 2), this.sandSlab, 2);
            generatoraccessseed.setBlock(blockposition.offset(0, 1, -2), this.sandSlab, 2);

            for (i = -1; i <= 1; ++i) {
                for (j = -1; j <= 1; ++j) {
                    if (i == 0 && j == 0) {
                        generatoraccessseed.setBlock(blockposition.offset(i, 4, j), this.sandstone, 2);
                    } else {
                        generatoraccessseed.setBlock(blockposition.offset(i, 4, j), this.sandSlab, 2);
                    }
                }
            }

            for (i = 1; i <= 3; ++i) {
                generatoraccessseed.setBlock(blockposition.offset(-1, i, -1), this.sandstone, 2);
                generatoraccessseed.setBlock(blockposition.offset(-1, i, 1), this.sandstone, 2);
                generatoraccessseed.setBlock(blockposition.offset(1, i, -1), this.sandstone, 2);
                generatoraccessseed.setBlock(blockposition.offset(1, i, 1), this.sandstone, 2);
            }

            return true;
        }
    }
}
