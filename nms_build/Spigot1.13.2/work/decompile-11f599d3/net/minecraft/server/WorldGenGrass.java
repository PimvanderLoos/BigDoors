package net.minecraft.server;

import java.util.Random;

public class WorldGenGrass extends WorldGenerator<WorldGenFeatureTallGrassConfiguration> {

    public WorldGenGrass() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureTallGrassConfiguration worldgenfeaturetallgrassconfiguration) {
        for (IBlockData iblockdata = generatoraccess.getType(blockposition); (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) && blockposition.getY() > 0; iblockdata = generatoraccess.getType(blockposition)) {
            blockposition = blockposition.down();
        }

        int i = 0;

        for (int j = 0; j < 128; ++j) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if (generatoraccess.isEmpty(blockposition1) && worldgenfeaturetallgrassconfiguration.a.canPlace(generatoraccess, blockposition1)) {
                generatoraccess.setTypeAndData(blockposition1, worldgenfeaturetallgrassconfiguration.a, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
