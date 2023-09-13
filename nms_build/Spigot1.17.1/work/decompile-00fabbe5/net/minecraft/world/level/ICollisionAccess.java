package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
    IBlockAccess c(int i, int j);

    default boolean a(@Nullable Entity entity, VoxelShape voxelshape) {
        return true;
    }

    default boolean a(IBlockData iblockdata, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        VoxelShape voxelshape = iblockdata.b((IBlockAccess) this, blockposition, voxelshapecollision);

        return voxelshape.isEmpty() || this.a((Entity) null, voxelshape.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()));
    }

    default boolean f(Entity entity) {
        return this.a(entity, VoxelShapes.a(entity.getBoundingBox()));
    }

    default boolean b(AxisAlignedBB axisalignedbb) {
        return this.b((Entity) null, axisalignedbb, (entity) -> {
            return true;
        });
    }

    default boolean getCubes(Entity entity) {
        return this.b(entity, entity.getBoundingBox(), (entity1) -> {
            return true;
        });
    }

    default boolean getCubes(Entity entity, AxisAlignedBB axisalignedbb) {
        return this.b(entity, axisalignedbb, (entity1) -> {
            return true;
        });
    }

    default boolean b(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate) {
        return this.d(entity, axisalignedbb, predicate).allMatch(VoxelShape::isEmpty);
    }

    Stream<VoxelShape> c(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate);

    default Stream<VoxelShape> d(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Predicate<Entity> predicate) {
        return Stream.concat(this.b(entity, axisalignedbb), this.c(entity, axisalignedbb, predicate));
    }

    default Stream<VoxelShape> b(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return StreamSupport.stream(new VoxelShapeSpliterator(this, entity, axisalignedbb), false);
    }

    default boolean a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, BiPredicate<IBlockData, BlockPosition> bipredicate) {
        return !this.b(entity, axisalignedbb, bipredicate).allMatch(VoxelShape::isEmpty);
    }

    default Stream<VoxelShape> b(@Nullable Entity entity, AxisAlignedBB axisalignedbb, BiPredicate<IBlockData, BlockPosition> bipredicate) {
        return StreamSupport.stream(new VoxelShapeSpliterator(this, entity, axisalignedbb, bipredicate), false);
    }

    default Optional<Vec3D> a(@Nullable Entity entity, VoxelShape voxelshape, Vec3D vec3d, double d0, double d1, double d2) {
        if (voxelshape.isEmpty()) {
            return Optional.empty();
        } else {
            AxisAlignedBB axisalignedbb = voxelshape.getBoundingBox().grow(d0, d1, d2);
            VoxelShape voxelshape1 = (VoxelShape) this.b(entity, axisalignedbb).flatMap((voxelshape2) -> {
                return voxelshape2.toList().stream();
            }).map((axisalignedbb1) -> {
                return axisalignedbb1.grow(d0 / 2.0D, d1 / 2.0D, d2 / 2.0D);
            }).map(VoxelShapes::a).reduce(VoxelShapes.a(), VoxelShapes::a);
            VoxelShape voxelshape2 = VoxelShapes.a(voxelshape, voxelshape1, OperatorBoolean.ONLY_FIRST);

            return voxelshape2.a(vec3d);
        }
    }
}
