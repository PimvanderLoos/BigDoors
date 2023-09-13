package net.minecraft.server;

import java.util.Random;

public class WorldGenMelon extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenMelon() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        for (int i = 0; i < 64; ++i) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            IBlockData iblockdata = Blocks.MELON.getBlockData();

            if (generatoraccess.getType(blockposition1.down()).getBlock() == Blocks.GRASS_BLOCK) {
                generatoraccess.setTypeAndData(blockposition1, iblockdata, 2);
            }
        }

        return true;
    }
}
