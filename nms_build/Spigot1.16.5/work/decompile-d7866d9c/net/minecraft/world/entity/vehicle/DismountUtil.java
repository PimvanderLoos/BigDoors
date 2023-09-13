package net.minecraft.world.entity.vehicle;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
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

    public static int[][] a(EnumDirection enumdirection) {
        EnumDirection enumdirection1 = enumdirection.g();
        EnumDirection enumdirection2 = enumdirection1.opposite();
        EnumDirection enumdirection3 = enumdirection.opposite();

        return new int[][]{{enumdirection1.getAdjacentX(), enumdirection1.getAdjacentZ()}, {enumdirection2.getAdjacentX(), enumdirection2.getAdjacentZ()}, {enumdirection3.getAdjacentX() + enumdirection1.getAdjacentX(), enumdirection3.getAdjacentZ() + enumdirection1.getAdjacentZ()}, {enumdirection3.getAdjacentX() + enumdirection2.getAdjacentX(), enumdirection3.getAdjacentZ() + enumdirection2.getAdjacentZ()}, {enumdirection.getAdjacentX() + enumdirection1.getAdjacentX(), enumdirection.getAdjacentZ() + enumdirection1.getAdjacentZ()}, {enumdirection.getAdjacentX() + enumdirection2.getAdjacentX(), enumdirection.getAdjacentZ() + enumdirection2.getAdjacentZ()}, {enumdirection3.getAdjacentX(), enumdirection3.getAdjacentZ()}, {enumdirection.getAdjacentX(), enumdirection.getAdjacentZ()}};
    }

    public static boolean a(double d0) {
        return !Double.isInfinite(d0) && d0 < 1.0D;
    }

    public static boolean a(ICollisionAccess icollisionaccess, EntityLiving entityliving, AxisAlignedBB axisalignedbb) {
        return icollisionaccess.b(entityliving, axisalignedbb).allMatch(VoxelShape::isEmpty);
    }

    @Nullable
    public static Vec3D a(ICollisionAccess icollisionaccess, double d0, double d1, double d2, EntityLiving entityliving, EntityPose entitypose) {
        if (a(d1)) {
            Vec3D vec3d = new Vec3D(d0, d1, d2);

            if (a(icollisionaccess, entityliving, entityliving.f(entitypose).c(vec3d))) {
                return vec3d;
            }
        }

        return null;
    }

    public static VoxelShape a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        return !iblockdata.a((Tag) TagsBlock.CLIMBABLE) && (!(iblockdata.getBlock() instanceof BlockTrapdoor) || !(Boolean) iblockdata.get(BlockTrapdoor.OPEN)) ? iblockdata.getCollisionShape(iblockaccess, blockposition) : VoxelShapes.a();
    }

    public static double a(BlockPosition blockposition, int i, Function<BlockPosition, VoxelShape> function) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        int j = 0;

        while (j < i) {
            VoxelShape voxelshape = (VoxelShape) function.apply(blockposition_mutableblockposition);

            if (!voxelshape.isEmpty()) {
                return (double) (blockposition.getY() + j) + voxelshape.b(EnumDirection.EnumAxis.Y);
            }

            ++j;
            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

        return Double.POSITIVE_INFINITY;
    }

    @Nullable
    public static Vec3D a(EntityTypes<?> entitytypes, ICollisionAccess icollisionaccess, BlockPosition blockposition, boolean flag) {
        if (flag && entitytypes.a(icollisionaccess.getType(blockposition))) {
            return null;
        } else {
            double d0 = icollisionaccess.a(a((IBlockAccess) icollisionaccess, blockposition), () -> {
                return a((IBlockAccess) icollisionaccess, blockposition.down());
            });

            if (!a(d0)) {
                return null;
            } else if (flag && d0 <= 0.0D && entitytypes.a(icollisionaccess.getType(blockposition.down()))) {
                return null;
            } else {
                Vec3D vec3d = Vec3D.a((BaseBlockPosition) blockposition, d0);

                return icollisionaccess.b((Entity) null, entitytypes.l().a(vec3d)).allMatch(VoxelShape::isEmpty) ? vec3d : null;
            }
        }
    }
}
