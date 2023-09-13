package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBasaltColumnsConfiguration;

public class WorldGenFeatureBasaltColumns extends WorldGenerator<WorldGenFeatureBasaltColumnsConfiguration> {

    private static final ImmutableList<Block> CANNOT_PLACE_ON = ImmutableList.of(Blocks.LAVA, Blocks.BEDROCK, Blocks.MAGMA_BLOCK, Blocks.SOUL_SAND, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
    private static final int CLUSTERED_REACH = 5;
    private static final int CLUSTERED_SIZE = 50;
    private static final int UNCLUSTERED_REACH = 8;
    private static final int UNCLUSTERED_SIZE = 15;

    public WorldGenFeatureBasaltColumns(Codec<WorldGenFeatureBasaltColumnsConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureBasaltColumnsConfiguration> featureplacecontext) {
        int i = featureplacecontext.chunkGenerator().getSeaLevel();
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        RandomSource randomsource = featureplacecontext.random();
        WorldGenFeatureBasaltColumnsConfiguration worldgenfeaturebasaltcolumnsconfiguration = (WorldGenFeatureBasaltColumnsConfiguration) featureplacecontext.config();

        if (!canPlaceAt(generatoraccessseed, i, blockposition.mutable())) {
            return false;
        } else {
            int j = worldgenfeaturebasaltcolumnsconfiguration.height().sample(randomsource);
            boolean flag = randomsource.nextFloat() < 0.9F;
            int k = Math.min(j, flag ? 5 : 8);
            int l = flag ? 50 : 15;
            boolean flag1 = false;
            Iterator iterator = BlockPosition.randomBetweenClosed(randomsource, l, blockposition.getX() - k, blockposition.getY(), blockposition.getZ() - k, blockposition.getX() + k, blockposition.getY(), blockposition.getZ() + k).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                int i1 = j - blockposition1.distManhattan(blockposition);

                if (i1 >= 0) {
                    flag1 |= this.placeColumn(generatoraccessseed, i, blockposition1, i1, worldgenfeaturebasaltcolumnsconfiguration.reach().sample(randomsource));
                }
            }

            return flag1;
        }
    }

    private boolean placeColumn(GeneratorAccess generatoraccess, int i, BlockPosition blockposition, int j, int k) {
        boolean flag = false;
        Iterator iterator = BlockPosition.betweenClosed(blockposition.getX() - k, blockposition.getY(), blockposition.getZ() - k, blockposition.getX() + k, blockposition.getY(), blockposition.getZ() + k).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            int l = blockposition1.distManhattan(blockposition);
            BlockPosition blockposition2 = isAirOrLavaOcean(generatoraccess, i, blockposition1) ? findSurface(generatoraccess, i, blockposition1.mutable(), l) : findAir(generatoraccess, blockposition1.mutable(), l);

            if (blockposition2 != null) {
                int i1 = j - l / 2;

                for (BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition2.mutable(); i1 >= 0; --i1) {
                    if (isAirOrLavaOcean(generatoraccess, i, blockposition_mutableblockposition)) {
                        this.setBlock(generatoraccess, blockposition_mutableblockposition, Blocks.BASALT.defaultBlockState());
                        blockposition_mutableblockposition.move(EnumDirection.UP);
                        flag = true;
                    } else {
                        if (!generatoraccess.getBlockState(blockposition_mutableblockposition).is(Blocks.BASALT)) {
                            break;
                        }

                        blockposition_mutableblockposition.move(EnumDirection.UP);
                    }
                }
            }
        }

        return flag;
    }

    @Nullable
    private static BlockPosition findSurface(GeneratorAccess generatoraccess, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int j) {
        while (blockposition_mutableblockposition.getY() > generatoraccess.getMinBuildHeight() + 1 && j > 0) {
            --j;
            if (canPlaceAt(generatoraccess, i, blockposition_mutableblockposition)) {
                return blockposition_mutableblockposition;
            }

            blockposition_mutableblockposition.move(EnumDirection.DOWN);
        }

        return null;
    }

    private static boolean canPlaceAt(GeneratorAccess generatoraccess, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        if (!isAirOrLavaOcean(generatoraccess, i, blockposition_mutableblockposition)) {
            return false;
        } else {
            IBlockData iblockdata = generatoraccess.getBlockState(blockposition_mutableblockposition.move(EnumDirection.DOWN));

            blockposition_mutableblockposition.move(EnumDirection.UP);
            return !iblockdata.isAir() && !WorldGenFeatureBasaltColumns.CANNOT_PLACE_ON.contains(iblockdata.getBlock());
        }
    }

    @Nullable
    private static BlockPosition findAir(GeneratorAccess generatoraccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i) {
        while (blockposition_mutableblockposition.getY() < generatoraccess.getMaxBuildHeight() && i > 0) {
            --i;
            IBlockData iblockdata = generatoraccess.getBlockState(blockposition_mutableblockposition);

            if (WorldGenFeatureBasaltColumns.CANNOT_PLACE_ON.contains(iblockdata.getBlock())) {
                return null;
            }

            if (iblockdata.isAir()) {
                return blockposition_mutableblockposition;
            }

            blockposition_mutableblockposition.move(EnumDirection.UP);
        }

        return null;
    }

    private static boolean isAirOrLavaOcean(GeneratorAccess generatoraccess, int i, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

        return iblockdata.isAir() || iblockdata.is(Blocks.LAVA) && blockposition.getY() <= i;
    }
}
