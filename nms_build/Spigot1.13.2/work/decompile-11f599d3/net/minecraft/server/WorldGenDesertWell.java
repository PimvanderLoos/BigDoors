package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class WorldGenDesertWell extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final BlockStatePredicate a = BlockStatePredicate.a(Blocks.SAND);
    private final IBlockData b;
    private final IBlockData c;
    private final IBlockData d;

    public WorldGenDesertWell() {
        this.b = Blocks.SANDSTONE_SLAB.getBlockData();
        this.c = Blocks.SANDSTONE.getBlockData();
        this.d = Blocks.WATER.getBlockData();
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        for (blockposition = blockposition.up(); generatoraccess.isEmpty(blockposition) && blockposition.getY() > 2; blockposition = blockposition.down()) {
            ;
        }

        if (!WorldGenDesertWell.a.test(generatoraccess.getType(blockposition))) {
            return false;
        } else {
            int i;
            int j;

            for (i = -2; i <= 2; ++i) {
                for (j = -2; j <= 2; ++j) {
                    if (generatoraccess.isEmpty(blockposition.a(i, -1, j)) && generatoraccess.isEmpty(blockposition.a(i, -2, j))) {
                        return false;
                    }
                }
            }

            for (i = -1; i <= 0; ++i) {
                for (j = -2; j <= 2; ++j) {
                    for (int k = -2; k <= 2; ++k) {
                        generatoraccess.setTypeAndData(blockposition.a(j, i, k), this.c, 2);
                    }
                }
            }

            generatoraccess.setTypeAndData(blockposition, this.d, 2);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                generatoraccess.setTypeAndData(blockposition.shift(enumdirection), this.d, 2);
            }

            for (i = -2; i <= 2; ++i) {
                for (j = -2; j <= 2; ++j) {
                    if (i == -2 || i == 2 || j == -2 || j == 2) {
                        generatoraccess.setTypeAndData(blockposition.a(i, 1, j), this.c, 2);
                    }
                }
            }

            generatoraccess.setTypeAndData(blockposition.a(2, 1, 0), this.b, 2);
            generatoraccess.setTypeAndData(blockposition.a(-2, 1, 0), this.b, 2);
            generatoraccess.setTypeAndData(blockposition.a(0, 1, 2), this.b, 2);
            generatoraccess.setTypeAndData(blockposition.a(0, 1, -2), this.b, 2);

            for (i = -1; i <= 1; ++i) {
                for (j = -1; j <= 1; ++j) {
                    if (i == 0 && j == 0) {
                        generatoraccess.setTypeAndData(blockposition.a(i, 4, j), this.c, 2);
                    } else {
                        generatoraccess.setTypeAndData(blockposition.a(i, 4, j), this.b, 2);
                    }
                }
            }

            for (i = 1; i <= 3; ++i) {
                generatoraccess.setTypeAndData(blockposition.a(-1, i, -1), this.c, 2);
                generatoraccess.setTypeAndData(blockposition.a(-1, i, 1), this.c, 2);
                generatoraccess.setTypeAndData(blockposition.a(1, i, -1), this.c, 2);
                generatoraccess.setTypeAndData(blockposition.a(1, i, 1), this.c, 2);
            }

            return true;
        }
    }
}
