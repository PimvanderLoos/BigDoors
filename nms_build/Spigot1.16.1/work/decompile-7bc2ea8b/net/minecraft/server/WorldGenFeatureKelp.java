package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureKelp extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureKelp(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        int i = 0;
        int j = generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR, blockposition.getX(), blockposition.getZ());
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), j, blockposition.getZ());

        if (generatoraccessseed.getType(blockposition1).a(Blocks.WATER)) {
            IBlockData iblockdata = Blocks.KELP.getBlockData();
            IBlockData iblockdata1 = Blocks.KELP_PLANT.getBlockData();
            int k = 1 + random.nextInt(10);

            for (int l = 0; l <= k; ++l) {
                if (generatoraccessseed.getType(blockposition1).a(Blocks.WATER) && generatoraccessseed.getType(blockposition1.up()).a(Blocks.WATER) && iblockdata1.canPlace(generatoraccessseed, blockposition1)) {
                    if (l == k) {
                        generatoraccessseed.setTypeAndData(blockposition1, (IBlockData) iblockdata.set(BlockKelp.d, random.nextInt(4) + 20), 2);
                        ++i;
                    } else {
                        generatoraccessseed.setTypeAndData(blockposition1, iblockdata1, 2);
                    }
                } else if (l > 0) {
                    BlockPosition blockposition2 = blockposition1.down();

                    if (iblockdata.canPlace(generatoraccessseed, blockposition2) && !generatoraccessseed.getType(blockposition2.down()).a(Blocks.KELP)) {
                        generatoraccessseed.setTypeAndData(blockposition2, (IBlockData) iblockdata.set(BlockKelp.d, random.nextInt(4) + 20), 2);
                        ++i;
                    }
                    break;
                }

                blockposition1 = blockposition1.up();
            }
        }

        return i > 0;
    }
}
