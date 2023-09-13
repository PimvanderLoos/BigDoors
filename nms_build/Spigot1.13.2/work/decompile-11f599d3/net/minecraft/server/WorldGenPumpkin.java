package net.minecraft.server;

import java.util.Random;

public class WorldGenPumpkin extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenPumpkin() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        int i = 0;
        IBlockData iblockdata = Blocks.PUMPKIN.getBlockData();

        for (int j = 0; j < 64; ++j) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if (generatoraccess.isEmpty(blockposition1) && generatoraccess.getType(blockposition1.down()).getBlock() == Blocks.GRASS_BLOCK) {
                generatoraccess.setTypeAndData(blockposition1, iblockdata, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
