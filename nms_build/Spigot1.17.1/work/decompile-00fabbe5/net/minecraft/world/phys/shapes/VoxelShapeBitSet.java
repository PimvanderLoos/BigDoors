package net.minecraft.world.phys.shapes;

import java.util.BitSet;
import net.minecraft.core.EnumDirection;

public final class VoxelShapeBitSet extends VoxelShapeDiscrete {

    private final BitSet storage;
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;

    public VoxelShapeBitSet(int i, int j, int k) {
        super(i, j, k);
        this.storage = new BitSet(i * j * k);
        this.xMin = i;
        this.yMin = j;
        this.zMin = k;
    }

    public static VoxelShapeBitSet a(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(i, j, k);

        voxelshapebitset.xMin = l;
        voxelshapebitset.yMin = i1;
        voxelshapebitset.zMin = j1;
        voxelshapebitset.xMax = k1;
        voxelshapebitset.yMax = l1;
        voxelshapebitset.zMax = i2;

        for (int j2 = l; j2 < k1; ++j2) {
            for (int k2 = i1; k2 < l1; ++k2) {
                for (int l2 = j1; l2 < i2; ++l2) {
                    voxelshapebitset.a(j2, k2, l2, false);
                }
            }
        }

        return voxelshapebitset;
    }

    public VoxelShapeBitSet(VoxelShapeDiscrete voxelshapediscrete) {
        super(voxelshapediscrete.xSize, voxelshapediscrete.ySize, voxelshapediscrete.zSize);
        if (voxelshapediscrete instanceof VoxelShapeBitSet) {
            this.storage = (BitSet) ((VoxelShapeBitSet) voxelshapediscrete).storage.clone();
        } else {
            this.storage = new BitSet(this.xSize * this.ySize * this.zSize);

            for (int i = 0; i < this.xSize; ++i) {
                for (int j = 0; j < this.ySize; ++j) {
                    for (int k = 0; k < this.zSize; ++k) {
                        if (voxelshapediscrete.b(i, j, k)) {
                            this.storage.set(this.a(i, j, k));
                        }
                    }
                }
            }
        }

        this.xMin = voxelshapediscrete.a(EnumDirection.EnumAxis.X);
        this.yMin = voxelshapediscrete.a(EnumDirection.EnumAxis.Y);
        this.zMin = voxelshapediscrete.a(EnumDirection.EnumAxis.Z);
        this.xMax = voxelshapediscrete.b(EnumDirection.EnumAxis.X);
        this.yMax = voxelshapediscrete.b(EnumDirection.EnumAxis.Y);
        this.zMax = voxelshapediscrete.b(EnumDirection.EnumAxis.Z);
    }

    protected int a(int i, int j, int k) {
        return (i * this.ySize + j) * this.zSize + k;
    }

    @Override
    public boolean b(int i, int j, int k) {
        return this.storage.get(this.a(i, j, k));
    }

    private void a(int i, int j, int k, boolean flag) {
        this.storage.set(this.a(i, j, k));
        if (flag) {
            this.xMin = Math.min(this.xMin, i);
            this.yMin = Math.min(this.yMin, j);
            this.zMin = Math.min(this.zMin, k);
            this.xMax = Math.max(this.xMax, i + 1);
            this.yMax = Math.max(this.yMax, j + 1);
            this.zMax = Math.max(this.zMax, k + 1);
        }

    }

    @Override
    public void c(int i, int j, int k) {
        this.a(i, j, k, true);
    }

    @Override
    public boolean a() {
        return this.storage.isEmpty();
    }

