package net.minecraft.world.phys.shapes;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumAxisCycle;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public abstract class VoxelShape {

    protected final VoxelShapeDiscrete shape;
    @Nullable
    private VoxelShape[] faces;

    VoxelShape(VoxelShapeDiscrete voxelshapediscrete) {
        this.shape = voxelshapediscrete;
    }

    public double min(EnumDirection.EnumAxis enumdirection_enumaxis) {
        int i = this.shape.firstFull(enumdirection_enumaxis);

        return i >= this.shape.getSize(enumdirection_enumaxis) ? Double.POSITIVE_INFINITY : this.get(enumdirection_enumaxis, i);
    }

    public double max(EnumDirection.EnumAxis enumdirection_enumaxis) {
        int i = this.shape.lastFull(enumdirection_enumaxis);

        return i <= 0 ? Double.NEGATIVE_INFINITY : this.get(enumdirection_enumaxis, i);
    }

    public AxisAlignedBB bounds() {
        if (this.isEmpty()) {
            throw (UnsupportedOperationException) SystemUtils.pauseInIde(new UnsupportedOperationException("No bounds for empty shape."));
        } else {
            return new AxisAlignedBB(this.min(EnumDirection.EnumAxis.X), this.min(EnumDirection.EnumAxis.Y), this.min(EnumDirection.EnumAxis.Z), this.max(EnumDirection.EnumAxis.X), this.max(EnumDirection.EnumAxis.Y), this.max(EnumDirection.EnumAxis.Z));
        }
    }

    protected double get(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        return this.getCoords(enumdirection_enumaxis).getDouble(i);
    }

    protected abstract DoubleList getCoords(EnumDirection.EnumAxis enumdirection_enumaxis);

    public boolean isEmpty() {
        return this.shape.isEmpty();
    }

    public VoxelShape move(double d0, double d1, double d2) {
        return (VoxelShape) (this.isEmpty() ? VoxelShapes.empty() : new VoxelShapeArray(this.shape, new DoubleListOffset(this.getCoords(EnumDirection.EnumAxis.X), d0), new DoubleListOffset(this.getCoords(EnumDirection.EnumAxis.Y), d1), new DoubleListOffset(this.getCoords(EnumDirection.EnumAxis.Z), d2)));
    }

    public VoxelShape optimize() {
        VoxelShape[] avoxelshape = new VoxelShape[]{VoxelShapes.empty()};

        this.forAllBoxes((d0, d1, d2, d3, d4, d5) -> {
            avoxelshape[0] = VoxelShapes.joinUnoptimized(avoxelshape[0], VoxelShapes.box(d0, d1, d2, d3, d4, d5), OperatorBoolean.OR);
        });
        return avoxelshape[0];
    }

    public void forAllEdges(VoxelShapes.a voxelshapes_a) {
        this.shape.forAllEdges((i, j, k, l, i1, j1) -> {
            voxelshapes_a.consume(this.get(EnumDirection.EnumAxis.X, i), this.get(EnumDirection.EnumAxis.Y, j), this.get(EnumDirection.EnumAxis.Z, k), this.get(EnumDirection.EnumAxis.X, l), this.get(EnumDirection.EnumAxis.Y, i1), this.get(EnumDirection.EnumAxis.Z, j1));
        }, true);
    }

    public void forAllBoxes(VoxelShapes.a voxelshapes_a) {
        DoubleList doublelist = this.getCoords(EnumDirection.EnumAxis.X);
        DoubleList doublelist1 = this.getCoords(EnumDirection.EnumAxis.Y);
        DoubleList doublelist2 = this.getCoords(EnumDirection.EnumAxis.Z);

        this.shape.forAllBoxes((i, j, k, l, i1, j1) -> {
            voxelshapes_a.consume(doublelist.getDouble(i), doublelist1.getDouble(j), doublelist2.getDouble(k), doublelist.getDouble(l), doublelist1.getDouble(i1), doublelist2.getDouble(j1));
        }, true);
    }

    public List<AxisAlignedBB> toAabbs() {
        List<AxisAlignedBB> list = Lists.newArrayList();

        this.forAllBoxes((d0, d1, d2, d3, d4, d5) -> {
            list.add(new AxisAlignedBB(d0, d1, d2, d3, d4, d5));
        });
        return list;
    }

    public double min(EnumDirection.EnumAxis enumdirection_enumaxis, double d0, double d1) {
        EnumDirection.EnumAxis enumdirection_enumaxis1 = EnumAxisCycle.FORWARD.cycle(enumdirection_enumaxis);
        EnumDirection.EnumAxis enumdirection_enumaxis2 = EnumAxisCycle.BACKWARD.cycle(enumdirection_enumaxis);
        int i = this.findIndex(enumdirection_enumaxis1, d0);
        int j = this.findIndex(enumdirection_enumaxis2, d1);
        int k = this.shape.firstFull(enumdirection_enumaxis, i, j);

        return k >= this.shape.getSize(enumdirection_enumaxis) ? Double.POSITIVE_INFINITY : this.get(enumdirection_enumaxis, k);
    }

    public double max(EnumDirection.EnumAxis enumdirection_enumaxis, double d0, double d1) {
        EnumDirection.EnumAxis enumdirection_enumaxis1 = EnumAxisCycle.FORWARD.cycle(enumdirection_enumaxis);
        EnumDirection.EnumAxis enumdirection_enumaxis2 = EnumAxisCycle.BACKWARD.cycle(enumdirection_enumaxis);
        int i = this.findIndex(enumdirection_enumaxis1, d0);
        int j = this.findIndex(enumdirection_enumaxis2, d1);
        int k = this.shape.lastFull(enumdirection_enumaxis, i, j);

        return k <= 0 ? Double.NEGATIVE_INFINITY : this.get(enumdirection_enumaxis, k);
    }

    protected int findIndex(EnumDirection.EnumAxis enumdirection_enumaxis, double d0) {
        return MathHelper.binarySearch(0, this.shape.getSize(enumdirection_enumaxis) + 1, (i) -> {
            return d0 < this.get(enumdirection_enumaxis, i);
        }) - 1;
    }

    @Nullable
    public MovingObjectPositionBlock clip(Vec3D vec3d, Vec3D vec3d1, BlockPosition blockposition) {
        if (this.isEmpty()) {
            return null;
        } else {
            Vec3D vec3d2 = vec3d1.subtract(vec3d);

            if (vec3d2.lengthSqr() < 1.0E-7D) {
                return null;
            } else {
                Vec3D vec3d3 = vec3d.add(vec3d2.scale(0.001D));

                return this.shape.isFullWide(this.findIndex(EnumDirection.EnumAxis.X, vec3d3.x - (double) blockposition.getX()), this.findIndex(EnumDirection.EnumAxis.Y, vec3d3.y - (double) blockposition.getY()), this.findIndex(EnumDirection.EnumAxis.Z, vec3d3.z - (double) blockposition.getZ())) ? new MovingObjectPositionBlock(vec3d3, EnumDirection.getNearest(vec3d2.x, vec3d2.y, vec3d2.z).getOpposite(), blockposition, true) : AxisAlignedBB.clip(this.toAabbs(), vec3d, vec3d1, blockposition);
            }
        }
    }

    public Optional<Vec3D> closestPointTo(Vec3D vec3d) {
        if (this.isEmpty()) {
            return Optional.empty();
        } else {
            Vec3D[] avec3d = new Vec3D[1];

            this.forAllBoxes((d0, d1, d2, d3, d4, d5) -> {
                double d6 = MathHelper.clamp(vec3d.x(), d0, d3);
                double d7 = MathHelper.clamp(vec3d.y(), d1, d4);
                double d8 = MathHelper.clamp(vec3d.z(), d2, d5);

                if (avec3d[0] == null || vec3d.distanceToSqr(d6, d7, d8) < vec3d.distanceToSqr(avec3d[0])) {
                    avec3d[0] = new Vec3D(d6, d7, d8);
                }

            });
            return Optional.of(avec3d[0]);
        }
    }

    public VoxelShape getFaceShape(EnumDirection enumdirection) {
        if (!this.isEmpty() && this != VoxelShapes.block()) {
            VoxelShape voxelshape;

            if (this.faces != null) {
                voxelshape = this.faces[enumdirection.ordinal()];
                if (voxelshape != null) {
                    return voxelshape;
                }
            } else {
                this.faces = new VoxelShape[6];
            }

            voxelshape = this.calculateFace(enumdirection);
            this.faces[enumdirection.ordinal()] = voxelshape;
            return voxelshape;
        } else {
            return this;
        }
    }

    private VoxelShape calculateFace(EnumDirection enumdirection) {
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis();
        DoubleList doublelist = this.getCoords(enumdirection_enumaxis);

        if (doublelist.size() == 2 && DoubleMath.fuzzyEquals(doublelist.getDouble(0), 0.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(doublelist.getDouble(1), 1.0D, 1.0E-7D)) {
            return this;
        } else {
            EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection = enumdirection.getAxisDirection();
            int i = this.findIndex(enumdirection_enumaxis, enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? 0.9999999D : 1.0E-7D);

            return new VoxelShapeSlice(this, enumdirection_enumaxis, i);
        }
    }

    public double collide(EnumDirection.EnumAxis enumdirection_enumaxis, AxisAlignedBB axisalignedbb, double d0) {
        return this.collideX(EnumAxisCycle.between(enumdirection_enumaxis, EnumDirection.EnumAxis.X), axisalignedbb, d0);
    }

    protected double collideX(EnumAxisCycle enumaxiscycle, AxisAlignedBB axisalignedbb, double d0) {
        if (this.isEmpty()) {
            return d0;
        } else if (Math.abs(d0) < 1.0E-7D) {
            return 0.0D;
        } else {
            EnumAxisCycle enumaxiscycle1 = enumaxiscycle.inverse();
            EnumDirection.EnumAxis enumdirection_enumaxis = enumaxiscycle1.cycle(EnumDirection.EnumAxis.X);
            EnumDirection.EnumAxis enumdirection_enumaxis1 = enumaxiscycle1.cycle(EnumDirection.EnumAxis.Y);
            EnumDirection.EnumAxis enumdirection_enumaxis2 = enumaxiscycle1.cycle(EnumDirection.EnumAxis.Z);
            double d1 = axisalignedbb.max(enumdirection_enumaxis);
            double d2 = axisalignedbb.min(enumdirection_enumaxis);
            int i = this.findIndex(enumdirection_enumaxis, d2 + 1.0E-7D);
            int j = this.findIndex(enumdirection_enumaxis, d1 - 1.0E-7D);
            int k = Math.max(0, this.findIndex(enumdirection_enumaxis1, axisalignedbb.min(enumdirection_enumaxis1) + 1.0E-7D));
            int l = Math.min(this.shape.getSize(enumdirection_enumaxis1), this.findIndex(enumdirection_enumaxis1, axisalignedbb.max(enumdirection_enumaxis1) - 1.0E-7D) + 1);
            int i1 = Math.max(0, this.findIndex(enumdirection_enumaxis2, axisalignedbb.min(enumdirection_enumaxis2) + 1.0E-7D));
            int j1 = Math.min(this.shape.getSize(enumdirection_enumaxis2), this.findIndex(enumdirection_enumaxis2, axisalignedbb.max(enumdirection_enumaxis2) - 1.0E-7D) + 1);
            int k1 = this.shape.getSize(enumdirection_enumaxis);
            double d3;
            int l1;
            int i2;
            int j2;

            if (d0 > 0.0D) {
                for (l1 = j + 1; l1 < k1; ++l1) {
                    for (i2 = k; i2 < l; ++i2) {
                        for (j2 = i1; j2 < j1; ++j2) {
                            if (this.shape.isFullWide(enumaxiscycle1, l1, i2, j2)) {
                                d3 = this.get(enumdirection_enumaxis, l1) - d1;
                                if (d3 >= -1.0E-7D) {
                                    d0 = Math.min(d0, d3);
                                }

                                return d0;
                            }
                        }
                    }
                }
            } else if (d0 < 0.0D) {
                for (l1 = i - 1; l1 >= 0; --l1) {
                    for (i2 = k; i2 < l; ++i2) {
                        for (j2 = i1; j2 < j1; ++j2) {
                            if (this.shape.isFullWide(enumaxiscycle1, l1, i2, j2)) {
                                d3 = this.get(enumdirection_enumaxis, l1 + 1) - d2;
                                if (d3 <= 1.0E-7D) {
                                    d0 = Math.max(d0, d3);
                                }

                                return d0;
                            }
                        }
                    }
                }
            }

            return d0;
        }
    }

    public String toString() {
        return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.bounds() + "]";
    }
}
