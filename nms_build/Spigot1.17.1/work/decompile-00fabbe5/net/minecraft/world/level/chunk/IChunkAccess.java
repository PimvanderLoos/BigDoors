package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.FluidType;
import org.apache.logging.log4j.LogManager;

public interface IChunkAccess extends IBlockAccess, IStructureAccess {

    default GameEventDispatcher a(int i) {
        return GameEventDispatcher.NOOP;
    }

    @Nullable
    IBlockData setType(BlockPosition blockposition, IBlockData iblockdata, boolean flag);

    void setTileEntity(TileEntity tileentity);

    void a(Entity entity);

    @Nullable
    default ChunkSection a() {
        ChunkSection[] achunksection = this.getSections();

        for (int i = achunksection.length - 1; i >= 0; --i) {
            ChunkSection chunksection = achunksection[i];

            if (!ChunkSection.a(chunksection)) {
                return chunksection;
            }
        }

        return null;
    }

    default int b() {
        ChunkSection chunksection = this.a();

        return chunksection == null ? this.getMinBuildHeight() : chunksection.getYPosition();
    }

    Set<BlockPosition> c();

    ChunkSection[] getSections();

    default ChunkSection b(int i) {
        ChunkSection[] achunksection = this.getSections();

        if (achunksection[i] == Chunk.EMPTY_SECTION) {
            achunksection[i] = new ChunkSection(this.getSectionYFromSectionIndex(i));
        }

        return achunksection[i];
    }

    Collection<Entry<HeightMap.Type, HeightMap>> e();

    default void a(HeightMap.Type heightmap_type, long[] along) {
        this.a(heightmap_type).a(this, heightmap_type, along);
    }

    HeightMap a(HeightMap.Type heightmap_type);

    int getHighestBlock(HeightMap.Type heightmap_type, int i, int j);

    BlockPosition b(HeightMap.Type heightmap_type);

    ChunkCoordIntPair getPos();

    Map<StructureGenerator<?>, StructureStart<?>> g();

    void a(Map<StructureGenerator<?>, StructureStart<?>> map);

    default boolean a(int i, int j) {
        if (i < this.getMinBuildHeight()) {
            i = this.getMinBuildHeight();
        }

        if (j >= this.getMaxBuildHeight()) {
            j = this.getMaxBuildHeight() - 1;
        }

        for (int k = i; k <= j; k += 16) {
            if (!ChunkSection.a(this.getSections()[this.getSectionIndex(k)])) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    BiomeStorage getBiomeIndex();

    void setNeedsSaving(boolean flag);

    boolean isNeedsSaving();

    ChunkStatus getChunkStatus();

    void removeTileEntity(BlockPosition blockposition);

    default void e(BlockPosition blockposition) {
        LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", blockposition);
    }

    ShortList[] k();

    default void a(short short0, int i) {
        a(this.k(), i).add(short0);
    }

    default void a(NBTTagCompound nbttagcompound) {
        LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
    }

    @Nullable
    NBTTagCompound f(BlockPosition blockposition);

    @Nullable
    NBTTagCompound g(BlockPosition blockposition);

    Stream<BlockPosition> n();

    TickList<Block> o();

    TickList<FluidType> p();

    ChunkConverter q();

    void setInhabitedTime(long i);

    long getInhabitedTime();

    static ShortList a(ShortList[] ashortlist, int i) {
        if (ashortlist[i] == null) {
            ashortlist[i] = new ShortArrayList();
        }

        return ashortlist[i];
    }

    boolean s();

    void b(boolean flag);
}
