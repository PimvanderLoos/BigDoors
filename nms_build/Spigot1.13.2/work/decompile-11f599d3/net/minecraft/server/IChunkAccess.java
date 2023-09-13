package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;

public interface IChunkAccess extends IBlockAccess {

    @Nullable
    IBlockData setType(BlockPosition blockposition, IBlockData iblockdata, boolean flag);

    void a(BlockPosition blockposition, TileEntity tileentity);

    void a(Entity entity);

    void a(ChunkStatus chunkstatus);

    @Nullable
    default ChunkSection a() {
        ChunkSection[] achunksection = this.getSections();

        for (int i = achunksection.length - 1; i >= 0; --i) {
            if (achunksection[i] != Chunk.a) {
                return achunksection[i];
            }
        }

        return null;
    }

    default int b() {
        ChunkSection chunksection = this.a();

        return chunksection == null ? 0 : chunksection.getYPosition();
    }

    ChunkSection[] getSections();

    int a(EnumSkyBlock enumskyblock, BlockPosition blockposition, boolean flag);

    int a(BlockPosition blockposition, int i, boolean flag);

    boolean c(BlockPosition blockposition);

    int a(HeightMap.Type heightmap_type, int i, int j);

    ChunkCoordIntPair getPos();

    void setLastSaved(long i);

    @Nullable
    StructureStart a(String s);

    void a(String s, StructureStart structurestart);

    Map<String, StructureStart> e();

    @Nullable
    LongSet b(String s);

    void a(String s, long i);

    Map<String, LongSet> f();

    BiomeBase[] getBiomeIndex();

    ChunkStatus i();

    void d(BlockPosition blockposition);

    void a(EnumSkyBlock enumskyblock, boolean flag, BlockPosition blockposition, int i);

    default void e(BlockPosition blockposition) {
        LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", blockposition);
    }

    default void a(NBTTagCompound nbttagcompound) {
        LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
    }

    @Nullable
    default NBTTagCompound g(BlockPosition blockposition) {
        throw new UnsupportedOperationException();
    }

    default void a(BiomeBase[] abiomebase) {
        throw new UnsupportedOperationException();
    }

    default void a(HeightMap.Type... aheightmap_type) {
        throw new UnsupportedOperationException();
    }

    default List<BlockPosition> j() {
        throw new UnsupportedOperationException();
    }

    TickList<Block> k();

    TickList<FluidType> l();

    BitSet a(WorldGenStage.Features worldgenstage_features);
}
