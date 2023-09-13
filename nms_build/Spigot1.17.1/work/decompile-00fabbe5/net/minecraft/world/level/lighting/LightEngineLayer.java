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
        this.d();
    }

    @Override
    protected void f(long i) {
        this.storage.d();
        if (this.storage.g(SectionPosition.e(i))) {
            super.f(i);
        }

    }

    @Nullable
    private IBlockAccess a(int i, int j) {
        long k = ChunkCoordIntPair.pair(i, j);

        for (int l = 0; l < 2; ++l) {
            if (k == this.lastChunkPos[l]) {
                return this.lastChunk[l];
            }
        }

        IBlockAccess iblockaccess = this.chunkSource.c(i, j);

        for (int i1 = 1; i1 > 0; --i1) {
            this.lastChunkPos[i1] = this.lastChunkPos[i1 - 1];
            this.lastChunk[i1] = this.lastChunk[i1 - 1];
        }

        this.lastChunkPos[0] = k;
        this.lastChunk[0] = iblockaccess;
        return iblockaccess;
    }

    private void d() {
        Arrays.fill(this.lastChunkPos, ChunkCoordIntPair.INVALID_CHUNK_POS);
        Arrays.fill(this.lastChunk, (Object) null);
    }

    protected IBlockData a(long i, @Nullable MutableInt mutableint) {
        if (i == Long.MAX_VALUE) {
            if (mutableint != null) {
                mutableint.setValue(0);
            }

            return Blocks.AIR.getBlockData();
        } else {
            int j = SectionPosition.a(BlockPosition.a(i));
            int k = SectionPosition.a(BlockPosition.c(i));
            IBlockAccess iblockaccess = this.a(j, k);

            if (iblockaccess == null) {
                if (mutableint != null) {
                    mutableint.setValue(16);
                }

                return Blocks.BEDROCK.getBlockData();
            } else {
                this.pos.f(i);
                IBlockData iblockdata = iblockaccess.getType(this.pos);
                boolean flag = iblockdata.l() && iblockdata.e();

                if (mutableint != null) {
                    mutableint.setValue(iblockdata.b(this.chunkSource.getWorld(), (BlockPosition) this.pos));
                }

                return flag ? iblockdata : Blocks.AIR.getBlockData();
            }
        }
    }

    protected VoxelShape a(IBlockData iblockdata, long i, EnumDirection enumdirection) {
        return iblockdata.l() ? iblockdata.a(this.chunkSource.getWorld(), this.pos.f(i), enumdirection) : VoxelShapes.a();
    }

    public static int a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, IBlockData iblockdata1, BlockPosition blockposition1, EnumDirection enumdirection, int i) {
        boolean flag = iblockdata.l() && iblockdata.e();
        boolean flag1 = iblockdata1.l() && iblockdata1.e();

        if (!flag && !flag1) {
            return i;
        } else {
            VoxelShape voxelshape = flag ? iblockdata.c(iblockaccess, blockposition) : VoxelShapes.a();
            VoxelShape voxelshape1 = flag1 ? iblockdata1.c(iblockaccess, blockposition1) : VoxelShapes.a();

            return VoxelShapes.b(voxelshape, voxelshape1, enumdirection) ? 16 : i;
        }
    }

    @Override
    protected boolean a(long i) {
        return i == Long.MAX_VALUE;
    }

    @Override
    protected int a(long i, long j, int k) {
        return 0;
    }

    @Override
    protected int c(long i) {
        return i == Long.MAX_VALUE ? 0 : 15 - this.storage.i(i);
    }

    protected int a(NibbleArray nibblearray, long i) {
        return 15 - nibblearray.a(SectionPosition.b(BlockPosition.a(i)), SectionPosition.b(BlockPosition.b(i)), SectionPosition.b(BlockPosition.c(i)));
    }

    @Override
    protected void a(long i, int j) {
        this.storage.b(i, Math.min(15, 15 - j));
    }

    @Override
    protected int b(long i, long j, int k) {
        return 0;
    }

    @Override
    public boolean z_() {
        return this.b() || this.storage.b() || this.storage.a();
    }

    @Override
    public int a(int i, boolean flag, boolean flag1) {
        if (!this.runningLightUpdates) {
            if (this.storage.b()) {
                i = this.storage.b(i);
                if (i == 0) {
                    return i;
                }
            }

            this.storage.a(this, flag, flag1);
        }

        this.runningLightUpdates = true;
        if (this.b()) {
            i = this.b(i);
            this.d();
            if (i == 0) {
                return i;
            }
        }

        this.runningLightUpdates = false;
        this.storage.e();
        return i;
    }

    protected void a(long i, @Nullable NibbleArray nibblearray, boolean flag) {
        this.storage.a(i, nibblearray, flag);
    }

    @Nullable
    @Override
    public NibbleArray a(SectionPosition sectionposition) {
        return this.storage.h(sectionposition.s());
    }

    @Override
    public int b(BlockPosition blockposition) {
        return this.storage.d(blockposition.asLong());
    }

    public String b(long i) {
        int j = this.storage.c(i);

        return j.makeConcatWithConstants < invokedynamic > (j);
    }

    @Override
    public void a(BlockPosition blockposition) {
        long i = blockposition.asLong();

        this.f(i);
        EnumDirection[] aenumdirection = LightEngineLayer.DIRECTIONS;
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];

            this.f(BlockPosition.a(i, enumdirection));
        }

    }

    @Override
    public void a(BlockPosition blockposition, int i) {}

    @Override
    public void a(SectionPosition sectionposition, boolean flag) {
        this.storage.d(sectionposition.s(), flag);
    }

    @Override
    public void a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        long i = SectionPosition.f(SectionPosition.b(chunkcoordintpair.x, 0, chunkcoordintpair.z));

        this.storage.b(i, flag);
    }

    public void b(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        long i = SectionPosition.f(SectionPosition.b(chunkcoordintpair.x, 0, chunkcoordintpair.z));

        this.storage.c(i, flag);
    }
}
