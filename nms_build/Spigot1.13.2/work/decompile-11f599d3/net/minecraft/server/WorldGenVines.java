package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class WorldGenVines extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenVines() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition);

        for (int i = blockposition.getY(); i < 256; ++i) {
            blockposition_mutableblockposition.g(blockposition);
            blockposition_mutableblockposition.d(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            blockposition_mutableblockposition.p(i);
            if (generatoraccess.isEmpty(blockposition_mutableblockposition)) {
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();
                    IBlockData iblockdata = (IBlockData) Blocks.VINE.getBlockData().set(BlockVine.getDirection(enumdirection), true);

                    if (iblockdata.canPlace(generatoraccess, blockposition_mutableblockposition)) {
                        generatoraccess.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
                        break;
                    }
                }
            }
        }

        return true;
    }
}
