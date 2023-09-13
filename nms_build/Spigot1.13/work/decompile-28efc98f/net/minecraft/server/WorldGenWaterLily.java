package net.minecraft.server;

import java.util.Random;

public class WorldGenWaterLily extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenWaterLily() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        BlockPosition blockposition1;

        for (BlockPosition blockposition2 = blockposition; blockposition2.getY() > 0; blockposition2 = blockposition1) {
            blockposition1 = blockposition2.down();
            if (!generatoraccess.isEmpty(blockposition1)) {
                break;
            }
        }

        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition3 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
            IBlockData iblockdata = Blocks.LILY_PAD.getBlockData();

            if (generatoraccess.isEmpty(blockposition3) && iblockdata.canPlace(generatoraccess, blockposition3)) {
                generatoraccess.setTypeAndData(blockposition3, iblockdata, 2);
            }
        }

        return true;
    }
}
