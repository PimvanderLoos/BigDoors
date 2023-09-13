package net.minecraft.server;

import java.util.Random;

public class WorldGenMushrooms extends WorldGenerator<WorldGenFeatureMushroomConfiguration> {

    public WorldGenMushrooms() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration) {
        int i = 0;
        IBlockData iblockdata = worldgenfeaturemushroomconfiguration.a.getBlockData();

        for (int j = 0; j < 64; ++j) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if (generatoraccess.isEmpty(blockposition1) && (!generatoraccess.o().h() || blockposition1.getY() < 255) && iblockdata.canPlace(generatoraccess, blockposition1)) {
                generatoraccess.setTypeAndData(blockposition1, iblockdata, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
