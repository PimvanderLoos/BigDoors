package net.minecraft.server;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public abstract class VoxelShape {

    protected final VoxelShapeDiscrete a;

    VoxelShape(VoxelShapeDiscrete voxelshapediscrete) {
        this.a = voxelshapediscrete;
    }

    public double b(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return this.a(enumdirection_enumaxis, this.a.a(enumdirection_enumaxis));
    }

    public double c(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return this.a(enumdirection_enumaxis, this.a.b(enumdirection_enumaxis));
    }

    public AxisAlignedBB a() {
        if (this.b()) {
            throw new UnsupportedOperationException("No bounds for empty shape.");
        } else {
            return new AxisAlignedBB(this.b(EnumDirection.EnumAxis.X), this.b(EnumDirection.EnumAxis.Y), this.b(EnumDirection.EnumAxis.Z), this.c(EnumDirection.EnumAxis.X), this.c(EnumDirection.EnumAxis.Y), this.c(EnumDirection.EnumAxis.Z));
        }
    }

    protected double a(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        return i < 0 ? -1.7976931348623157E308D : (i > this.a.c(enumdirection_enumaxis) ? Double.MAX_VALUE : this.b(enumdirection_enumaxis, i));
    }

    protected double b(EnumDirection.EnumAxis enumdirection_enumaxis, int i) {
        return this.a(enumdirection_enumaxis).getDouble(i);
    }

    protected abstract DoubleList a(EnumDirection.EnumAxis enumdirection_enumaxis);

    public boolean b() {
        return this.a.a();
    }

    public VoxelShape a(double d0, double d1, double d2) {
        return (VoxelShape) (this.b() ? VoxelShapes.a() : new VoxelShapeArray(this.a, new DoubleListOffset(this.a(EnumDirection.EnumAxis.X), d0), new DoubleListOffset(this.a(EnumDirection.EnumAxis.Y), d1), new DoubleListOffset(this.a(EnumDirection.EnumAxis.Z), d2)));
    }

    public VoxelShape c() {
        VoxelShape[] avoxelshape = new VoxelShape[] { VoxelShapes.a()};

        this.b((d0, d1, d2, d3, d4, d5) -> {
            avoxelshape[0] = VoxelShapes.b(avoxelshape[0], VoxelShapes.a(d0, d1, d2, d3, d4, d5), OperatorBoolean.OR);
        });
        return avoxelshape[0];
    }

    public void b(VoxelShapes.a voxelshapes_a) {
        this.a.b((i, j, k, l, i1, j1) -> {
            voxelshapes_a.consume(this.b(EnumDirection.EnumAxis.X, i), this.b(EnumDirection.EnumAxis.Y, j), this.b(EnumDirection.EnumAxis.Z, k), this.b(EnumDirection.EnumAxis.X, l), this.b(EnumDirection.EnumAxis.Y, i1), this.b(EnumDirection.EnumAxis.Z, j1));
        }, true);
    }

    public List<AxisAlignedBB> d() {
        ArrayList arraylist = Lists.newArrayList();

        this.b((d0, d1, d2, d3, d4, d5) -> {
            list.add(new AxisAlignedBB(d0, d1, d2, d3, d4, d5));
        });
        return arraylist;
    }

    public int a(EnumDirection.EnumAxis enumdirection_enumaxis, double d0) {
        return MathHelper.a(0, this.a.c(enumdirection_enumaxis) + 1, (i) -> {
            return d0 < this.a(enumdirection_enumaxis, i);
        }) - 1;
    }

    public boolean b(double d0, double d1, double d2) {
        return this.a.c(this.a(EnumDirection.EnumAxis.X, d0), this.a(EnumDirection.EnumAxis.Y, d1), this.a(EnumDirection.EnumAxis.Z, d2));
    }

    public String toString() {
        return this.b() ? "EMPTY" : "VoxelShape[" + this.a() + "]";
    }
}
