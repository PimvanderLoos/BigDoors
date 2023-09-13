package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class WorldGenTaigaStructure extends WorldGenerator<WorldGenFeatureBlockOffsetConfiguration> {

    public WorldGenTaigaStructure() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureBlockOffsetConfiguration worldgenfeatureblockoffsetconfiguration) {
        while (true) {
            if (blockposition.getY() > 3) {
                label47:
                {
                    if (!generatoraccess.isEmpty(blockposition.down())) {
                        Block block = generatoraccess.getType(blockposition.down()).getBlock();

                        if (block == Blocks.GRASS_BLOCK || Block.d(block) || Block.c(block)) {
                            break label47;
                        }
                    }

                    blockposition = blockposition.down();
                    continue;
                }
            }

            if (blockposition.getY() <= 3) {
                return false;
            }

            int i = worldgenfeatureblockoffsetconfiguration.b;

            for (int j = 0; i >= 0 && j < 3; ++j) {
                int k = i + random.nextInt(2);
                int l = i + random.nextInt(2);
                int i1 = i + random.nextInt(2);
                float f = (float) (k + l + i1) * 0.333F + 0.5F;
                Iterator iterator = BlockPosition.a(blockposition.a(-k, -l, -i1), blockposition.a(k, l, i1)).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition1 = (BlockPosition) iterator.next();

                    if (blockposition1.n(blockposition) <= (double) (f * f)) {
                        generatoraccess.setTypeAndData(blockposition1, worldgenfeatureblockoffsetconfiguration.a.getBlockData(), 4);
                    }
                }

                blockposition = blockposition.a(-(i + 1) + random.nextInt(2 + i * 2), 0 - random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
            }

            return true;
        }
    }
}
