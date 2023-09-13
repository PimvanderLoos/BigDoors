package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenFeatureDelta extends WorldGenerator<WorldGenFeatureDeltaConfiguration> {

    private static final ImmutableList<Block> a = ImmutableList.of(Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
    private static final EnumDirection[] ac = EnumDirection.values();

    private static int a(Random random, WorldGenFeatureDeltaConfiguration worldgenfeaturedeltaconfiguration) {
        return worldgenfeaturedeltaconfiguration.d + random.nextInt(worldgenfeaturedeltaconfiguration.e - worldgenfeaturedeltaconfiguration.d + 1);
    }

    private static int b(Random random, WorldGenFeatureDeltaConfiguration worldgenfeaturedeltaconfiguration) {
        return random.nextInt(worldgenfeaturedeltaconfiguration.f + 1);
    }

    public WorldGenFeatureDelta(Codec<WorldGenFeatureDeltaConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureDeltaConfiguration worldgenfeaturedeltaconfiguration) {
        BlockPosition blockposition1 = a((GeneratorAccess) generatoraccessseed, blockposition.i().a(EnumDirection.EnumAxis.Y, 1, generatoraccessseed.getBuildHeight() - 1));

        if (blockposition1 == null) {
            return false;
        } else {
            boolean flag = false;
            boolean flag1 = random.nextDouble() < 0.9D;
            int i = flag1 ? b(random, worldgenfeaturedeltaconfiguration) : 0;
            int j = flag1 ? b(random, worldgenfeaturedeltaconfiguration) : 0;
            boolean flag2 = flag1 && i != 0 && j != 0;
            int k = a(random, worldgenfeaturedeltaconfiguration);
            int l = a(random, worldgenfeaturedeltaconfiguration);
            int i1 = Math.max(k, l);
            Iterator iterator = BlockPosition.a(blockposition1, k, 0, l).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition2 = (BlockPosition) iterator.next();

                if (blockposition2.k(blockposition1) > i1) {
                    break;
                }

                if (a((GeneratorAccess) generatoraccessseed, blockposition2, worldgenfeaturedeltaconfiguration)) {
                    if (flag2) {
                        flag = true;
                        this.a((IWorldWriter) generatoraccessseed, blockposition2, worldgenfeaturedeltaconfiguration.c);
                    }

                    BlockPosition blockposition3 = blockposition2.b(i, 0, j);

                    if (a((GeneratorAccess) generatoraccessseed, blockposition3, worldgenfeaturedeltaconfiguration)) {
                        flag = true;
                        this.a((IWorldWriter) generatoraccessseed, blockposition3, worldgenfeaturedeltaconfiguration.b);
                    }
                }
            }

            return flag;
        }
    }

    private static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, WorldGenFeatureDeltaConfiguration worldgenfeaturedeltaconfiguration) {
        IBlockData iblockdata = generatoraccess.getType(blockposition);

        if (iblockdata.a(worldgenfeaturedeltaconfiguration.b.getBlock())) {
            return false;
        } else if (WorldGenFeatureDelta.a.contains(iblockdata.getBlock())) {
            return false;
        } else {
            EnumDirection[] aenumdirection = WorldGenFeatureDelta.ac;
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];
                boolean flag = generatoraccess.getType(blockposition.shift(enumdirection)).isAir();

                if (flag && enumdirection != EnumDirection.UP || !flag && enumdirection == EnumDirection.UP) {
                    return false;
                }
            }

            return true;
        }
    }

    @Nullable
    private static BlockPosition a(GeneratorAccess generatoraccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        for (; blockposition_mutableblockposition.getY() > 1; blockposition_mutableblockposition.c(EnumDirection.DOWN)) {
            if (generatoraccess.getType(blockposition_mutableblockposition).isAir()) {
                IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition.c(EnumDirection.DOWN));

                blockposition_mutableblockposition.c(EnumDirection.UP);
                if (!iblockdata.a(Blocks.LAVA) && !iblockdata.a(Blocks.BEDROCK) && !iblockdata.isAir()) {
                    return blockposition_mutableblockposition;
                }
            }
        }

        return null;
    }
}
