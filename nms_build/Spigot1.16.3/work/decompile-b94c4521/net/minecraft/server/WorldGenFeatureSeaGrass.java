package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureSeaGrass extends WorldGenerator<WorldGenFeatureConfigurationChance> {

    public WorldGenFeatureSeaGrass(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance) {
        boolean flag = false;
        int i = random.nextInt(8) - random.nextInt(8);
        int j = random.nextInt(8) - random.nextInt(8);
        int k = generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR, blockposition.getX() + i, blockposition.getZ() + j);
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX() + i, k, blockposition.getZ() + j);

        if (generatoraccessseed.getType(blockposition1).a(Blocks.WATER)) {
            boolean flag1 = random.nextDouble() < (double) worldgenfeatureconfigurationchance.c;
            IBlockData iblockdata = flag1 ? Blocks.TALL_SEAGRASS.getBlockData() : Blocks.SEAGRASS.getBlockData();

            if (iblockdata.canPlace(generatoraccessseed, blockposition1)) {
                if (flag1) {
                    IBlockData iblockdata1 = (IBlockData) iblockdata.set(BlockTallSeaGrass.b, BlockPropertyDoubleBlockHalf.UPPER);
                    BlockPosition blockposition2 = blockposition1.up();

                    if (generatoraccessseed.getType(blockposition2).a(Blocks.WATER)) {
                        generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                        generatoraccessseed.setTypeAndData(blockposition2, iblockdata1, 2);
                    }
                } else {
                    generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                }

                flag = true;
            }
        }

        return flag;
    }
}
