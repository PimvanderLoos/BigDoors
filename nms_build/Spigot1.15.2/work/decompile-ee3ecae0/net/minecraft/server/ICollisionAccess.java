package net.minecraft.server;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

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

    default boolean i(Entity entity) {
        return this.a(entity, VoxelShapes.a(entity.getBoundingBox()));
    }

    default boolean a(AxisAlignedBB axisalignedbb) {
        return this.a((Entity) null, axisalignedbb, Collections.emptySet());
    }

    default boolean getCubes(Entity entity) {
        return this.a(entity, entity.getBoundingBox(), Collections.emptySet());
    }

    default boolean getCubes(Entity entity, AxisAlignedBB axisalignedbb) {
        return this.a(entity, axisalignedbb, Collections.emptySet());
    }

    default boolean a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set) {
        return this.c(entity, axisalignedbb, set).allMatch(VoxelShape::isEmpty);
    }

    default Stream<VoxelShape> b(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set) {
        return Stream.empty();
    }

    default Stream<VoxelShape> c(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set) {
        return Streams.concat(new Stream[]{this.b(entity, axisalignedbb), this.b(entity, axisalignedbb, set)});
    }

    default Stream<VoxelShape> b(@Nullable final Entity entity, AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX - 1.0E-7D) - 1;
        int j = MathHelper.floor(axisalignedbb.maxX + 1.0E-7D) + 1;
        int k = MathHelper.floor(axisalignedbb.minY - 1.0E-7D) - 1;
        int l = MathHelper.floor(axisalignedbb.maxY + 1.0E-7D) + 1;
        int i1 = MathHelper.floor(axisalignedbb.minZ - 1.0E-7D) - 1;
        int j1 = MathHelper.floor(axisalignedbb.maxZ + 1.0E-7D) + 1;
        final VoxelShapeCollision voxelshapecollision = entity == null ? VoxelShapeCollision.a() : VoxelShapeCollision.a(entity);
        final CursorPosition cursorposition = new CursorPosition(i, k, i1, j, l, j1);
        final BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        final VoxelShape voxelshape = VoxelShapes.a(axisalignedbb);

        return StreamSupport.stream(new AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280) {
            boolean a = entity == null;

            public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
                if (!this.a) {
                    this.a = true;
                    VoxelShape voxelshape1 = ICollisionAccess.this.getWorldBorder().a();
                    boolean flag = VoxelShapes.c(voxelshape1, VoxelShapes.a(entity.getBoundingBox().shrink(1.0E-7D)), OperatorBoolean.AND);
                    boolean flag1 = VoxelShapes.c(voxelshape1, VoxelShapes.a(entity.getBoundingBox().g(1.0E-7D)), OperatorBoolean.AND);

                    if (!flag && flag1) {
                        consumer.accept(voxelshape1);
                        return true;
                    }
                }

                while (cursorposition.a()) {
                    int k1 = cursorposition.b();
                    int l1 = cursorposition.c();
                    int i2 = cursorposition.d();
                    int j2 = cursorposition.e();

                    if (j2 != 3) {
                        int k2 = k1 >> 4;
                        int l2 = i2 >> 4;
                        IBlockAccess iblockaccess = ICollisionAccess.this.c(k2, l2);

                        if (iblockaccess != null) {
                            blockposition_mutableblockposition.d(k1, l1, i2);
                            IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);

                            if ((j2 != 1 || iblockdata.f()) && (j2 != 2 || iblockdata.getBlock() == Blocks.MOVING_PISTON)) {
                                VoxelShape voxelshape2 = iblockdata.b((IBlockAccess) ICollisionAccess.this, blockposition_mutableblockposition, voxelshapecollision);
                                VoxelShape voxelshape3 = voxelshape2.a((double) k1, (double) l1, (double) i2);

                                if (VoxelShapes.c(voxelshape, voxelshape3, OperatorBoolean.AND)) {
                                    consumer.accept(voxelshape3);
                                    return true;
                                }
                            }
                        }
                    }
                }

                return false;
            }
        }, false);
    }
}
