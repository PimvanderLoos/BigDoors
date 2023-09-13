package net.minecraft.world.level.levelgen.feature;

import java.util.function.Consumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

public class DripstoneUtils {

    public DripstoneUtils() {}

    protected static double getDripstoneHeight(double d0, double d1, double d2, double d3) {
        if (d0 < d3) {
            d0 = d3;
        }

        double d4 = 0.384D;
        double d5 = d0 / d1 * 0.384D;
        double d6 = 0.75D * Math.pow(d5, 1.3333333333333333D);
        double d7 = Math.pow(d5, 0.6666666666666666D);
        double d8 = 0.3333333333333333D * Math.log(d5);
        double d9 = d2 * (d6 - d7 - d8);

        d9 = Math.max(d9, 0.0D);
        return d9 / 0.384D * d1;
    }

    protected static boolean isCircleMostlyEmbeddedInStone(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, int i) {
        if (isEmptyOrWaterOrLava(generatoraccessseed, blockposition)) {
            return false;
        } else {
            float f = 6.0F;
            float f1 = 6.0F / (float) i;

            for (float f2 = 0.0F; f2 < 6.2831855F; f2 += f1) {
                int j = (int) (MathHelper.cos(f2) * (float) i);
                int k = (int) (MathHelper.sin(f2) * (float) i);

                if (isEmptyOrWaterOrLava(generatoraccessseed, blockposition.offset(j, 0, k))) {
                    return false;
                }
            }

            return true;
        }
    }

    protected static boolean isEmptyOrWater(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return generatoraccess.isStateAtPosition(blockposition, DripstoneUtils::isEmptyOrWater);
    }

    protected static boolean isEmptyOrWaterOrLava(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return generatoraccess.isStateAtPosition(blockposition, DripstoneUtils::isEmptyOrWaterOrLava);
    }

    protected static void buildBaseToTipColumn(EnumDirection enumdirection, int i, boolean flag, Consumer<IBlockData> consumer) {
        if (i >= 3) {
            consumer.accept(createPointedDripstone(enumdirection, DripstoneThickness.BASE));

            for (int j = 0; j < i - 3; ++j) {
                consumer.accept(createPointedDripstone(enumdirection, DripstoneThickness.MIDDLE));
            }
        }

        if (i >= 2) {
            consumer.accept(createPointedDripstone(enumdirection, DripstoneThickness.FRUSTUM));
        }

        if (i >= 1) {
            consumer.accept(createPointedDripstone(enumdirection, flag ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
        }

    }

    protected static void growPointedDripstone(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, int i, boolean flag) {
        if (isDripstoneBase(generatoraccess.getBlockState(blockposition.relative(enumdirection.getOpposite())))) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

            buildBaseToTipColumn(enumdirection, i, flag, (iblockdata) -> {
                if (iblockdata.is(Blocks.POINTED_DRIPSTONE)) {
                    iblockdata = (IBlockData) iblockdata.setValue(PointedDripstoneBlock.WATERLOGGED, generatoraccess.isWaterAt(blockposition_mutableblockposition));
                }

                generatoraccess.setBlock(blockposition_mutableblockposition, iblockdata, 2);
                blockposition_mutableblockposition.move(enumdirection);
            });
        }
    }

    protected static boolean placeDripstoneBlockIfPossible(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

        if (iblockdata.is(TagsBlock.DRIPSTONE_REPLACEABLE)) {
            generatoraccess.setBlock(blockposition, Blocks.DRIPSTONE_BLOCK.defaultBlockState(), 2);
            return true;
        } else {
            return false;
        }
    }

    private static IBlockData createPointedDripstone(EnumDirection enumdirection, DripstoneThickness dripstonethickness) {
        return (IBlockData) ((IBlockData) Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, enumdirection)).setValue(PointedDripstoneBlock.THICKNESS, dripstonethickness);
    }

    public static boolean isDripstoneBaseOrLava(IBlockData iblockdata) {
        return isDripstoneBase(iblockdata) || iblockdata.is(Blocks.LAVA);
    }

    public static boolean isDripstoneBase(IBlockData iblockdata) {
        return iblockdata.is(Blocks.DRIPSTONE_BLOCK) || iblockdata.is(TagsBlock.DRIPSTONE_REPLACEABLE);
    }

    public static boolean isEmptyOrWater(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.is(Blocks.WATER);
    }

    public static boolean isNeitherEmptyNorWater(IBlockData iblockdata) {
        return !iblockdata.isAir() && !iblockdata.is(Blocks.WATER);
    }

    public static boolean isEmptyOrWaterOrLava(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.is(Blocks.WATER) || iblockdata.is(Blocks.LAVA);
    }
}
