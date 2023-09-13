package net.minecraft.world.phys.shapes;

import net.minecraft.core.EnumAxisCycle;
import net.minecraft.core.EnumDirection;

public abstract class VoxelShapeDiscrete {

    private static final EnumDirection.EnumAxis[] AXIS_VALUES = EnumDirection.EnumAxis.values();
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;

    protected VoxelShapeDiscrete(int i, int j, int k) {
        if (i >= 0 && j >= 0 && k >= 0) {
            this.xSize = i;
            this.ySize = j;
            this.zSize = k;
        } else {
            throw new IllegalArgumentException("Need all positive sizes: x: " + i + ", y: " + j + ", z: " + k);
        }
    }

    public boolean a(EnumAxisCycle enumaxiscycle, int i, int j, int k) {
        return this.d(enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.X), enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.Y), enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.Z));
    }

    public boolean d(int i, int j, int k) {
        return i >= 0 && j >= 0 && k >= 0 ? (i < this.xSize && j < this.ySize && k < this.zSize ? this.b(i, j, k) : false) : false;
    }

    public boolean b(EnumAxisCycle enumaxiscycle, int i, int j, int k) {
        return this.b(enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.X), enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.Y), enumaxiscycle.a(i, j, k, EnumDirection.EnumAxis.Z));
    }

    public abstract boolean b(int i, int j, int k);

    public abstract void c(int i, int j, int k);

    public boolean a() {
        EnumDirection.EnumAxis[] aenumdirection_enumaxis = VoxelShapeDiscrete.AXIS_VALUES;
        int i = aenumdirection_enumaxis.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection.EnumAxis enumdirection_enumaxis = aenumdirection_enumaxis[j];

            if (this.a(enumdirection_enumaxis) >= this.b(enumdirection_enumaxis)) {
                return true;
            }
        }

        return false;
    }

    public abstract int a(EnumDirection.EnumAxis enumdirection_enumaxis);

    public abstract int b(EnumDirection.EnumAxis enumdirection_enumaxis);

    public int a(EnumDirection.EnumAxis enumdirection_enumaxis, int i, int j) {
        int k = this.c(enumdirection_enumaxis);

        if (i >= 0 && j >= 0) {
            EnumDirection.EnumAxis enumdirection_enumaxis1 = EnumAxisCycle.FORWARD.a(enumdirection_enumaxis);
            EnumDirection.EnumAxis enumdirection_enumaxis2 = EnumAxisCycle.BACKWARD.a(enumdirection_enumaxis);

            if (i < this.c(enumdirection_enumaxis1) && j < this.c(enumdirection_enumaxis2)) {
                EnumAxisCycle enumaxiscycle = EnumAxisCycle.a(EnumDirection.EnumAxis.X, enumdirection_enumaxis);

                for (int l = 0; l < k; ++l) {
                    if (this.b(enumaxiscycle, l, i, j)) {
                        return l;
                    }
                }

                return k;
            } else {
                return k;
            }
        } else {
            return k;
        }
    }

    public int b(EnumDirection.EnumAxis enumdirection_enumaxis, int i, int j) {
        if (i >= 0 && j >= 0) {
            EnumDirection.EnumAxis enumdirection_enumaxis1 = EnumAxisCycle.FORWARD.a(enumdirection_enumaxis);
            EnumDirection.EnumAxis enumdirection_enumaxis2 = EnumAxisCycle.BACKWARD.a(enumdirection_enumaxis);

            if (i < this.c(enumdirection_enumaxis1) && j < this.c(enumdirection_enumaxis2)) {
                int k = this.c(enumdirection_enumaxis);
                EnumAxisCycle enumaxiscycle = EnumAxisCycle.a(EnumDirection.EnumAxis.X, enumdirection_enumaxis);

                for (int l = k - 1; l >= 0; --l) {
                    if (this.b(enumaxiscycle, l, i, j)) {
                        return l + 1;
                    }
                }

                return 0;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public int c(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.a(this.xSize, this.ySize, this.zSize);
    }

    public int b() {
        return this.c(EnumDirection.EnumAxis.X);
    }

    public int c() {
        return this.c(EnumDirection.EnumAxis.Y);
    }

    public int d() {
        return this.c(EnumDirection.EnumAxis.Z);
    }

    public void a(VoxelShapeDiscrete.b voxelshapediscrete_b, boolean flag) {
        this.a(voxelshapediscrete_b, EnumAxisCycle.NONE, flag);
        this.a(voxelshapediscrete_b, EnumAxisCycle.FORWARD, flag);
        this.a(voxelshapediscrete_b, EnumAxisCycle.BACKWARD, flag);
    }

    private void a(VoxelShapeDiscrete.b voxelshapediscrete_b, EnumAxisCycle enumaxiscycle, boolean flag) {
        EnumAxisCycle enumaxiscycle1 = enumaxiscycle.a();
        int i = this.c(enumaxiscycle1.a(EnumDirection.EnumAxis.X));
        int j = this.c(enumaxiscycle1.a(EnumDirection.EnumAxis.Y));
        int k = this.c(enumaxiscycle1.a(EnumDirection.EnumAxis.Z));

        for (int l = 0; l <= i; ++l) {
            for (int i1 = 0; i1 <= j; ++i1) {
                int j1 = -1;

                for (int k1 = 0; k1 <= k; ++k1) {
                    int l1 = 0;
                    int i2 = 0;

                    for (int j2 = 0; j2 <= 1; ++j2) {
                        for (int k2 = 0; k2 <= 1; ++k2) {
                            if (this.a(enumaxiscycle1, l + j2 - 1, i1 + k2 - 1, k1)) {
                                ++l1;
                                i2 ^= j2 ^ k2;
                            }
                        }
                    }

                    if (l1 != 1 && l1 != 3 && (l1 != 2 || (i2 & 1) != 0)) {
                        if (j1 != -1) {
                            voxelshapediscrete_b.consume(enumaxiscycle1.a(l, i1, j1, EnumDirection.EnumAxis.X), enumaxiscycle1.a(l, i1, j1, EnumDirection.EnumAxis.Y), enumaxiscycle1.a(l, i1, j1, EnumDirection.EnumAxis.Z), enumaxiscycle1.a(l, i1, k1, EnumDirection.EnumAxis.X), enumaxiscycle1.a(l, i1, k1, EnumDirection.EnumAxis.Y), enumaxiscycle1.a(l, i1, k1, EnumDirection.EnumAxis.Z));
                            j1 = -1;
                        }
                    } else if (flag) {
                        if (j1 == -1) {
                            j1 = k1;
                        }
                    } else {
                        voxelshapediscrete_b.consume(enumaxiscycle1.a(l, i1, k1, EnumDirection.EnumAxis.X), enumaxiscycle1.a(l, i1, k1, EnumDirection.EnumAxis.Y), enumaxiscycle1.a(l, i1, k1, EnumDirection.EnumAxis.Z), enumaxiscycle1.a(l, i1, k1 + 1, EnumDirection.EnumAxis.X), enumaxiscycle1.a(l, i1, k1 + 1, EnumDirection.EnumAxis.Y), enumaxiscycle1.a(l, i1, k1 + 1, EnumDirection.EnumAxis.Z));
                    }
                }
            }
        }

    }

    public void b(VoxelShapeDiscrete.b voxelshapediscrete_b, boolean flag) {
        VoxelShapeBitSet.a(this, voxelshapediscrete_b, flag);
    }

    public void a(VoxelShapeDiscrete.a voxelshapediscrete_a) {
        this.a(voxelshapediscrete_a, EnumAxisCycle.NONE);
        this.a(voxelshapediscrete_a, EnumAxisCycle.FORWARD);
        this.a(voxelshapediscrete_a, EnumAxisCycle.BACKWARD);
    }

    private void a(VoxelShapeDiscrete.a voxelshapediscrete_a, EnumAxisCycle enumaxiscycle) {
        EnumAxisCycle enumaxiscycle1 = enumaxiscycle.a();
        EnumDirection.EnumAxis enumdirection_enumaxis = enumaxiscycle1.a(EnumDirection.EnumAxis.Z);
        int i = this.c(enumaxiscycle1.a(EnumDirection.EnumAxis.X));
        int j = this.c(enumaxiscycle1.a(EnumDirection.EnumAxis.Y));
        int k = this.c(enumdirection_enumaxis);
        EnumDirection enumdirection = EnumDirection.a(enumdirection_enumaxis, EnumDirection.EnumAxisDirection.NEGATIVE);
        EnumDirection enumdirection1 = EnumDirection.a(enumdirection_enumaxis, EnumDirection.EnumAxisDirection.POSITIVE);

        for (int l = 0; l < i; ++l) {
            for (int i1 = 0; i1 < j; ++i1) {
                boolean flag = false;

                for (int j1 = 0; j1 <= k; ++j1) {
                    boolean flag1 = j1 != k && this.b(enumaxiscycle1, l, i1, j1);

                    if (!flag && flag1) {
                        voxelshapediscrete_a.consume(enumdirection, enumaxiscycle1.a(l, i1, j1, EnumDirection.EnumAxis.X), enumaxiscycle1.a(l, i1, j1, EnumDirection.EnumAxis.Y), enumaxiscycle1.a(l, i1, j1, EnumDirection.EnumAxis.Z));
                    }

                    if (flag && !flag1) {
                        voxelshapediscrete_a.consume(enumdirection1, enumaxiscycle1.a(l, i1, j1 - 1, EnumDirection.EnumAxis.X), enumaxiscycle1.a(l, i1, j1 - 1, EnumDirection.EnumAxis.Y), enumaxiscycle1.a(l, i1, j1 - 1, EnumDirection.EnumAxis.Z));
                    }

                    flag = flag1;
                }
            }
        }

    }

    public interface b {

        void consume(int i, int j, int k, int l, int i1, int j1);
    }

    public interface a {

        void consume(EnumDirection enumdirection, int i, int j, int k);
    }
}
