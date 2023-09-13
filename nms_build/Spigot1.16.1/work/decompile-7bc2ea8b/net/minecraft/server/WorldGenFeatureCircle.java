package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;

public class WorldGenFeatureCircle extends WorldGenerator<WorldGenFeatureCircleConfiguration> {

    public WorldGenFeatureCircle(Codec<WorldGenFeatureCircleConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration) {
        if (!generatoraccessseed.getFluid(blockposition).a((Tag) TagsFluid.WATER)) {
            return false;
        } else {
            int i = 0;
            int j = random.nextInt(worldgenfeaturecircleconfiguration.c - 2) + 2;

            for (int k = blockposition.getX() - j; k <= blockposition.getX() + j; ++k) {
                for (int l = blockposition.getZ() - j; l <= blockposition.getZ() + j; ++l) {
                    int i1 = k - blockposition.getX();
                    int j1 = l - blockposition.getZ();

                    if (i1 * i1 + j1 * j1 <= j * j) {
                        int k1 = blockposition.getY() - worldgenfeaturecircleconfiguration.d;

                        while (k1 <= blockposition.getY() + worldgenfeaturecircleconfiguration.d) {
                            BlockPosition blockposition1 = new BlockPosition(k, k1, l);
                            IBlockData iblockdata = generatoraccessseed.getType(blockposition1);
                            Iterator iterator = worldgenfeaturecircleconfiguration.e.iterator();

                            while (true) {
                                if (iterator.hasNext()) {
                                    IBlockData iblockdata1 = (IBlockData) iterator.next();

                                    if (!iblockdata1.a(iblockdata.getBlock())) {
                                        continue;
                                    }

                                    generatoraccessseed.setTypeAndData(blockposition1, worldgenfeaturecircleconfiguration.b, 2);
                                    ++i;
                                }

                                ++k1;
                                break;
                            }
                        }
                    }
                }
            }

            return i > 0;
        }
    }
}
