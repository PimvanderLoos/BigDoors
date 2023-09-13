package net.minecraft.world.entity.vehicle;

import java.util.Iterator;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.ICollisionAccess;
import net.minecraft.world.level.block.BlockTrapdoor;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class DismountUtil {

    public DismountUtil() {}

    public static int[][] offsetsForDirection(EnumDirection enumdirection) {
        EnumDirection enumdirection1 = enumdirection.getClockWise();
        EnumDirection enumdirection2 = enumdirection1.getOpposite();
        EnumDirection enumdirection3 = enumdirection.getOpposite();

        return new int[][]{{enumdirection1.getStepX(), enumdirection1.getStepZ()}, {enumdirection2.getStepX(), enumdirection2.getStepZ()}, {enumdirection3.getStepX() + enumdirection1.getStepX(), enumdirection3.getStepZ() + enumdirection1.getStepZ()}, {enumdirection3.getStepX() + enumdirection2.getStepX(), enumdirection3.getStepZ() + enumdirection2.getStepZ()}, {enumdirection.getStepX() + enumdirection1.getStepX(), enumdirection.getStepZ() + enumdirection1.getStepZ()}, {enumdirection.getStepX() + enumdirection2.getStepX(), enumdirection.getStepZ() + enumdirection2.getStepZ()}, {enumdirection3.getStepX(), enumdirection3.getStepZ()}, {enumdirection.getStepX(), enumdirection.getStepZ()}};
    }

    public static boolean isBlockFloorValid(double d0) {
        return !Double.isInfinite(d0) && d0 < 1.0D;
    }

    public static boolean canDismountTo(ICollisionAccess icollisionaccess, EntityLiving entityliving, AxisAlignedBB axisalignedbb) {
        Iterable<VoxelShape> iterable = icollisionaccess.getBlockCollisions(entityliving, axisalignedbb);
        Iterator iterator = iterable.iterator();

        VoxelShape voxelshape;

        do {
            if (!iterator.hasNext()) {
                if (!icollisionaccess.getWorldBorder().isWithinBounds(axisalignedbb)) {
                    return false;
                }

                return true;
            }

            voxelshape = (VoxelShape) iterator.next();
        } while (voxelshape.isEmpty());

        return false;
    }

    public static boolean canDismountTo(ICollisionAccess icollisionaccess, Vec3D vec3d, EntityLiving entityliving, EntityPose entitypose) {
        return canDismountTo(icollisionaccess, entityliving, entityliving.getLocalBoundsForPose(entitypose).move(vec3d));
    }

    public static VoxelShape nonClimbableShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getBlockState(blockposition);

        return !iblockdata.is(TagsBlock.CLIMBABLE) && (!(iblockdata.getBlock() instanceof BlockTrapdoor) || !(Boolean) iblockdata.getValue(BlockTrapdoor.OPEN)) ? iblockdata.getCollisionShape(iblockaccess, blockposition) : VoxelShapes.empty();
    }

    public static double findCeilingFrom(BlockPosition blockposition, int i, Function<BlockPosition, VoxelShape> function) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        int j = 0;

        while (j < i) {
            VoxelShape voxelshape = (VoxelShape) function.apply(blockposition_mutableblockposition);

            if (!voxelshape.isEmpty()) {
                return (double) (blockposition.getY() + j) + voxelshape.min(EnumDirection.EnumAxis.Y);
            }

            ++j;
            blockposition_mutableblockposition.move(EnumDirection.UP);
        }

        return Double.POSITIVE_INFINITY;
    }

    @Nullable
    public static Vec3D findSafeDismountLocation(EntityTypes<?> entitytypes, ICollisionAccess icollisionaccess, BlockPosition blockposition, boolean flag) {
        if (flag && entitytypes.isBlockDangerous(icollisionaccess.getBlockState(blockposition))) {
            return null;
        } else {
            double d0 = icollisionaccess.getBlockFloorHeight(nonClimbableShape(icollisionaccess, blockposition), () -> {
                return nonClimbableShape(icollisionaccess, blockposition.below());
            });

            if (!isBlockFloorValid(d0)) {
                return null;
            } else if (flag && d0 <= 0.0D && entitytypes.isBlockDangerous(icollisionaccess.getBlockState(blockposition.below()))) {
                return null;
            } else {
                Vec3D vec3d = Vec3D.upFromBottomCenterOf(blockposition, d0);
                AxisAlignedBB axisalignedbb = entitytypes.getDimensions().makeBoundingBox(vec3d);
                Iterable<VoxelShape> iterable = icollisionaccess.getBlockCollisions((Entity) null, axisalignedbb);
                Iterator iterator = iterable.iterator();

                while (iterator.hasNext()) {
                    VoxelShape voxelshape = (VoxelShape) iterator.next();

                    if (!voxelshape.isEmpty()) {
                        return null;
                    }
                }

                return entitytypes == EntityTypes.PLAYER && (icollisionaccess.getBlockState(blockposition).is(TagsBlock.INVALID_SPAWN_INSIDE) || icollisionaccess.getBlockState(blockposition.above()).is(TagsBlock.INVALID_SPAWN_INSIDE)) ? null : (!icollisionaccess.getWorldBorder().isWithinBounds(axisalignedbb) ? null : vec3d);
            }
        }
    }
}
