package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureLakeConfiguration;

public class WorldGenTaigaStructure extends WorldGenerator<WorldGenFeatureLakeConfiguration> {

    public WorldGenTaigaStructure(Codec<WorldGenFeatureLakeConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureLakeConfiguration worldgenfeaturelakeconfiguration) {
        while (true) {
            if (blockposition.getY() > 3) {
                label38:
                {
                    if (!generatoraccessseed.isEmpty(blockposition.down())) {
                        Block block = generatoraccessseed.getType(blockposition.down()).getBlock();

                        if (b(block) || a(block)) {
                            break label38;
                        }
                    }

                    blockposition = blockposition.down();
                    continue;
                }
            }

            if (blockposition.getY() <= 3) {
                return false;
            }

            for (int i = 0; i < 3; ++i) {
                int j = random.nextInt(2);
                int k = random.nextInt(2);
                int l = random.nextInt(2);
                float f = (float) (j + k + l) * 0.333F + 0.5F;
                Iterator iterator = BlockPosition.a(blockposition.b(-j, -k, -l), blockposition.b(j, k, l)).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition1 = (BlockPosition) iterator.next();

                    if (blockposition1.j(blockposition) <= (double) (f * f)) {
                        generatoraccessseed.setTypeAndData(blockposition1, worldgenfeaturelakeconfiguration.b, 4);
                    }
                }

                blockposition = blockposition.b(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
            }

            return true;
        }
    }
}
