package net.minecraft.world.level;

import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public interface ICollisionAccess extends IBlockAccess {

    WorldBorder getWorldBorder();

    @Nullable
    IBlockAccess getChunkForCollisions(int i, int j);

    default boolean isUnobstructed(@Nullable Entity entity, VoxelShape voxelshape) {
        return true;
    }

    default boolean isUnobstructed(IBlockData iblockdata, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        VoxelShape voxelshape = iblockdata.getCollisionShape(this, blockposition, voxelshapecollision);

        return voxelshape.isEmpty() || this.isUnobstructed((Entity) null, voxelshape.move((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()));
    }

    default boolean isUnobstructed(Entity entity) {
        return this.isUnobstructed(entity, VoxelShapes.create(entity.getBoundingBox()));
    }

    default boolean noCollision(AxisAlignedBB axisalignedbb) {
        return this.noCollision((Entity) null, axisalignedbb);
    }

    default boolean noCollision(Entity entity) {
        return this.noCollision(entity, entity.getBoundingBox());
    }

    default boolean noCollision(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        Iterator iterator = this.getBlockCollisions(entity, axisalignedbb).iterator();

        while (iterator.hasNext()) {
            VoxelShape voxelshape = (VoxelShape) iterator.next();

            if (!voxelshape.isEmpty()) {
                return false;
            }
        }

        if (!this.getEntityCollisions(entity, axisalignedbb).isEmpty()) {
            return false;
        } else if (entity == null) {
            return true;
        } else {
            VoxelShape voxelshape1 = this.borderCollision(entity, axisalignedbb);

            return voxelshape1 == null || !VoxelShapes.joinIsNotEmpty(voxelshape1, VoxelShapes.create(axisalignedbb), OperatorBoolean.AND);
        }
    }

    List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AxisAlignedBB axisalignedbb);

    default Iterable<VoxelShape> getCollisions(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        List<VoxelShape> list = this.getEntityCollisions(entity, axisalignedbb);
        Iterable<VoxelShape> iterable = this.getBlockCollisions(entity, axisalignedbb);

        return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
    }

    default Iterable<VoxelShape> getBlockCollisions(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return () -> {
            return new VoxelShapeSpliterator(this, entity, axisalignedbb);
        };
    }

    @Nullable
    private default VoxelShape borderCollision(Entity entity, AxisAlignedBB axisalignedbb) {
        WorldBorder worldborder = this.getWorldBorder();

        return worldborder.isInsideCloseToBorder(entity, axisalignedbb) ? worldborder.getCollisionShape() : null;
    }

    default boolean collidesWithSuffocatingBlock(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        VoxelShapeSpliterator voxelshapespliterator = new VoxelShapeSpliterator(this, entity, axisalignedbb, true);

        do {
            if (!voxelshapespliterator.hasNext()) {
                return false;
            }
        } while (((VoxelShape) voxelshapespliterator.next()).isEmpty());

        return true;
    }

    default Optional<Vec3D> findFreePosition(@Nullable Entity entity, VoxelShape voxelshape, Vec3D vec3d, double d0, double d1, double d2) {
        if (voxelshape.isEmpty()) {
            return Optional.empty();
        } else {
            AxisAlignedBB axisalignedbb = voxelshape.bounds().inflate(d0, d1, d2);
            VoxelShape voxelshape1 = (VoxelShape) StreamSupport.stream(this.getBlockCollisions(entity, axisalignedbb).spliterator(), false).filter((voxelshape2) -> {
                return this.getWorldBorder() == null || this.getWorldBorder().isWithinBounds(voxelshape2.bounds());
            }).flatMap((voxelshape2) -> {
                return voxelshape2.toAabbs().stream();
            }).map((axisalignedbb1) -> {
                return axisalignedbb1.inflate(d0 / 2.0D, d1 / 2.0D, d2 / 2.0D);
            }).map(VoxelShapes::create).reduce(VoxelShapes.empty(), VoxelShapes::or);
            VoxelShape voxelshape2 = VoxelShapes.join(voxelshape, voxelshape1, OperatorBoolean.ONLY_FIRST);

            return voxelshape2.closestPointTo(vec3d);
        }
    }
}
