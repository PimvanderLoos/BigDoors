package net.minecraft.server;

final class VoxelShapeDiscreteSlice extends VoxelShapeDiscrete {

    private final VoxelShapeDiscrete e;
    private final int f;
    private final int g;
    private final int h;
    private final int i;
    private final int j;
    private final int k;

    public VoxelShapeDiscreteSlice(VoxelShapeDiscrete voxelshapediscrete, int i, int j, int k, int l, int i1, int j1) {
        super(l - i, i1 - j, j1 - k);
        this.e = voxelshapediscrete;
        this.f = i;
        this.g = j;
        this.h = k;
        this.i = l;
        this.j = i1;
        this.k = j1;
    }

    public boolean b(int i, int j, int k) {
        return this.e.b(this.f + i, this.g + j, this.h + k);
    }

    public boolean a(int i, int j, int k, boolean flag, boolean flag1) {
        return this.e.a(this.f + i, this.g + j, this.h + k, flag, flag1);
    }

    public int a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return Math.max(0, this.e.a(enumdirection_enumaxis) - enumdirection_enumaxis.a(this.f, this.g, this.h));
    }

    public int b(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return Math.min(enumdirection_enumaxis.a(this.i, this.j, this.k), this.e.b(enumdirection_enumaxis) - enumdirection_enumaxis.a(this.f, this.g, this.h));
    }
}
