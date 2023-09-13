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
    protected int computeLevelFromNeighbor(long i, long j, int k) {
        if (j != Long.MAX_VALUE && i != Long.MAX_VALUE) {
            if (k >= 15) {
                return k;
            } else {
                MutableInt mutableint = new MutableInt();
                IBlockData iblockdata = this.getStateAndOpacity(j, mutableint);

                if (mutableint.getValue() >= 15) {
                    return 15;
                } else {
                    int l = BlockPosition.getX(i);
                    int i1 = BlockPosition.getY(i);
                    int j1 = BlockPosition.getZ(i);
                    int k1 = BlockPosition.getX(j);
                    int l1 = BlockPosition.getY(j);
                    int i2 = BlockPosition.getZ(j);
                    int j2 = Integer.signum(k1 - l);
                    int k2 = Integer.signum(l1 - i1);
                    int l2 = Integer.signum(i2 - j1);
                    EnumDirection enumdirection = EnumDirection.fromNormal(j2, k2, l2);

                    if (enumdirection == null) {
                        throw new IllegalStateException(String.format("Light was spread in illegal direction %d, %d, %d", j2, k2, l2));
                    } else {
                        IBlockData iblockdata1 = this.getStateAndOpacity(i, (MutableInt) null);
                        VoxelShape voxelshape = this.getShape(iblockdata1, i, enumdirection);
                        VoxelShape voxelshape1 = this.getShape(iblockdata, j, enumdirection.getOpposite());

                        if (VoxelShapes.faceShapeOccludes(voxelshape, voxelshape1)) {
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
    protected void checkNeighborsAfterUpdate(long i, int j, boolean flag) {
        long k = SectionPosition.blockToSection(i);
        int l = BlockPosition.getY(i);
        int i1 = SectionPosition.sectionRelative(l);
        int j1 = SectionPosition.blockToSectionCoord(l);
        int k1;

        if (i1 != 0) {
            k1 = 0;
        } else {
            int l1;

            for (l1 = 0; !((LightEngineStorageSky) this.storage).storingLightForSection(SectionPosition.offset(k, 0, -l1 - 1, 0)) && ((LightEngineStorageSky) this.storage).hasSectionsBelow(j1 - l1 - 1); ++l1) {
                ;
            }

            k1 = l1;
        }

        long i2 = BlockPosition.offset(i, 0, -1 - k1 * 16, 0);
        long j2 = SectionPosition.blockToSection(i2);

        if (k == j2 || ((LightEngineStorageSky) this.storage).storingLightForSection(j2)) {
            this.checkNeighbor(i, i2, j, flag);
        }

        long k2 = BlockPosition.offset(i, EnumDirection.UP);
        long l2 = SectionPosition.blockToSection(k2);

        if (k == l2 || ((LightEngineStorageSky) this.storage).storingLightForSection(l2)) {
            this.checkNeighbor(i, k2, j, flag);
        }

        EnumDirection[] aenumdirection = LightEngineSky.HORIZONTALS;
        int i3 = aenumdirection.length;
        int j3 = 0;

        while (j3 < i3) {
            EnumDirection enumdirection = aenumdirection[j3];
            int k3 = 0;

            while (true) {
                long l3 = BlockPosition.offset(i, enumdirection.getStepX(), -k3, enumdirection.getStepZ());
                long i4 = SectionPosition.blockToSection(l3);

                if (k == i4) {
                    this.checkNeighbor(i, l3, j, flag);
                } else {
                    if (((LightEngineStorageSky) this.storage).storingLightForSection(i4)) {
                        long j4 = BlockPosition.offset(i, 0, -k3, 0);

                        this.checkNeighbor(j4, l3, j, flag);
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
    protected int getComputedLevel(long i, long j, int k) {
        int l = k;
        long i1 = SectionPosition.blockToSection(i);
        NibbleArray nibblearray = ((LightEngineStorageSky) this.storage).getDataLayer(i1, true);
        EnumDirection[] aenumdirection = LightEngineSky.DIRECTIONS;
        int j1 = aenumdirection.length;

        for (int k1 = 0; k1 < j1; ++k1) {
            EnumDirection enumdirection = aenumdirection[k1];
            long l1 = BlockPosition.offset(i, enumdirection);

            if (l1 != j) {
                long i2 = SectionPosition.blockToSection(l1);
                NibbleArray nibblearray1;

                if (i1 == i2) {
                    nibblearray1 = nibblearray;
                } else {
                    nibblearray1 = ((LightEngineStorageSky) this.storage).getDataLayer(i2, true);
                }

                int j2;

                if (nibblearray1 != null) {
                    j2 = this.getLevel(nibblearray1, l1);
                } else {
                    if (enumdirection == EnumDirection.DOWN) {
                        continue;
                    }

                    j2 = 15 - ((LightEngineStorageSky) this.storage).getLightValue(l1, true);
                }

                int k2 = this.computeLevelFromNeighbor(l1, i, j2);

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
    protected void checkNode(long i) {
        ((LightEngineStorageSky) this.storage).runAllUpdates();
        long j = SectionPosition.blockToSection(i);

        if (((LightEngineStorageSky) this.storage).storingLightForSection(j)) {
            super.checkNode(i);
        } else {
            for (i = BlockPosition.getFlatIndex(i); !((LightEngineStorageSky) this.storage).storingLightForSection(j) && !((LightEngineStorageSky) this.storage).isAboveData(j); i = BlockPosition.offset(i, 0, 16, 0)) {
                j = SectionPosition.offset(j, EnumDirection.UP);
            }

            if (((LightEngineStorageSky) this.storage).storingLightForSection(j)) {
                super.checkNode(i);
            }
        }

    }

    @Override
    public String getDebugData(long i) {
        String s = super.getDebugData(i);

        return s + (((LightEngineStorageSky) this.storage).isAboveData(i) ? "*" : "");
    }
}
