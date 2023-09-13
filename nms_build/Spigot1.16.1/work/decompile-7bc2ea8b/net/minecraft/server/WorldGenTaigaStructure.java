package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;

public class WorldGenTaigaStructure extends WorldGenerator<WorldGenFeatureBlockOffsetConfiguration> {

    public WorldGenTaigaStructure(Codec<WorldGenFeatureBlockOffsetConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureBlockOffsetConfiguration worldgenfeatureblockoffsetconfiguration) {
        while (true) {
            if (blockposition.getY() > 3) {
                label44:
                {
                    if (!generatoraccessseed.isEmpty(blockposition.down())) {
                        Block block = generatoraccessseed.getType(blockposition.down()).getBlock();

                        if (b(block) || a(block)) {
                            break label44;
                        }
                    }

                    blockposition = blockposition.down();
                    continue;
                }
            }

            if (blockposition.getY() <= 3) {
                return false;
            }

            int i = worldgenfeatureblockoffsetconfiguration.c;

            for (int j = 0; i >= 0 && j < 3; ++j) {
                int k = i + random.nextInt(2);
                int l = i + random.nextInt(2);
                int i1 = i + random.nextInt(2);
                float f = (float) (k + l + i1) * 0.333F + 0.5F;
                Iterator iterator = BlockPosition.a(blockposition.b(-k, -l, -i1), blockposition.b(k, l, i1)).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition1 = (BlockPosition) iterator.next();

                    if (blockposition1.j(blockposition) <= (double) (f * f)) {
                        generatoraccessseed.setTypeAndData(blockposition1, worldgenfeatureblockoffsetconfiguration.b, 4);
                    }
                }

                blockposition = blockposition.b(-(i + 1) + random.nextInt(2 + i * 2), 0 - random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
            }

            return true;
        }
    }
}
