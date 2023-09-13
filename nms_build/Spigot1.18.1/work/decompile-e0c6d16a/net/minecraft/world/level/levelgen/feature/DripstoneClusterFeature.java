package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;

public class DripstoneClusterFeature extends WorldGenerator<DripstoneClusterConfiguration> {

    public DripstoneClusterFeature(Codec<DripstoneClusterConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<DripstoneClusterConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        DripstoneClusterConfiguration dripstoneclusterconfiguration = (DripstoneClusterConfiguration) featureplacecontext.config();
        Random random = featureplacecontext.random();

        if (!DripstoneUtils.isEmptyOrWater(generatoraccessseed, blockposition)) {
            return false;
        } else {
            int i = dripstoneclusterconfiguration.height.sample(random);
            float f = dripstoneclusterconfiguration.wetness.sample(random);
            float f1 = dripstoneclusterconfiguration.density.sample(random);
            int j = dripstoneclusterconfiguration.radius.sample(random);
            int k = dripstoneclusterconfiguration.radius.sample(random);

            for (int l = -j; l <= j; ++l) {
                for (int i1 = -k; i1 <= k; ++i1) {
                    double d0 = this.getChanceOfStalagmiteOrStalactite(j, k, l, i1, dripstoneclusterconfiguration);
                    BlockPosition blockposition1 = blockposition.offset(l, 0, i1);

                    this.placeColumn(generatoraccessseed, random, blockposition1, l, i1, f, d0, i, f1, dripstoneclusterconfiguration);
                }
            }

            return true;
        }
    }

    private void placeColumn(GeneratorAccessSeed generatoraccessseed, Random random, BlockPosition blockposition, int i, int j, float f, double d0, int k, float f1, DripstoneClusterConfiguration dripstoneclusterconfiguration) {
        Optional<Column> optional = Column.scan(generatoraccessseed, blockposition, dripstoneclusterconfiguration.floorToCeilingSearchRange, DripstoneUtils::isEmptyOrWater, DripstoneUtils::isDripstoneBaseOrLava);

        if (optional.isPresent()) {
            OptionalInt optionalint = ((Column) optional.get()).getCeiling();
            OptionalInt optionalint1 = ((Column) optional.get()).getFloor();

            if (optionalint.isPresent() || optionalint1.isPresent()) {
                boolean flag = random.nextFloat() < f;
                Column column;

                if (flag && optionalint1.isPresent() && this.canPlacePool(generatoraccessseed, blockposition.atY(optionalint1.getAsInt()))) {
                    int l = optionalint1.getAsInt();

                    column = ((Column) optional.get()).withFloor(OptionalInt.of(l - 1));
                    generatoraccessseed.setBlock(blockposition.atY(l), Blocks.WATER.defaultBlockState(), 2);
                } else {
                    column = (Column) optional.get();
                }

                OptionalInt optionalint2 = column.getFloor();
                boolean flag1 = random.nextDouble() < d0;
                int i1;
                int j1;

                if (optionalint.isPresent() && flag1 && !this.isLava(generatoraccessseed, blockposition.atY(optionalint.getAsInt()))) {
                    i1 = dripstoneclusterconfiguration.dripstoneBlockLayerThickness.sample(random);
                    this.replaceBlocksWithDripstoneBlocks(generatoraccessseed, blockposition.atY(optionalint.getAsInt()), i1, EnumDirection.UP);
                    int k1;

                    if (optionalint2.isPresent()) {
                        k1 = Math.min(k, optionalint.getAsInt() - optionalint2.getAsInt());
                    } else {
                        k1 = k;
                    }

                    j1 = this.getDripstoneHeight(random, i, j, f1, k1, dripstoneclusterconfiguration);
                } else {
                    j1 = 0;
                }

                boolean flag2 = random.nextDouble() < d0;
                int l1;

                if (optionalint2.isPresent() && flag2 && !this.isLava(generatoraccessseed, blockposition.atY(optionalint2.getAsInt()))) {
                    l1 = dripstoneclusterconfiguration.dripstoneBlockLayerThickness.sample(random);
                    this.replaceBlocksWithDripstoneBlocks(generatoraccessseed, blockposition.atY(optionalint2.getAsInt()), l1, EnumDirection.DOWN);
                    if (optionalint.isPresent()) {
                        i1 = Math.max(0, j1 + MathHelper.randomBetweenInclusive(random, -dripstoneclusterconfiguration.maxStalagmiteStalactiteHeightDiff, dripstoneclusterconfiguration.maxStalagmiteStalactiteHeightDiff));
                    } else {
                        i1 = this.getDripstoneHeight(random, i, j, f1, k, dripstoneclusterconfiguration);
                    }
                } else {
                    i1 = 0;
                }

                int i2;

                if (optionalint.isPresent() && optionalint2.isPresent() && optionalint.getAsInt() - j1 <= optionalint2.getAsInt() + i1) {
                    int j2 = optionalint2.getAsInt();
                    int k2 = optionalint.getAsInt();
                    int l2 = Math.max(k2 - j1, j2 + 1);
                    int i3 = Math.min(j2 + i1, k2 - 1);
                    int j3 = MathHelper.randomBetweenInclusive(random, l2, i3 + 1);
                    int k3 = j3 - 1;

                    l1 = k2 - j3;
                    i2 = k3 - j2;
                } else {
                    l1 = j1;
                    i2 = i1;
                }

                boolean flag3 = random.nextBoolean() && l1 > 0 && i2 > 0 && column.getHeight().isPresent() && l1 + i2 == column.getHeight().getAsInt();

                if (optionalint.isPresent()) {
                    DripstoneUtils.growPointedDripstone(generatoraccessseed, blockposition.atY(optionalint.getAsInt() - 1), EnumDirection.DOWN, l1, flag3);
                }

                if (optionalint2.isPresent()) {
                    DripstoneUtils.growPointedDripstone(generatoraccessseed, blockposition.atY(optionalint2.getAsInt() + 1), EnumDirection.UP, i2, flag3);
                }

            }
        }
    }

