package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBasaltColumnsConfiguration;

public class WorldGenFeatureBasaltColumns extends WorldGenerator<WorldGenFeatureBasaltColumnsConfiguration> {

    private static final ImmutableList<Block> a = ImmutableList.of(Blocks.LAVA, Blocks.BEDROCK, Blocks.MAGMA_BLOCK, Blocks.SOUL_SAND, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);

    public WorldGenFeatureBasaltColumns(Codec<WorldGenFeatureBasaltColumnsConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureBasaltColumnsConfiguration worldgenfeaturebasaltcolumnsconfiguration) {
        int i = chunkgenerator.getSeaLevel();

        if (!a(generatoraccessseed, i, blockposition.i())) {
            return false;
        } else {
            int j = worldgenfeaturebasaltcolumnsconfiguration.b().a(random);
            boolean flag = random.nextFloat() < 0.9F;
            int k = Math.min(j, flag ? 5 : 8);
            int l = flag ? 50 : 15;
            boolean flag1 = false;
            Iterator iterator = BlockPosition.a(random, l, blockposition.getX() - k, blockposition.getY(), blockposition.getZ() - k, blockposition.getX() + k, blockposition.getY(), blockposition.getZ() + k).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                int i1 = j - blockposition1.k(blockposition);

                if (i1 >= 0) {
                    flag1 |= this.a(generatoraccessseed, i, blockposition1, i1, worldgenfeaturebasaltcolumnsconfiguration.am_().a(random));
                }
            }

            return flag1;
        }
    }

    private boolean a(GeneratorAccess generatoraccess, int i, BlockPosition blockposition, int j, int k) {
        boolean flag = false;
        Iterator iterator = BlockPosition.b(blockposition.getX() - k, blockposition.getY(), blockposition.getZ() - k, blockposition.getX() + k, blockposition.getY(), blockposition.getZ() + k).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            int l = blockposition1.k(blockposition);
            BlockPosition blockposition2 = a(generatoraccess, i, blockposition1) ? a(generatoraccess, i, blockposition1.i(), l) : a(generatoraccess, blockposition1.i(), l);

            if (blockposition2 != null) {
                int i1 = j - l / 2;

                for (BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition2.i(); i1 >= 0; --i1) {
                    if (a(generatoraccess, i, (BlockPosition) blockposition_mutableblockposition)) {
                        this.a(generatoraccess, blockposition_mutableblockposition, Blocks.BASALT.getBlockData());
                        blockposition_mutableblockposition.c(EnumDirection.UP);
                        flag = true;
                    } else {
                        if (!generatoraccess.getType(blockposition_mutableblockposition).a(Blocks.BASALT)) {
                            break;
                        }

                        blockposition_mutableblockposition.c(EnumDirection.UP);
                    }
                }
            }
        }

        return flag;
    }

    @Nullable
    private static BlockPosition a(GeneratorAccess generatoraccess, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int j) {
        while (blockposition_mutableblockposition.getY() > 1 && j > 0) {
            --j;
            if (a(generatoraccess, i, blockposition_mutableblockposition)) {
                return blockposition_mutableblockposition;
            }

            blockposition_mutableblockposition.c(EnumDirection.DOWN);
        }

        return null;
    }

    private static boolean a(GeneratorAccess generatoraccess, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        if (!a(generatoraccess, i, (BlockPosition) blockposition_mutableblockposition)) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition.c(EnumDirection.DOWN));

            blockposition_mutableblockposition.c(EnumDirection.UP);
            return !iblockdata.isAir() && !WorldGenFeatureBasaltColumns.a.contains(iblockdata.getBlock());
        }
    }

    @Nullable
    private static BlockPosition a(GeneratorAccess generatoraccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i) {
        while (blockposition_mutableblockposition.getY() < generatoraccess.getBuildHeight() && i > 0) {
            --i;
            IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition);

            if (WorldGenFeatureBasaltColumns.a.contains(iblockdata.getBlock())) {
                return null;
            }

            if (iblockdata.isAir()) {
                return blockposition_mutableblockposition;
            }

            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

        return null;
    }

    private static boolean a(GeneratorAccess generatoraccess, int i, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getType(blockposition);

        return iblockdata.isAir() || iblockdata.a(Blocks.LAVA) && blockposition.getY() <= i;
    }
}
