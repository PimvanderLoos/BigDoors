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

    private static final BlockStatePredicate IS_SAND = BlockStatePredicate.a(Blocks.SAND);
    private final IBlockData sandSlab;
    private final IBlockData sandstone;
    private final IBlockData water;

    public WorldGenDesertWell(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
        this.sandSlab = Blocks.SANDSTONE_SLAB.getBlockData();
        this.sandstone = Blocks.SANDSTONE.getBlockData();
        this.water = Blocks.WATER.getBlockData();
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();

        for (blockposition = blockposition.up(); generatoraccessseed.isEmpty(blockposition) && blockposition.getY() > generatoraccessseed.getMinBuildHeight() + 2; blockposition = blockposition.down()) {
            ;
        }

        if (!WorldGenDesertWell.IS_SAND.test(generatoraccessseed.getType(blockposition))) {
            return false;
        } else {
            int i;
            int j;

            for (i = -2; i <= 2; ++i) {
                for (j = -2; j <= 2; ++j) {
                    if (generatoraccessseed.isEmpty(blockposition.c(i, -1, j)) && generatoraccessseed.isEmpty(blockposition.c(i, -2, j))) {
                        return false;
                    }
                }
            }

            for (i = -1; i <= 0; ++i) {
                for (j = -2; j <= 2; ++j) {
                    for (int k = -2; k <= 2; ++k) {
                        generatoraccessseed.setTypeAndData(blockposition.c(j, i, k), this.sandstone, 2);
                    }
                }
            }

            generatoraccessseed.setTypeAndData(blockposition, this.water, 2);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                generatoraccessseed.setTypeAndData(blockposition.shift(enumdirection), this.water, 2);
            }

            for (i = -2; i <= 2; ++i) {
                for (j = -2; j <= 2; ++j) {
                    if (i == -2 || i == 2 || j == -2 || j == 2) {
                        generatoraccessseed.setTypeAndData(blockposition.c(i, 1, j), this.sandstone, 2);
                    }
                }
            }

            generatoraccessseed.setTypeAndData(blockposition.c(2, 1, 0), this.sandSlab, 2);
            generatoraccessseed.setTypeAndData(blockposition.c(-2, 1, 0), this.sandSlab, 2);
            generatoraccessseed.setTypeAndData(blockposition.c(0, 1, 2), this.sandSlab, 2);
            generatoraccessseed.setTypeAndData(blockposition.c(0, 1, -2), this.sandSlab, 2);

            for (i = -1; i <= 1; ++i) {
                for (j = -1; j <= 1; ++j) {
                    if (i == 0 && j == 0) {
                        generatoraccessseed.setTypeAndData(blockposition.c(i, 4, j), this.sandstone, 2);
                    } else {
                        generatoraccessseed.setTypeAndData(blockposition.c(i, 4, j), this.sandSlab, 2);
                    }
                }
            }

            for (i = 1; i <= 3; ++i) {
                generatoraccessseed.setTypeAndData(blockposition.c(-1, i, -1), this.sandstone, 2);
                generatoraccessseed.setTypeAndData(blockposition.c(-1, i, 1), this.sandstone, 2);
                generatoraccessseed.setTypeAndData(blockposition.c(1, i, -1), this.sandstone, 2);
                generatoraccessseed.setTypeAndData(blockposition.c(1, i, 1), this.sandstone, 2);
            }

            return true;
        }
    }
}
