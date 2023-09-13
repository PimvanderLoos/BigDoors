package net.minecraft.world.level.redstone;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public interface NeighborUpdater {

    EnumDirection[] UPDATE_ORDER = new EnumDirection[]{EnumDirection.WEST, EnumDirection.EAST, EnumDirection.DOWN, EnumDirection.UP, EnumDirection.NORTH, EnumDirection.SOUTH};

    void shapeUpdate(EnumDirection enumdirection, IBlockData iblockdata, BlockPosition blockposition, BlockPosition blockposition1, int i, int j);

    void neighborChanged(BlockPosition blockposition, Block block, BlockPosition blockposition1);

    void neighborChanged(IBlockData iblockdata, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag);

    default void updateNeighborsAtExceptFromFacing(BlockPosition blockposition, Block block, @Nullable EnumDirection enumdirection) {
        EnumDirection[] aenumdirection = NeighborUpdater.UPDATE_ORDER;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection1 = aenumdirection[j];

            if (enumdirection1 != enumdirection) {
                this.neighborChanged(blockposition.relative(enumdirection1), block, blockposition);
            }
        }

    }

    static void executeShapeUpdate(GeneratorAccess generatoraccess, EnumDirection enumdirection, IBlockData iblockdata, BlockPosition blockposition, BlockPosition blockposition1, int i, int j) {
        IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition);
        IBlockData iblockdata2 = iblockdata1.updateShape(enumdirection, iblockdata, generatoraccess, blockposition, blockposition1);

        Block.updateOrDestroy(iblockdata1, iblockdata2, generatoraccess, blockposition, i, j);
    }

    static void executeUpdate(World world, IBlockData iblockdata, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        try {
            iblockdata.neighborChanged(world, blockposition, block, blockposition1, flag);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception while updating neighbours");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Block being updated");

            crashreportsystemdetails.setDetail("Source block type", () -> {
                try {
                    return String.format(Locale.ROOT, "ID #%s (%s // %s)", BuiltInRegistries.BLOCK.getKey(block), block.getDescriptionId(), block.getClass().getCanonicalName());
                } catch (Throwable throwable1) {
                    return "ID #" + BuiltInRegistries.BLOCK.getKey(block);
                }
            });
            CrashReportSystemDetails.populateBlockDetails(crashreportsystemdetails, world, blockposition, iblockdata);
            throw new ReportedException(crashreport);
        }
    }
}
