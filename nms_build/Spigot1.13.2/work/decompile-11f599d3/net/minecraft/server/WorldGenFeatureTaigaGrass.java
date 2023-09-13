package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureTaigaGrass extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureTaigaGrass() {}

    public IBlockData a(Random random) {
        return random.nextInt(5) > 0 ? Blocks.FERN.getBlockData() : Blocks.GRASS.getBlockData();
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        IBlockData iblockdata = this.a(random);

        for (IBlockData iblockdata1 = generatoraccess.getType(blockposition); (iblockdata1.isAir() || iblockdata1.a(TagsBlock.LEAVES)) && blockposition.getY() > 0; iblockdata1 = generatoraccess.getType(blockposition)) {
            blockposition = blockposition.down();
        }

        int i = 0;

        for (int j = 0; j < 128; ++j) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if (generatoraccess.isEmpty(blockposition1) && iblockdata.canPlace(generatoraccess, blockposition1)) {
                generatoraccess.setTypeAndData(blockposition1, iblockdata, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
