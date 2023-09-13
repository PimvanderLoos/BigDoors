package net.minecraft.world.level.lighting;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ILightAccess;
import net.minecraft.world.level.chunk.NibbleArray;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class LightEngineLayer<M extends LightEngineStorageArray<M>, S extends LightEngineStorage<M>> extends LightEngineGraph implements LightEngineLayerEventListener {

    public static final long SELF_SOURCE = Long.MAX_VALUE;
    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();
    protected final ILightAccess chunkSource;
    protected final EnumSkyBlock layer;
    protected final S storage;
    private boolean runningLightUpdates;
    protected final BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition();
    private static final int CACHE_SIZE = 2;
    private final long[] lastChunkPos = new long[2];
    private final IBlockAccess[] lastChunk = new IBlockAccess[2];

    public LightEngineLayer(ILightAccess ilightaccess, EnumSkyBlock enumskyblock, S s0) {
        super(16, 256, 8192);
        this.chunkSource = ilightaccess;
        this.layer = enumskyblock;
        this.storage = s0;
        this.clearCache();
    }

    @Override
    protected void checkNode(long i) {
        this.storage.runAllUpdates();
        if (this.storage.storingLightForSection(SectionPosition.blockToSection(i))) {
            super.checkNode(i);
        }

    }

    @Nullable
    private IBlockAccess getChunk(int i, int j) {
        long k = ChunkCoordIntPair.asLong(i, j);

        for (int l = 0; l < 2; ++l) {
            if (k == this.lastChunkPos[l]) {
                return this.lastChunk[l];
            }
        }

        IBlockAccess iblockaccess = this.chunkSource.getChunkForLighting(i, j);

        for (int i1 = 1; i1 > 0; --i1) {
            this.lastChunkPos[i1] = this.lastChunkPos[i1 - 1];
            this.lastChunk[i1] = this.lastChunk[i1 - 1];
        }

        this.lastChunkPos[0] = k;
        this.lastChunk[0] = iblockaccess;
        return iblockaccess;
    }

    private void clearCache() {
        Arrays.fill(this.lastChunkPos, ChunkCoordIntPair.INVALID_CHUNK_POS);
        Arrays.fill(this.lastChunk, (Object) null);
    }

    protected IBlockData getStateAndOpacity(long i, @Nullable MutableInt mutableint) {
        if (i == Long.MAX_VALUE) {
            if (mutableint != null) {
                mutableint.setValue(0);
            }

            return Blocks.AIR.defaultBlockState();
        } else {
            int j = SectionPosition.blockToSectionCoord(BlockPosition.getX(i));
            int k = SectionPosition.blockToSectionCoord(BlockPosition.getZ(i));
            IBlockAccess iblockaccess = this.getChunk(j, k);

            if (iblockaccess == null) {
                if (mutableint != null) {
                    mutableint.setValue(16);
                }

                return Blocks.BEDROCK.defaultBlockState();
            } else {
                this.pos.set(i);
                IBlockData iblockdata = iblockaccess.getBlockState(this.pos);
                boolean flag = iblockdata.canOcclude() && iblockdata.useShapeForLightOcclusion();

                if (mutableint != null) {
                    mutableint.setValue(iblockdata.getLightBlock(this.chunkSource.getLevel(), this.pos));
                }

                return flag ? iblockdata : Blocks.AIR.defaultBlockState();
            }
        }
    }

    protected VoxelShape getShape(IBlockData iblockdata, long i, EnumDirection enumdirection) {
        return iblockdata.canOcclude() ? iblockdata.getFaceOcclusionShape(this.chunkSource.getLevel(), this.pos.set(i), enumdirection) : VoxelShapes.empty();
    }

    public static int getLightBlockInto(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, IBlockData iblockdata1, BlockPosition blockposition1, EnumDirection enumdirection, int i) {
        boolean flag = iblockdata.canOcclude() && iblockdata.useShapeForLightOcclusion();
        boolean flag1 = iblockdata1.canOcclude() && iblockdata1.useShapeForLightOcclusion();

        if (!flag && !flag1) {
            return i;
        } else {
            VoxelShape voxelshape = flag ? iblockdata.getOcclusionShape(iblockaccess, blockposition) : VoxelShapes.empty();
            VoxelShape voxelshape1 = flag1 ? iblockdata1.getOcclusionShape(iblockaccess, blockposition1) : VoxelShapes.empty();

            return VoxelShapes.mergedFaceOccludes(voxelshape, voxelshape1, enumdirection) ? 16 : i;
        }
    }

    @Override
    protected boolean isSource(long i) {
        return i == Long.MAX_VALUE;
    }

    @Override
    protected int getComputedLevel(long i, long j, int k) {
        return 0;
    }

    @Override
    protected int getLevel(long i) {
        return i == Long.MAX_VALUE ? 0 : 15 - this.storage.getStoredLevel(i);
    }

    protected int getLevel(NibbleArray nibblearray, long i) {
        return 15 - nibblearray.get(SectionPosition.sectionRelative(BlockPosition.getX(i)), SectionPosition.sectionRelative(BlockPosition.getY(i)), SectionPosition.sectionRelative(BlockPosition.getZ(i)));
    }

    @Override
    protected void setLevel(long i, int j) {
        this.storage.setStoredLevel(i, Math.min(15, 15 - j));
    }

    @Override
    protected int computeLevelFromNeighbor(long i, long j, int k) {
        return 0;
    }

    @Override
    public boolean hasLightWork() {
        return this.hasWork() || this.storage.hasWork() || this.storage.hasInconsistencies();
    }

    @Override
    public int runUpdates(int i, boolean flag, boolean flag1) {
        if (!this.runningLightUpdates) {
            if (this.storage.hasWork()) {
                i = this.storage.runUpdates(i);
                if (i == 0) {
                    return i;
                }
            }

            this.storage.markNewInconsistencies(this, flag, flag1);
        }

        this.runningLightUpdates = true;
        if (this.hasWork()) {
            i = this.runUpdates(i);
            this.clearCache();
            if (i == 0) {
                return i;
            }
        }

        this.runningLightUpdates = false;
        this.storage.swapSectionMap();
        return i;
    }

    protected void queueSectionData(long i, @Nullable NibbleArray nibblearray, boolean flag) {
        this.storage.queueSectionData(i, nibblearray, flag);
    }

    @Nullable
    @Override
    public NibbleArray getDataLayerData(SectionPosition sectionposition) {
        return this.storage.getDataLayerData(sectionposition.asLong());
    }

    @Override
    public int getLightValue(BlockPosition blockposition) {
        return this.storage.getLightValue(blockposition.asLong());
    }

    public String getDebugData(long i) {
        int j = this.storage.getLevel(i);

        return j.makeConcatWithConstants < invokedynamic > (j);
    }

    @Override
    public void checkBlock(BlockPosition blockposition) {
        long i = blockposition.asLong();

        this.checkNode(i);
        EnumDirection[] aenumdirection = LightEngineLayer.DIRECTIONS;
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];

            this.checkNode(BlockPosition.offset(i, enumdirection));
        }

    }

    @Override
    public void onBlockEmissionIncrease(BlockPosition blockposition, int i) {}

    @Override
    public void updateSectionStatus(SectionPosition sectionposition, boolean flag) {
        this.storage.updateSectionStatus(sectionposition.asLong(), flag);
    }

    @Override
    public void enableLightSources(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        long i = SectionPosition.getZeroNode(SectionPosition.asLong(chunkcoordintpair.x, 0, chunkcoordintpair.z));

        this.storage.enableLightSources(i, flag);
    }

    public void retainData(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        long i = SectionPosition.getZeroNode(SectionPosition.asLong(chunkcoordintpair.x, 0, chunkcoordintpair.z));

        this.storage.retainData(i, flag);
    }
}
