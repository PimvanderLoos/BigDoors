package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureSeaGrass extends WorldGenerator<WorldGenFeatureSeaGrassConfiguration> {

    public WorldGenFeatureSeaGrass(Codec<WorldGenFeatureSeaGrassConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureSeaGrassConfiguration worldgenfeatureseagrassconfiguration) {
        int i = 0;

        for (int j = 0; j < worldgenfeatureseagrassconfiguration.b; ++j) {
            int k = random.nextInt(8) - random.nextInt(8);
            int l = random.nextInt(8) - random.nextInt(8);
            int i1 = generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR, blockposition.getX() + k, blockposition.getZ() + l);
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX() + k, i1, blockposition.getZ() + l);

            if (generatoraccessseed.getType(blockposition1).a(Blocks.WATER)) {
                boolean flag = random.nextDouble() < worldgenfeatureseagrassconfiguration.c;
                IBlockData iblockdata = flag ? Blocks.TALL_SEAGRASS.getBlockData() : Blocks.SEAGRASS.getBlockData();

                if (iblockdata.canPlace(generatoraccessseed, blockposition1)) {
                    if (flag) {
                        IBlockData iblockdata1 = (IBlockData) iblockdata.set(BlockTallSeaGrass.b, BlockPropertyDoubleBlockHalf.UPPER);
                        BlockPosition blockposition2 = blockposition1.up();

                        if (generatoraccessseed.getType(blockposition2).a(Blocks.WATER)) {
                            generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                            generatoraccessseed.setTypeAndData(blockposition2, iblockdata1, 2);
                        }
                    } else {
                        generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                    }

                    ++i;
                }
            }
        }

        return i > 0;
    }
}
