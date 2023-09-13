package net.minecraft.server;

import java.util.Random;

public class WorldGenCactus extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenCactus() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if (generatoraccess.isEmpty(blockposition1)) {
                int j = 1 + random.nextInt(random.nextInt(3) + 1);

                for (int k = 0; k < j; ++k) {
                    if (Blocks.CACTUS.getBlockData().canPlace(generatoraccess, blockposition1)) {
                        generatoraccess.setTypeAndData(blockposition1.up(k), Blocks.CACTUS.getBlockData(), 2);
                    }
                }
            }
        }

        return true;
    }
}
