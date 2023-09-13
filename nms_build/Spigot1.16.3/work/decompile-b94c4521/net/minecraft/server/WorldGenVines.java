package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenVines extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final EnumDirection[] a = EnumDirection.values();

    public WorldGenVines(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        for (int i = 64; i < 256; ++i) {
            blockposition_mutableblockposition.g(blockposition);
            blockposition_mutableblockposition.e(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            blockposition_mutableblockposition.p(i);
            if (generatoraccessseed.isEmpty(blockposition_mutableblockposition)) {
                EnumDirection[] aenumdirection = WorldGenVines.a;
                int j = aenumdirection.length;

                for (int k = 0; k < j; ++k) {
                    EnumDirection enumdirection = aenumdirection[k];

                    if (enumdirection != EnumDirection.DOWN && BlockVine.a((IBlockAccess) generatoraccessseed, (BlockPosition) blockposition_mutableblockposition, enumdirection)) {
                        generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, (IBlockData) Blocks.VINE.getBlockData().set(BlockVine.getDirection(enumdirection), true), 2);
                        break;
                    }
                }
            }
        }

        return true;
    }
}
