package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;

public class WorldGenFeatureDisk extends WorldGenerator<WorldGenFeatureCircleConfiguration> {

    public WorldGenFeatureDisk(Codec<WorldGenFeatureCircleConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration) {
        boolean flag = false;
        int i = worldgenfeaturecircleconfiguration.c.a(random);

        for (int j = blockposition.getX() - i; j <= blockposition.getX() + i; ++j) {
            for (int k = blockposition.getZ() - i; k <= blockposition.getZ() + i; ++k) {
                int l = j - blockposition.getX();
                int i1 = k - blockposition.getZ();

                if (l * l + i1 * i1 <= i * i) {
                    int j1 = blockposition.getY() - worldgenfeaturecircleconfiguration.d;

                    while (j1 <= blockposition.getY() + worldgenfeaturecircleconfiguration.d) {
                        BlockPosition blockposition1 = new BlockPosition(j, j1, k);
                        Block block = generatoraccessseed.getType(blockposition1).getBlock();
                        Iterator iterator = worldgenfeaturecircleconfiguration.e.iterator();

                        while (true) {
                            if (iterator.hasNext()) {
                                IBlockData iblockdata = (IBlockData) iterator.next();

                                if (!iblockdata.a(block)) {
                                    continue;
                                }

                                generatoraccessseed.setTypeAndData(blockposition1, worldgenfeaturecircleconfiguration.b, 2);
                                flag = true;
                            }

                            ++j1;
                            break;
                        }
                    }
                }
            }
        }

        return flag;
    }
}