    @Override
    public int a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.a(this.xMin, this.yMin, this.zMin);
    }

    @Override
    public int b(EnumDirection.EnumAxis enumdirection_enumaxis) {
        return enumdirection_enumaxis.a(this.xMax, this.yMax, this.zMax);
    }

    static VoxelShapeBitSet a(VoxelShapeDiscrete voxelshapediscrete, VoxelShapeDiscrete voxelshapediscrete1, VoxelShapeMerger voxelshapemerger, VoxelShapeMerger voxelshapemerger1, VoxelShapeMerger voxelshapemerger2, OperatorBoolean operatorboolean) {
        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(voxelshapemerger.size() - 1, voxelshapemerger1.size() - 1, voxelshapemerger2.size() - 1);
        int[] aint = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};

        voxelshapemerger.a((i, j, k) -> {
            boolean[] aboolean = new boolean[]{false};

            voxelshapemerger1.a((l, i1, j1) -> {
                boolean[] aboolean1 = new boolean[]{false};

                voxelshapemerger2.a((k1, l1, i2) -> {
                    if (operatorboolean.apply(voxelshapediscrete.d(i, l, k1), voxelshapediscrete1.d(j, i1, l1))) {
                        voxelshapebitset.storage.set(voxelshapebitset.a(k, j1, i2));
                        aint[2] = Math.min(aint[2], i2);
                        aint[5] = Math.max(aint[5], i2);
                        aboolean1[0] = true;
                    }

                    return true;
                });
                if (aboolean1[0]) {
                    aint[1] = Math.min(aint[1], j1);
                    aint[4] = Math.max(aint[4], j1);
                    aboolean[0] = true;
                }

                return true;
            });
            if (aboolean[0]) {
                aint[0] = Math.min(aint[0], k);
                aint[3] = Math.max(aint[3], k);
            }

            return true;
        });
        voxelshapebitset.xMin = aint[0];
        voxelshapebitset.yMin = aint[1];
        voxelshapebitset.zMin = aint[2];
        voxelshapebitset.xMax = aint[3] + 1;
        voxelshapebitset.yMax = aint[4] + 1;
        voxelshapebitset.zMax = aint[5] + 1;
        return voxelshapebitset;
    }

    protected static void a(VoxelShapeDiscrete voxelshapediscrete, VoxelShapeDiscrete.b voxelshapediscrete_b, boolean flag) {
        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(voxelshapediscrete);

        for (int i = 0; i < voxelshapebitset.xSize; ++i) {
            for (int j = 0; j < voxelshapebitset.ySize; ++j) {
                int k = -1;

                for (int l = 0; l <= voxelshapebitset.zSize; ++l) {
                    if (voxelshapebitset.d(i, j, l)) {
                        if (flag) {
                            if (k == -1) {
                                k = l;
                            }
                        } else {
                            voxelshapediscrete_b.consume(i, j, l, i + 1, j + 1, l + 1);
                        }
                    } else if (k != -1) {
                        int i1 = i;
                        int j1 = j;

                        voxelshapebitset.b(k, l, i, j);

                        while (voxelshapebitset.a(k, l, i1 + 1, j)) {
                            voxelshapebitset.b(k, l, i1 + 1, j);
                            ++i1;
                        }

                        while (voxelshapebitset.a(i, i1 + 1, k, l, j1 + 1)) {
                            for (int k1 = i; k1 <= i1; ++k1) {
                                voxelshapebitset.b(k, l, k1, j1 + 1);
                            }

                            ++j1;
                        }

                        voxelshapediscrete_b.consume(i, j, k, i1 + 1, j1 + 1, l);
                        k = -1;
                    }
                }
            }
        }

    }

    private boolean a(int i, int j, int k, int l) {
        return k < this.xSize && l < this.ySize ? this.storage.nextClearBit(this.a(k, l, i)) >= this.a(k, l, j) : false;
    }

    private boolean a(int i, int j, int k, int l, int i1) {
        for (int j1 = i; j1 < j; ++j1) {
            if (!this.a(k, l, j1, i1)) {
                return false;
            }
        }

        return true;
    }

    private void b(int i, int j, int k, int l) {
        this.storage.clear(this.a(k, l, i), this.a(k, l, j));
    }
}
