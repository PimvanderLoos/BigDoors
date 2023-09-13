package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDeltaConfiguration;

public class WorldGenFeatureDelta extends WorldGenerator<WorldGenFeatureDeltaConfiguration> {

    private static final ImmutableList<Block> CANNOT_REPLACE = ImmutableList.of(Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();
    private static final double RIM_SPAWN_CHANCE = 0.9D;

    public WorldGenFeatureDelta(Codec<WorldGenFeatureDeltaConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureDeltaConfiguration> featureplacecontext) {
        boolean flag = false;
        RandomSource randomsource = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        WorldGenFeatureDeltaConfiguration worldgenfeaturedeltaconfiguration = (WorldGenFeatureDeltaConfiguration) featureplacecontext.config();
        BlockPosition blockposition = featureplacecontext.origin();
        boolean flag1 = randomsource.nextDouble() < 0.9D;
        int i = flag1 ? worldgenfeaturedeltaconfiguration.rimSize().sample(randomsource) : 0;
        int j = flag1 ? worldgenfeaturedeltaconfiguration.rimSize().sample(randomsource) : 0;
        boolean flag2 = flag1 && i != 0 && j != 0;
        int k = worldgenfeaturedeltaconfiguration.size().sample(randomsource);
        int l = worldgenfeaturedeltaconfiguration.size().sample(randomsource);
        int i1 = Math.max(k, l);
        Iterator iterator = BlockPosition.withinManhattan(blockposition, k, 0, l).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (blockposition1.distManhattan(blockposition) > i1) {
                break;
            }

            if (isClear(generatoraccessseed, blockposition1, worldgenfeaturedeltaconfiguration)) {
                if (flag2) {
                    flag = true;
                    this.setBlock(generatoraccessseed, blockposition1, worldgenfeaturedeltaconfiguration.rim());
                }

                BlockPosition blockposition2 = blockposition1.offset(i, 0, j);

                if (isClear(generatoraccessseed, blockposition2, worldgenfeaturedeltaconfiguration)) {
                    flag = true;
                    this.setBlock(generatoraccessseed, blockposition2, worldgenfeaturedeltaconfiguration.contents());
                }
            }
        }

        return flag;
    }

    private static boolean isClear(GeneratorAccess generatoraccess, BlockPosition blockposition, WorldGenFeatureDeltaConfiguration worldgenfeaturedeltaconfiguration) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

        if (iblockdata.is(worldgenfeaturedeltaconfiguration.contents().getBlock())) {
            return false;
        } else if (WorldGenFeatureDelta.CANNOT_REPLACE.contains(iblockdata.getBlock())) {
            return false;
        } else {
            EnumDirection[] aenumdirection = WorldGenFeatureDelta.DIRECTIONS;
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];
                boolean flag = generatoraccess.getBlockState(blockposition.relative(enumdirection)).isAir();

                if (flag && enumdirection != EnumDirection.UP || !flag && enumdirection == EnumDirection.UP) {
                    return false;
                }
            }

            return true;
        }
    }
}
