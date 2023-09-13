package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ILightAccess;
import net.minecraft.world.level.chunk.NibbleArray;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.apache.commons.lang3.mutable.MutableInt;

public final class LightEngineSky extends LightEngineLayer<LightEngineStorageSky.a, LightEngineStorageSky> {

    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();
    private static final EnumDirection[] HORIZONTALS = new EnumDirection[]{EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.WEST, EnumDirection.EAST};

    public LightEngineSky(ILightAccess ilightaccess) {
        super(ilightaccess, EnumSkyBlock.SKY, new LightEngineStorageSky(ilightaccess));
    }

    @Override
    protected int b(long i, long j, int k) {
        if (j != Long.MAX_VALUE && i != Long.MAX_VALUE) {
            if (k >= 15) {
                return k;
            } else {
                MutableInt mutableint = new MutableInt();
                IBlockData iblockdata = this.a(j, mutableint);

                if (mutableint.getValue() >= 15) {
                    return 15;
                } else {
                    int l = BlockPosition.a(i);
                    int i1 = BlockPosition.b(i);
                    int j1 = BlockPosition.c(i);
                    int k1 = BlockPosition.a(j);
                    int l1 = BlockPosition.b(j);
                    int i2 = BlockPosition.c(j);
                    int j2 = Integer.signum(k1 - l);
                    int k2 = Integer.signum(l1 - i1);
                    int l2 = Integer.signum(i2 - j1);
                    EnumDirection enumdirection = EnumDirection.a(j2, k2, l2);

                    if (enumdirection == null) {
                        throw new IllegalStateException(String.format("Light was spread in illegal direction %d, %d, %d", j2, k2, l2));
                    } else {
                        IBlockData iblockdata1 = this.a(i, (MutableInt) null);
                        VoxelShape voxelshape = this.a(iblockdata1, i, enumdirection);
                        VoxelShape voxelshape1 = this.a(iblockdata, j, enumdirection.opposite());

                        if (VoxelShapes.b(voxelshape, voxelshape1)) {
                            return 15;
                        } else {
                            boolean flag = l == k1 && j1 == i2;
                            boolean flag1 = flag && i1 > l1;

                            return flag1 && k == 0 && mutableint.getValue() == 0 ? 0 : k + Math.max(1, mutableint.getValue());
                        }
                    }
                }
            }
        } else {
            return 15;
        }
    }

    @Override
    protected void a(long i, int j, boolean flag) {
        long k = SectionPosition.e(i);
        int l = BlockPosition.b(i);
        int i1 = SectionPosition.b(l);
        int j1 = SectionPosition.a(l);
        int k1;

        if (i1 != 0) {
            k1 = 0;
        } else {
            int l1;

            for (l1 = 0; !((LightEngineStorageSky) this.storage).g(SectionPosition.a(k, 0, -l1 - 1, 0)) && ((LightEngineStorageSky) this.storage).a(j1 - l1 - 1); ++l1) {
                ;
            }

            k1 = l1;
        }

        long i2 = BlockPosition.a(i, 0, -1 - k1 * 16, 0);
        long j2 = SectionPosition.e(i2);

        if (k == j2 || ((LightEngineStorageSky) this.storage).g(j2)) {
            this.b(i, i2, j, flag);
        }

        long k2 = BlockPosition.a(i, EnumDirection.UP);
        long l2 = SectionPosition.e(k2);

        if (k == l2 || ((LightEngineStorageSky) this.storage).g(l2)) {
            this.b(i, k2, j, flag);
        }

        EnumDirection[] aenumdirection = LightEngineSky.HORIZONTALS;
        int i3 = aenumdirection.length;
        int j3 = 0;

        while (j3 < i3) {
            EnumDirection enumdirection = aenumdirection[j3];
            int k3 = 0;

            while (true) {
                long l3 = BlockPosition.a(i, enumdirection.getAdjacentX(), -k3, enumdirection.getAdjacentZ());
                long i4 = SectionPosition.e(l3);

                if (k == i4) {
                    this.b(i, l3, j, flag);
                } else {
                    if (((LightEngineStorageSky) this.storage).g(i4)) {
                        long j4 = BlockPosition.a(i, 0, -k3, 0);

                        this.b(j4, l3, j, flag);
                    }

                    ++k3;
                    if (k3 <= k1 * 16) {
                        continue;
                    }
                }

                ++j3;
                break;
            }
        }

    }

    @Override
    protected int a(long i, long j, int k) {
        int l = k;
        long i1 = SectionPosition.e(i);
        NibbleArray nibblearray = ((LightEngineStorageSky) this.storage).a(i1, true);
        EnumDirection[] aenumdirection = LightEngineSky.DIRECTIONS;
        int j1 = aenumdirection.length;

        for (int k1 = 0; k1 < j1; ++k1) {
            EnumDirection enumdirection = aenumdirection[k1];
            long l1 = BlockPosition.a(i, enumdirection);

            if (l1 != j) {
                long i2 = SectionPosition.e(l1);
                NibbleArray nibblearray1;

                if (i1 == i2) {
                    nibblearray1 = nibblearray;
                } else {
                    nibblearray1 = ((LightEngineStorageSky) this.storage).a(i2, true);
                }

                int j2;

                if (nibblearray1 != null) {
                    j2 = this.a(nibblearray1, l1);
                } else {
                    if (enumdirection == EnumDirection.DOWN) {
                        continue;
                    }

                    j2 = 15 - ((LightEngineStorageSky) this.storage).e(l1, true);
                }

                int k2 = this.b(l1, i, j2);

                if (l > k2) {
                    l = k2;
                }

                if (l == 0) {
                    return l;
                }
            }
        }

        return l;
    }

    @Override
    protected void f(long i) {
        ((LightEngineStorageSky) this.storage).d();
        long j = SectionPosition.e(i);

        if (((LightEngineStorageSky) this.storage).g(j)) {
            super.f(i);
        } else {
            for (i = BlockPosition.e(i); !((LightEngineStorageSky) this.storage).g(j) && !((LightEngineStorageSky) this.storage).m(j); i = BlockPosition.a(i, 0, 16, 0)) {
                j = SectionPosition.a(j, EnumDirection.UP);
            }

            if (((LightEngineStorageSky) this.storage).g(j)) {
                super.f(i);
            }
        }

    }

    @Override
    public String b(long i) {
        String s = super.b(i);

        return s + (((LightEngineStorageSky) this.storage).m(i) ? "*" : "");
    }
}
