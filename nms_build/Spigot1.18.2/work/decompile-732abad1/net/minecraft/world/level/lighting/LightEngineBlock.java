package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ILightAccess;
import net.minecraft.world.level.chunk.NibbleArray;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.apache.commons.lang3.mutable.MutableInt;

public final class LightEngineBlock extends LightEngineLayer<LightEngineStorageBlock.a, LightEngineStorageBlock> {

    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();
    private final BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition();

    public LightEngineBlock(ILightAccess ilightaccess) {
        super(ilightaccess, EnumSkyBlock.BLOCK, new LightEngineStorageBlock(ilightaccess));
    }

    private int getLightEmission(long i) {
        int j = BlockPosition.getX(i);
        int k = BlockPosition.getY(i);
        int l = BlockPosition.getZ(i);
        IBlockAccess iblockaccess = this.chunkSource.getChunkForLighting(SectionPosition.blockToSectionCoord(j), SectionPosition.blockToSectionCoord(l));

        return iblockaccess != null ? iblockaccess.getLightEmission(this.pos.set(j, k, l)) : 0;
    }

    @Override
    protected int computeLevelFromNeighbor(long i, long j, int k) {
        if (j == Long.MAX_VALUE) {
            return 15;
        } else if (i == Long.MAX_VALUE) {
            return k + 15 - this.getLightEmission(j);
        } else if (k >= 15) {
            return k;
        } else {
            int l = Integer.signum(BlockPosition.getX(j) - BlockPosition.getX(i));
            int i1 = Integer.signum(BlockPosition.getY(j) - BlockPosition.getY(i));
            int j1 = Integer.signum(BlockPosition.getZ(j) - BlockPosition.getZ(i));
            EnumDirection enumdirection = EnumDirection.fromNormal(l, i1, j1);

            if (enumdirection == null) {
                return 15;
            } else {
                MutableInt mutableint = new MutableInt();
                IBlockData iblockdata = this.getStateAndOpacity(j, mutableint);

                if (mutableint.getValue() >= 15) {
                    return 15;
                } else {
                    IBlockData iblockdata1 = this.getStateAndOpacity(i, (MutableInt) null);
                    VoxelShape voxelshape = this.getShape(iblockdata1, i, enumdirection);
                    VoxelShape voxelshape1 = this.getShape(iblockdata, j, enumdirection.getOpposite());

                    return VoxelShapes.faceShapeOccludes(voxelshape, voxelshape1) ? 15 : k + Math.max(1, mutableint.getValue());
                }
            }
        }
    }

    @Override
    protected void checkNeighborsAfterUpdate(long i, int j, boolean flag) {
        long k = SectionPosition.blockToSection(i);
        EnumDirection[] aenumdirection = LightEngineBlock.DIRECTIONS;
        int l = aenumdirection.length;

        for (int i1 = 0; i1 < l; ++i1) {
            EnumDirection enumdirection = aenumdirection[i1];
            long j1 = BlockPosition.offset(i, enumdirection);
            long k1 = SectionPosition.blockToSection(j1);

            if (k == k1 || ((LightEngineStorageBlock) this.storage).storingLightForSection(k1)) {
                this.checkNeighbor(i, j1, j, flag);
            }
        }

    }

    @Override
    protected int getComputedLevel(long i, long j, int k) {
        int l = k;

        if (Long.MAX_VALUE != j) {
            int i1 = this.computeLevelFromNeighbor(Long.MAX_VALUE, i, 0);

            if (k > i1) {
                l = i1;
            }

            if (l == 0) {
                return l;
            }
        }

        long j1 = SectionPosition.blockToSection(i);
        NibbleArray nibblearray = ((LightEngineStorageBlock) this.storage).getDataLayer(j1, true);
        EnumDirection[] aenumdirection = LightEngineBlock.DIRECTIONS;
        int k1 = aenumdirection.length;

        for (int l1 = 0; l1 < k1; ++l1) {
            EnumDirection enumdirection = aenumdirection[l1];
            long i2 = BlockPosition.offset(i, enumdirection);

            if (i2 != j) {
                long j2 = SectionPosition.blockToSection(i2);
                NibbleArray nibblearray1;

                if (j1 == j2) {
                    nibblearray1 = nibblearray;
                } else {
                    nibblearray1 = ((LightEngineStorageBlock) this.storage).getDataLayer(j2, true);
                }

                if (nibblearray1 != null) {
                    int k2 = this.computeLevelFromNeighbor(i2, i, this.getLevel(nibblearray1, i2));

                    if (l > k2) {
                        l = k2;
                    }

                    if (l == 0) {
                        return l;
                    }
                }
            }
        }

        return l;
    }

    @Override
    public void onBlockEmissionIncrease(BlockPosition blockposition, int i) {
        ((LightEngineStorageBlock) this.storage).runAllUpdates();
        this.checkEdge(Long.MAX_VALUE, blockposition.asLong(), 15 - i, true);
    }
}
