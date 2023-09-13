package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface IBlockAccess extends LevelHeightAccessor {

    @Nullable
    TileEntity getBlockEntity(BlockPosition blockposition);

    default <T extends TileEntity> Optional<T> getBlockEntity(BlockPosition blockposition, TileEntityTypes<T> tileentitytypes) {
        TileEntity tileentity = this.getBlockEntity(blockposition);

        return tileentity != null && tileentity.getType() == tileentitytypes ? Optional.of(tileentity) : Optional.empty();
    }

    IBlockData getBlockState(BlockPosition blockposition);

    Fluid getFluidState(BlockPosition blockposition);

    default int getLightEmission(BlockPosition blockposition) {
        return this.getBlockState(blockposition).getLightEmission();
    }

    default int getMaxLightLevel() {
        return 15;
    }

    default Stream<IBlockData> getBlockStates(AxisAlignedBB axisalignedbb) {
        return BlockPosition.betweenClosedStream(axisalignedbb).map(this::getBlockState);
    }

    default MovingObjectPositionBlock isBlockInLine(ClipBlockStateContext clipblockstatecontext) {
        return (MovingObjectPositionBlock) traverseBlocks(clipblockstatecontext.getFrom(), clipblockstatecontext.getTo(), clipblockstatecontext, (clipblockstatecontext1, blockposition) -> {
            IBlockData iblockdata = this.getBlockState(blockposition);
            Vec3D vec3d = clipblockstatecontext1.getFrom().subtract(clipblockstatecontext1.getTo());

            return clipblockstatecontext1.isTargetBlock().test(iblockdata) ? new MovingObjectPositionBlock(clipblockstatecontext1.getTo(), EnumDirection.getNearest(vec3d.x, vec3d.y, vec3d.z), BlockPosition.containing(clipblockstatecontext1.getTo()), false) : null;
        }, (clipblockstatecontext1) -> {
            Vec3D vec3d = clipblockstatecontext1.getFrom().subtract(clipblockstatecontext1.getTo());

            return MovingObjectPositionBlock.miss(clipblockstatecontext1.getTo(), EnumDirection.getNearest(vec3d.x, vec3d.y, vec3d.z), BlockPosition.containing(clipblockstatecontext1.getTo()));
        });
    }

    default MovingObjectPositionBlock clip(RayTrace raytrace) {
        return (MovingObjectPositionBlock) traverseBlocks(raytrace.getFrom(), raytrace.getTo(), raytrace, (raytrace1, blockposition) -> {
            IBlockData iblockdata = this.getBlockState(blockposition);
            Fluid fluid = this.getFluidState(blockposition);
            Vec3D vec3d = raytrace1.getFrom();
            Vec3D vec3d1 = raytrace1.getTo();
            VoxelShape voxelshape = raytrace1.getBlockShape(iblockdata, this, blockposition);
            MovingObjectPositionBlock movingobjectpositionblock = this.clipWithInteractionOverride(vec3d, vec3d1, blockposition, voxelshape, iblockdata);
            VoxelShape voxelshape1 = raytrace1.getFluidShape(fluid, this, blockposition);
            MovingObjectPositionBlock movingobjectpositionblock1 = voxelshape1.clip(vec3d, vec3d1, blockposition);
            double d0 = movingobjectpositionblock == null ? Double.MAX_VALUE : raytrace1.getFrom().distanceToSqr(movingobjectpositionblock.getLocation());
            double d1 = movingobjectpositionblock1 == null ? Double.MAX_VALUE : raytrace1.getFrom().distanceToSqr(movingobjectpositionblock1.getLocation());

            return d0 <= d1 ? movingobjectpositionblock : movingobjectpositionblock1;
        }, (raytrace1) -> {
            Vec3D vec3d = raytrace1.getFrom().subtract(raytrace1.getTo());

            return MovingObjectPositionBlock.miss(raytrace1.getTo(), EnumDirection.getNearest(vec3d.x, vec3d.y, vec3d.z), BlockPosition.containing(raytrace1.getTo()));
        });
    }

    @Nullable
    default MovingObjectPositionBlock clipWithInteractionOverride(Vec3D vec3d, Vec3D vec3d1, BlockPosition blockposition, VoxelShape voxelshape, IBlockData iblockdata) {
        MovingObjectPositionBlock movingobjectpositionblock = voxelshape.clip(vec3d, vec3d1, blockposition);

        if (movingobjectpositionblock != null) {
            MovingObjectPositionBlock movingobjectpositionblock1 = iblockdata.getInteractionShape(this, blockposition).clip(vec3d, vec3d1, blockposition);

            if (movingobjectpositionblock1 != null && movingobjectpositionblock1.getLocation().subtract(vec3d).lengthSqr() < movingobjectpositionblock.getLocation().subtract(vec3d).lengthSqr()) {
                return movingobjectpositionblock.withDirection(movingobjectpositionblock1.getDirection());
            }
        }

        return movingobjectpositionblock;
    }

    default double getBlockFloorHeight(VoxelShape voxelshape, Supplier<VoxelShape> supplier) {
        if (!voxelshape.isEmpty()) {
            return voxelshape.max(EnumDirection.EnumAxis.Y);
        } else {
            double d0 = ((VoxelShape) supplier.get()).max(EnumDirection.EnumAxis.Y);

            return d0 >= 1.0D ? d0 - 1.0D : Double.NEGATIVE_INFINITY;
        }
    }

    default double getBlockFloorHeight(BlockPosition blockposition) {
        return this.getBlockFloorHeight(this.getBlockState(blockposition).getCollisionShape(this, blockposition), () -> {
            BlockPosition blockposition1 = blockposition.below();

            return this.getBlockState(blockposition1).getCollisionShape(this, blockposition1);
        });
    }

    static <T, C> T traverseBlocks(Vec3D vec3d, Vec3D vec3d1, C c0, BiFunction<C, BlockPosition, T> bifunction, Function<C, T> function) {
        if (vec3d.equals(vec3d1)) {
            return function.apply(c0);
        } else {
            double d0 = MathHelper.lerp(-1.0E-7D, vec3d1.x, vec3d.x);
            double d1 = MathHelper.lerp(-1.0E-7D, vec3d1.y, vec3d.y);
            double d2 = MathHelper.lerp(-1.0E-7D, vec3d1.z, vec3d.z);
            double d3 = MathHelper.lerp(-1.0E-7D, vec3d.x, vec3d1.x);
            double d4 = MathHelper.lerp(-1.0E-7D, vec3d.y, vec3d1.y);
            double d5 = MathHelper.lerp(-1.0E-7D, vec3d.z, vec3d1.z);
            int i = MathHelper.floor(d3);
            int j = MathHelper.floor(d4);
            int k = MathHelper.floor(d5);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, j, k);
            T t0 = bifunction.apply(c0, blockposition_mutableblockposition);

            if (t0 != null) {
                return t0;
            } else {
                double d6 = d0 - d3;
                double d7 = d1 - d4;
                double d8 = d2 - d5;
                int l = MathHelper.sign(d6);
                int i1 = MathHelper.sign(d7);
                int j1 = MathHelper.sign(d8);
                double d9 = l == 0 ? Double.MAX_VALUE : (double) l / d6;
                double d10 = i1 == 0 ? Double.MAX_VALUE : (double) i1 / d7;
                double d11 = j1 == 0 ? Double.MAX_VALUE : (double) j1 / d8;
                double d12 = d9 * (l > 0 ? 1.0D - MathHelper.frac(d3) : MathHelper.frac(d3));
                double d13 = d10 * (i1 > 0 ? 1.0D - MathHelper.frac(d4) : MathHelper.frac(d4));
                double d14 = d11 * (j1 > 0 ? 1.0D - MathHelper.frac(d5) : MathHelper.frac(d5));

                Object object;

                do {
                    if (d12 > 1.0D && d13 > 1.0D && d14 > 1.0D) {
                        return function.apply(c0);
                    }

                    if (d12 < d13) {
                        if (d12 < d14) {
                            i += l;
                            d12 += d9;
                        } else {
                            k += j1;
                            d14 += d11;
                        }
                    } else if (d13 < d14) {
                        j += i1;
                        d13 += d10;
                    } else {
                        k += j1;
                        d14 += d11;
                    }

                    object = bifunction.apply(c0, blockposition_mutableblockposition.set(i, j, k));
                } while (object == null);

                return object;
            }
        }
    }
}
