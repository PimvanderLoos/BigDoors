package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenFeatureNetherrackReplaceBlobs extends WorldGenerator<WorldGenFeatureNetherrackReplaceBlobsConfiguration> {

    public WorldGenFeatureNetherrackReplaceBlobs(Codec<WorldGenFeatureNetherrackReplaceBlobsConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureNetherrackReplaceBlobsConfiguration worldgenfeaturenetherrackreplaceblobsconfiguration) {
        Block block = worldgenfeaturenetherrackreplaceblobsconfiguration.b.getBlock();
        BlockPosition blockposition1 = a((GeneratorAccess) generatoraccessseed, blockposition.i().a(EnumDirection.EnumAxis.Y, 1, generatoraccessseed.getBuildHeight() - 1), block);

        if (blockposition1 == null) {
            return false;
        } else {
            BaseBlockPosition baseblockposition = a(random, worldgenfeaturenetherrackreplaceblobsconfiguration);
            int i = Math.max(baseblockposition.getX(), Math.max(baseblockposition.getY(), baseblockposition.getZ()));
            boolean flag = false;
            Iterator iterator = BlockPosition.a(blockposition1, baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ()).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition2 = (BlockPosition) iterator.next();

                if (blockposition2.k(blockposition1) > i) {
                    break;
                }

                IBlockData iblockdata = generatoraccessseed.getType(blockposition2);

                if (iblockdata.a(block)) {
                    this.a((IWorldWriter) generatoraccessseed, blockposition2, worldgenfeaturenetherrackreplaceblobsconfiguration.c);
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

    private static BaseBlockPosition a(Random random, WorldGenFeatureNetherrackReplaceBlobsConfiguration worldgenfeaturenetherrackreplaceblobsconfiguration) {
        return new BaseBlockPosition(worldgenfeaturenetherrackreplaceblobsconfiguration.d.getX() + random.nextInt(worldgenfeaturenetherrackreplaceblobsconfiguration.e.getX() - worldgenfeaturenetherrackreplaceblobsconfiguration.d.getX() + 1), worldgenfeaturenetherrackreplaceblobsconfiguration.d.getY() + random.nextInt(worldgenfeaturenetherrackreplaceblobsconfiguration.e.getY() - worldgenfeaturenetherrackreplaceblobsconfiguration.d.getY() + 1), worldgenfeaturenetherrackreplaceblobsconfiguration.d.getZ() + random.nextInt(worldgenfeaturenetherrackreplaceblobsconfiguration.e.getZ() - worldgenfeaturenetherrackreplaceblobsconfiguration.d.getZ() + 1));
    }
}
