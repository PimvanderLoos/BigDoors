package net.minecraft.world.level.levelgen.feature;

import java.util.function.Consumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
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

    protected static double a(double d0, double d1, double d2, double d3) {
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

    protected static boolean a(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, int i) {
        if (b(generatoraccessseed, blockposition)) {
            return false;
        } else {
            float f = 6.0F;
            float f1 = 6.0F / (float) i;

            for (float f2 = 0.0F; f2 < 6.2831855F; f2 += f1) {
                int j = (int) (MathHelper.cos(f2) * (float) i);
                int k = (int) (MathHelper.sin(f2) * (float) i);

                if (b(generatoraccessseed, blockposition.c(j, 0, k))) {
                    return false;
                }
            }

            return true;
        }
    }

    protected static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return generatoraccess.a(blockposition, DripstoneUtils::c);
    }

    protected static boolean b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return generatoraccess.a(blockposition, DripstoneUtils::d);
    }

    protected static void a(EnumDirection enumdirection, int i, boolean flag, Consumer<IBlockData> consumer) {
        if (i >= 3) {
            consumer.accept(a(enumdirection, DripstoneThickness.BASE));

            for (int j = 0; j < i - 3; ++j) {
                consumer.accept(a(enumdirection, DripstoneThickness.MIDDLE));
            }
        }

        if (i >= 2) {
            consumer.accept(a(enumdirection, DripstoneThickness.FRUSTUM));
        }

        if (i >= 1) {
            consumer.accept(a(enumdirection, flag ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
        }

    }

    protected static void a(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, EnumDirection enumdirection, int i, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        a(enumdirection, i, flag, (iblockdata) -> {
            if (iblockdata.a(Blocks.POINTED_DRIPSTONE)) {
                iblockdata = (IBlockData) iblockdata.set(PointedDripstoneBlock.WATERLOGGED, generatoraccessseed.B(blockposition_mutableblockposition));
            }

            generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
            blockposition_mutableblockposition.c(enumdirection);
        });
    }

    protected static boolean a(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccessseed.getType(blockposition);

        if (iblockdata.a((Tag) TagsBlock.DRIPSTONE_REPLACEABLE)) {
            generatoraccessseed.setTypeAndData(blockposition, Blocks.DRIPSTONE_BLOCK.getBlockData(), 2);
            return true;
        } else {
            return false;
        }
    }

    private static IBlockData a(EnumDirection enumdirection, DripstoneThickness dripstonethickness) {
        return (IBlockData) ((IBlockData) Blocks.POINTED_DRIPSTONE.getBlockData().set(PointedDripstoneBlock.TIP_DIRECTION, enumdirection)).set(PointedDripstoneBlock.THICKNESS, dripstonethickness);
    }

    public static boolean a(IBlockData iblockdata) {
        return b(iblockdata) || iblockdata.a(Blocks.LAVA);
    }

    public static boolean b(IBlockData iblockdata) {
        return iblockdata.a(Blocks.DRIPSTONE_BLOCK) || iblockdata.a((Tag) TagsBlock.DRIPSTONE_REPLACEABLE);
    }

    public static boolean c(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.a(Blocks.WATER);
    }

    public static boolean d(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.a(Blocks.WATER) || iblockdata.a(Blocks.LAVA);
    }
}
