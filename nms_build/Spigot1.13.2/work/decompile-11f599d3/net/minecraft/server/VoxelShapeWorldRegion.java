package net.minecraft.server;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class VoxelShapeWorldRegion extends VoxelShape {

    private final int b;
    private final int c;
    private final int d;

    public VoxelShapeWorldRegion(VoxelShapeDiscrete voxelshapediscrete, int i, int j, int k) {
        super(voxelshapediscrete);
        this.b = i;
        this.c = j;
        this.d = k;
    }

    protected DoubleList a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return new DoubleListRange(this.a.c(enumdirection_enumaxis), enumdirection_enumaxis.a(this.b, this.c, this.d));
    }
}
