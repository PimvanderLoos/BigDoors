package net.minecraft.server;

import java.util.BitSet;

public final class VoxelShapeBitSet extends VoxelShapeDiscrete {

    private final BitSet e;
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;

    public VoxelShapeBitSet(int i, int j, int k) {
        this(i, j, k, i, j, k, 0, 0, 0);
    }

    public VoxelShapeBitSet(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
        super(i, j, k);
        this.e = new BitSet(i * j * k);
        this.f = l;
        this.g = i1;
        this.h = j1;
        this.i = k1;
        this.j = l1;
        this.k = i2;
    }

    public VoxelShapeBitSet(VoxelShapeDiscrete voxelshapediscrete) {
        super(voxelshapediscrete.b, voxelshapediscrete.c, voxelshapediscrete.d);
        if (voxelshapediscrete instanceof VoxelShapeBitSet) {
            this.e = (BitSet) ((VoxelShapeBitSet) voxelshapediscrete).e.clone();
        } else {
            this.e = new BitSet(this.b * this.c * this.d);

            for (int i = 0; i < this.b; ++i) {
                for (int j = 0; j < this.c; ++j) {
                    for (int k = 0; k < this.d; ++k) {
                        if (voxelshapediscrete.b(i, j, k)) {
                            this.e.set(this.a(i, j, k));
                        }
                    }
                }
            }
        }

        this.f = voxelshapediscrete.a(EnumDirection.EnumAxis.X);
        this.g = voxelshapediscrete.a(EnumDirection.EnumAxis.Y);
        this.h = voxelshapediscrete.a(EnumDirection.EnumAxis.Z);
        this.i = voxelshapediscrete.b(EnumDirection.EnumAxis.X);
        this.j = voxelshapediscrete.b(EnumDirection.EnumAxis.Y);
        this.k = voxelshapediscrete.b(EnumDirection.EnumAxis.Z);
    }

    protected int a(int i, int j, int k) {
        return (i * this.c + j) * this.d + k;
    }

    public boolean b(int i, int j, int k) {
        return this.e.get(this.a(i, j, k));
    }

    public boolean a(int i, int j, int k, boolean flag, boolean flag1) {
        int l = this.a(i, j, k);
        boolean flag2 = this.e.get(l);

        this.e.set(l, flag1);
        if (flag && flag1) {
            this.f = Math.min(this.f, i);
            this.g = Math.min(this.g, j);
            this.h = Math.min(this.h, k);
            this.i = Math.max(this.i, i + 1);
            this.j = Math.max(this.j, j + 1);
            this.k = Math.max(this.k, k + 1);
        }

        return flag2;
    }

    public boolean a() {
        return this.e.isEmpty();
    }

    public int a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.a(this.f, this.g, this.h);
    }

    public int b(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.a(this.i, this.j, this.k);
    }

    protected boolean a(int i, int j, int k, int l) {
        return k >= 0 && l >= 0 && i >= 0 ? (k < this.b && l < this.c && j <= this.d ? this.e.nextClearBit(this.a(k, l, i)) >= this.a(k, l, j) : false) : false;
    }

    protected void a(int i, int j, int k, int l, boolean flag) {
        this.e.set(this.a(k, l, i), this.a(k, l, j), flag);
    }

    static VoxelShapeBitSet a(VoxelShapeDiscrete voxelshapediscrete, VoxelShapeDiscrete voxelshapediscrete1, VoxelShapeMerger voxelshapemerger, VoxelShapeMerger voxelshapemerger1, VoxelShapeMerger voxelshapemerger2, OperatorBoolean operatorboolean) {
        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(voxelshapemerger.size() - 1, voxelshapemerger1.size() - 1, voxelshapemerger2.size() - 1);
        int[] aint = new int[] { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};

        voxelshapemerger.a((i, j, k) -> {
            boolean[] aboolean = new boolean[] { false};
            boolean flag = voxelshapemerger.a((ix, jx, kx) -> {
                boolean[] aboolean = new boolean[] { false};
                boolean flag = voxelshapemerger.a((ixx, jxx, kxx) -> {
                    boolean flag = operatorboolean.apply(voxelshapediscrete.c(i, ix, ixx), voxelshapediscrete1.c(j, jx, jxx));

                    if (flag) {
                        voxelshapebitset.e.set(voxelshapebitset.a(k, kx, kxx));
                        aint[2] = Math.min(aint[2], kxx);
                        aint[5] = Math.max(aint[5], kxx);
                        aboolean[0] = true;
                    }

                    return true;
                });

                if (aboolean[0]) {
                    aint[1] = Math.min(aint[1], kx);
                    aint[4] = Math.max(aint[4], kx);
                    aboolean1[0] = true;
                }

                return flag;
            });

            if (aboolean[0]) {
                aint[0] = Math.min(aint[0], k);
                aint[3] = Math.max(aint[3], k);
            }

            return flag;
        });
        voxelshapebitset.f = aint[0];
        voxelshapebitset.g = aint[1];
        voxelshapebitset.h = aint[2];
        voxelshapebitset.i = aint[3] + 1;
        voxelshapebitset.j = aint[4] + 1;
        voxelshapebitset.k = aint[5] + 1;
        return voxelshapebitset;
    }
}
