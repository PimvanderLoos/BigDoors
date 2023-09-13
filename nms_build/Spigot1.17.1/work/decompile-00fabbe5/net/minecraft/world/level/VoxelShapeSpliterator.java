package net.minecraft.world.level;

import java.util.Objects;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.CursorPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class VoxelShapeSpliterator extends AbstractSpliterator<VoxelShape> {

    @Nullable
    private final Entity source;
    private final AxisAlignedBB box;
    private final VoxelShapeCollision context;
    private final CursorPosition cursor;
    private final BlockPosition.MutableBlockPosition pos;
    private final VoxelShape entityShape;
    private final ICollisionAccess collisionGetter;
    private boolean needsBorderCheck;
    private final BiPredicate<IBlockData, BlockPosition> predicate;

    public VoxelShapeSpliterator(ICollisionAccess icollisionaccess, @Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        this(icollisionaccess, entity, axisalignedbb, (iblockdata, blockposition) -> {
            return true;
        });
    }

    public VoxelShapeSpliterator(ICollisionAccess icollisionaccess, @Nullable Entity entity, AxisAlignedBB axisalignedbb, BiPredicate<IBlockData, BlockPosition> bipredicate) {
        super(Long.MAX_VALUE, 1280);
        this.context = entity == null ? VoxelShapeCollision.a() : VoxelShapeCollision.a(entity);
        this.pos = new BlockPosition.MutableBlockPosition();
        this.entityShape = VoxelShapes.a(axisalignedbb);
        this.collisionGetter = icollisionaccess;
        this.needsBorderCheck = entity != null;
        this.source = entity;
        this.box = axisalignedbb;
        this.predicate = bipredicate;
        int i = MathHelper.floor(axisalignedbb.minX - 1.0E-7D) - 1;
        int j = MathHelper.floor(axisalignedbb.maxX + 1.0E-7D) + 1;
        int k = MathHelper.floor(axisalignedbb.minY - 1.0E-7D) - 1;
        int l = MathHelper.floor(axisalignedbb.maxY + 1.0E-7D) + 1;
        int i1 = MathHelper.floor(axisalignedbb.minZ - 1.0E-7D) - 1;
        int j1 = MathHelper.floor(axisalignedbb.maxZ + 1.0E-7D) + 1;

        this.cursor = new CursorPosition(i, k, i1, j, l, j1);
    }

    public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
        return this.needsBorderCheck && this.b(consumer) || this.a(consumer);
    }

    boolean a(Consumer<? super VoxelShape> consumer) {
        while (true) {
            if (this.cursor.a()) {
                int i = this.cursor.b();
                int j = this.cursor.c();
                int k = this.cursor.d();
                int l = this.cursor.e();

                if (l == 3) {
                    continue;
                }

                IBlockAccess iblockaccess = this.a(i, k);

                if (iblockaccess == null) {
                    continue;
                }

                this.pos.d(i, j, k);
                IBlockData iblockdata = iblockaccess.getType(this.pos);

                if (!this.predicate.test(iblockdata, this.pos) || l == 1 && !iblockdata.d() || l == 2 && !iblockdata.a(Blocks.MOVING_PISTON)) {
                    continue;
                }

                VoxelShape voxelshape = iblockdata.b((IBlockAccess) this.collisionGetter, this.pos, this.context);

                if (voxelshape == VoxelShapes.b()) {
                    if (!this.box.a((double) i, (double) j, (double) k, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D)) {
                        continue;
                    }

                    consumer.accept(voxelshape.a((double) i, (double) j, (double) k));
                    return true;
                }

                VoxelShape voxelshape1 = voxelshape.a((double) i, (double) j, (double) k);

                if (!VoxelShapes.c(voxelshape1, this.entityShape, OperatorBoolean.AND)) {
                    continue;
                }

                consumer.accept(voxelshape1);
                return true;
            }

            return false;
        }
    }

    @Nullable
    private IBlockAccess a(int i, int j) {
        int k = SectionPosition.a(i);
        int l = SectionPosition.a(j);

        return this.collisionGetter.c(k, l);
    }

    boolean b(Consumer<? super VoxelShape> consumer) {
        Objects.requireNonNull(this.source);
        this.needsBorderCheck = false;
        WorldBorder worldborder = this.collisionGetter.getWorldBorder();
        AxisAlignedBB axisalignedbb = this.source.getBoundingBox();

        if (!a(worldborder, axisalignedbb)) {
            VoxelShape voxelshape = worldborder.c();

            if (!b(voxelshape, axisalignedbb) && a(voxelshape, axisalignedbb)) {
                consumer.accept(voxelshape);
                return true;
            }
        }

        return false;
    }

    private static boolean a(VoxelShape voxelshape, AxisAlignedBB axisalignedbb) {
        return VoxelShapes.c(voxelshape, VoxelShapes.a(axisalignedbb.g(1.0E-7D)), OperatorBoolean.AND);
    }

    private static boolean b(VoxelShape voxelshape, AxisAlignedBB axisalignedbb) {
        return VoxelShapes.c(voxelshape, VoxelShapes.a(axisalignedbb.shrink(1.0E-7D)), OperatorBoolean.AND);
    }

    public static boolean a(WorldBorder worldborder, AxisAlignedBB axisalignedbb) {
        double d0 = (double) MathHelper.floor(worldborder.e());
        double d1 = (double) MathHelper.floor(worldborder.f());
        double d2 = (double) MathHelper.e(worldborder.g());
        double d3 = (double) MathHelper.e(worldborder.h());

        return axisalignedbb.minX > d0 && axisalignedbb.minX < d2 && axisalignedbb.minZ > d1 && axisalignedbb.minZ < d3 && axisalignedbb.maxX > d0 && axisalignedbb.maxX < d2 && axisalignedbb.maxZ > d1 && axisalignedbb.maxZ < d3;
    }
}
