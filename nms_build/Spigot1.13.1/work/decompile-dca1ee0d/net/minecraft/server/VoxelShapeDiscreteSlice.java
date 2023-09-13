package net.minecraft.server;

final class VoxelShapeDiscreteSlice extends VoxelShapeDiscrete {

    private final VoxelShapeDiscrete d;
    private final int e;
    private final int f;
    private final int g;
    private final int h;
    private final int i;
    private final int j;

    public VoxelShapeDiscreteSlice(VoxelShapeDiscrete voxelshapediscrete, int i, int j, int k, int l, int i1, int j1) {
        super(l - i, i1 - j, j1 - k);
        this.d = voxelshapediscrete;
        this.e = i;
        this.f = j;
        this.g = k;
        this.h = l;
        this.i = i1;
        this.j = j1;
    }

    public boolean b(int i, int j, int k) {
        return this.d.b(this.e + i, this.f + j, this.g + k);
    }

    public void a(int i, int j, int k, boolean flag, boolean flag1) {
        this.d.a(this.e + i, this.f + j, this.g + k, flag, flag1);
    }

    public int a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return Math.max(0, this.d.a(enumdirection_enumaxis) - enumdirection_enumaxis.a(this.e, this.f, this.g));
    }

    public int b(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return Math.min(enumdirection_enumaxis.a(this.h, this.i, this.j), this.d.b(enumdirection_enumaxis) - enumdirection_enumaxis.a(this.e, this.f, this.g));
    }
}
