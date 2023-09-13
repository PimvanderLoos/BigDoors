package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenFeatureNetherrackReplaceBlobs extends WorldGenerator<WorldGenFeatureRadiusConfiguration> {

    public WorldGenFeatureNetherrackReplaceBlobs(Codec<WorldGenFeatureRadiusConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRadiusConfiguration worldgenfeatureradiusconfiguration) {
        Block block = worldgenfeatureradiusconfiguration.b.getBlock();
        BlockPosition blockposition1 = a((GeneratorAccess) generatoraccessseed, blockposition.i().a(EnumDirection.EnumAxis.Y, 1, generatoraccessseed.getBuildHeight() - 1), block);

        if (blockposition1 == null) {
            return false;
        } else {
            int i = worldgenfeatureradiusconfiguration.b().a(random);
            boolean flag = false;
            Iterator iterator = BlockPosition.a(blockposition1, i, i, i).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition2 = (BlockPosition) iterator.next();

                if (blockposition2.k(blockposition1) > i) {
                    break;
                }

                IBlockData iblockdata = generatoraccessseed.getType(blockposition2);

                if (iblockdata.a(block)) {
                    this.a((IWorldWriter) generatoraccessseed, blockposition2, worldgenfeatureradiusconfiguration.c);
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Nullable
    private static BlockPosition a(GeneratorAccess generatoraccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, Block block) {
        while (blockposition_mutableblockposition.getY() > 1) {
            IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition);

            if (iblockdata.a(block)) {
                return blockposition_mutableblockposition;
            }

            blockposition_mutableblockposition.c(EnumDirection.DOWN);
        }

        return null;
    }
}