    private boolean isLava(IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getBlockState(blockposition).is(Blocks.LAVA);
    }

    private int getDripstoneHeight(Random random, int i, int j, float f, int k, DripstoneClusterConfiguration dripstoneclusterconfiguration) {
        if (random.nextFloat() > f) {
            return 0;
        } else {
            int l = Math.abs(i) + Math.abs(j);
            float f1 = (float) MathHelper.clampedMap((double) l, 0.0D, (double) dripstoneclusterconfiguration.maxDistanceFromCenterAffectingHeightBias, (double) k / 2.0D, 0.0D);

            return (int) randomBetweenBiased(random, 0.0F, (float) k, f1, (float) dripstoneclusterconfiguration.heightDeviation);
        }
    }

    private boolean canPlacePool(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition);

        if (!iblockdata.is(Blocks.WATER) && !iblockdata.is(Blocks.DRIPSTONE_BLOCK) && !iblockdata.is(Blocks.POINTED_DRIPSTONE)) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection;

            do {
                if (!iterator.hasNext()) {
                    return this.canBeAdjacentToWater(generatoraccessseed, blockposition.below());
                }

                enumdirection = (EnumDirection) iterator.next();
            } while (this.canBeAdjacentToWater(generatoraccessseed, blockposition.relative(enumdirection)));

            return false;
        } else {
            return false;
        }
    }

    private boolean canBeAdjacentToWater(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

        return iblockdata.is((Tag) TagsBlock.BASE_STONE_OVERWORLD) || iblockdata.getFluidState().is((Tag) TagsFluid.WATER);
    }

    private void replaceBlocksWithDripstoneBlocks(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, int i, EnumDirection enumdirection) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int j = 0; j < i; ++j) {
            if (!DripstoneUtils.placeDripstoneBlockIfPossible(generatoraccessseed, blockposition_mutableblockposition)) {
                return;
            }

            blockposition_mutableblockposition.move(enumdirection);
        }

    }

    private double getChanceOfStalagmiteOrStalactite(int i, int j, int k, int l, DripstoneClusterConfiguration dripstoneclusterconfiguration) {
        int i1 = i - Math.abs(k);
        int j1 = j - Math.abs(l);
        int k1 = Math.min(i1, j1);

        return (double) MathHelper.clampedMap((float) k1, 0.0F, (float) dripstoneclusterconfiguration.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn, dripstoneclusterconfiguration.chanceOfDripstoneColumnAtMaxDistanceFromCenter, 1.0F);
    }

    private static float randomBetweenBiased(Random random, float f, float f1, float f2, float f3) {
        return ClampedNormalFloat.sample(random, f2, f3, f, f1);
    }
}
