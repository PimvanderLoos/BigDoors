package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenDesertWell extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final BlockStatePredicate a = BlockStatePredicate.a(Blocks.SAND);
    private final IBlockData ab;
    private final IBlockData ac;
    private final IBlockData ad;

    public WorldGenDesertWell(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
        this.ab = Blocks.SANDSTONE_SLAB.getBlockData();
        this.ac = Blocks.SANDSTONE.getBlockData();
        this.ad = Blocks.WATER.getBlockData();
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        for (blockposition = blockposition.up(); generatoraccessseed.isEmpty(blockposition) && blockposition.getY() > 2; blockposition = blockposition.down()) {
            ;
        }

        if (!WorldGenDesertWell.a.test(generatoraccessseed.getType(blockposition))) {
            return false;
        } else {
            int i;
            int j;

            for (i = -2; i <= 2; ++i) {
                for (j = -2; j <= 2; ++j) {
                    if (generatoraccessseed.isEmpty(blockposition.b(i, -1, j)) && generatoraccessseed.isEmpty(blockposition.b(i, -2, j))) {
                        return false;
                    }
                }
            }

            for (i = -1; i <= 0; ++i) {
                for (j = -2; j <= 2; ++j) {
                    for (int k = -2; k <= 2; ++k) {
                        generatoraccessseed.setTypeAndData(blockposition.b(j, i, k), this.ac, 2);
                    }
                }
            }

            generatoraccessseed.setTypeAndData(blockposition, this.ad, 2);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                generatoraccessseed.setTypeAndData(blockposition.shift(enumdirection), this.ad, 2);
            }

            for (i = -2; i <= 2; ++i) {
                for (j = -2; j <= 2; ++j) {
                    if (i == -2 || i == 2 || j == -2 || j == 2) {
                        generatoraccessseed.setTypeAndData(blockposition.b(i, 1, j), this.ac, 2);
                    }
                }
            }

            generatoraccessseed.setTypeAndData(blockposition.b(2, 1, 0), this.ab, 2);
            generatoraccessseed.setTypeAndData(blockposition.b(-2, 1, 0), this.ab, 2);
            generatoraccessseed.setTypeAndData(blockposition.b(0, 1, 2), this.ab, 2);
            generatoraccessseed.setTypeAndData(blockposition.b(0, 1, -2), this.ab, 2);

            for (i = -1; i <= 1; ++i) {
                for (j = -1; j <= 1; ++j) {
                    if (i == 0 && j == 0) {
                        generatoraccessseed.setTypeAndData(blockposition.b(i, 4, j), this.ac, 2);
                    } else {
                        generatoraccessseed.setTypeAndData(blockposition.b(i, 4, j), this.ab, 2);
                    }
                }
            }

            for (i = 1; i <= 3; ++i) {
                generatoraccessseed.setTypeAndData(blockposition.b(-1, i, -1), this.ac, 2);
                generatoraccessseed.setTypeAndData(blockposition.b(-1, i, 1), this.ac, 2);
                generatoraccessseed.setTypeAndData(blockposition.b(1, i, -1), this.ac, 2);
                generatoraccessseed.setTypeAndData(blockposition.b(1, i, 1), this.ac, 2);
            }

            return true;
        }
    }
}
