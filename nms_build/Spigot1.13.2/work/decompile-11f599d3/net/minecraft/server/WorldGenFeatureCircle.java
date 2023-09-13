package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureCircle extends WorldGenerator<WorldGenFeatureCircleConfiguration> {

    public WorldGenFeatureCircle() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration) {
        if (!generatoraccess.getFluid(blockposition).a(TagsFluid.WATER)) {
            return false;
        } else {
            int i = 0;
            int j = random.nextInt(worldgenfeaturecircleconfiguration.b - 2) + 2;

            for (int k = blockposition.getX() - j; k <= blockposition.getX() + j; ++k) {
                for (int l = blockposition.getZ() - j; l <= blockposition.getZ() + j; ++l) {
                    int i1 = k - blockposition.getX();
                    int j1 = l - blockposition.getZ();

                    if (i1 * i1 + j1 * j1 <= j * j) {
                        for (int k1 = blockposition.getY() - worldgenfeaturecircleconfiguration.c; k1 <= blockposition.getY() + worldgenfeaturecircleconfiguration.c; ++k1) {
                            BlockPosition blockposition1 = new BlockPosition(k, k1, l);
                            Block block = generatoraccess.getType(blockposition1).getBlock();

                            if (worldgenfeaturecircleconfiguration.d.contains(block)) {
                                generatoraccess.setTypeAndData(blockposition1, worldgenfeaturecircleconfiguration.a.getBlockData(), 2);
                                ++i;
                            }
                        }
                    }
                }
            }

            return i > 0;
        }
    }
}
