package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public class ProtoChunkExtension extends ProtoChunk {

    private final Chunk wrapped;

    public ProtoChunkExtension(Chunk chunk) {
        super(chunk.getPos(), ChunkConverter.EMPTY, chunk);
        this.wrapped = chunk;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return this.wrapped.getTileEntity(blockposition);
    }

    @Nullable
    @Override
    public IBlockData getType(BlockPosition blockposition) {
        return this.wrapped.getType(blockposition);
    }

    @Override
    public Fluid getFluid(BlockPosition blockposition) {
        return this.wrapped.getFluid(blockposition);
    }

    @Override
    public int O() {
        return this.wrapped.O();
    }

    @Nullable
    @Override
    public IBlockData setType(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return null;
    }

    @Override
    public void setTileEntity(TileEntity tileentity) {}

    @Override
    public void a(Entity entity) {}

    @Override
    public void a(ChunkStatus chunkstatus) {}

    @Override
    public ChunkSection[] getSections() {
        return this.wrapped.getSections();
    }

    @Override
    public void a(HeightMap.Type heightmap_type, long[] along) {}

    private HeightMap.Type c(HeightMap.Type heightmap_type) {
        return heightmap_type == HeightMap.Type.WORLD_SURFACE_WG ? HeightMap.Type.WORLD_SURFACE : (heightmap_type == HeightMap.Type.OCEAN_FLOOR_WG ? HeightMap.Type.OCEAN_FLOOR : heightmap_type);
    }

    @Override
    public int getHighestBlock(HeightMap.Type heightmap_type, int i, int j) {
        return this.wrapped.getHighestBlock(this.c(heightmap_type), i, j);
    }

    @Override
    public BlockPosition b(HeightMap.Type heightmap_type) {
        return this.wrapped.b(this.c(heightmap_type));
    }

    @Override
    public ChunkCoordIntPair getPos() {
        return this.wrapped.getPos();
    }

    @Nullable
    @Override
    public StructureStart<?> a(StructureGenerator<?> structuregenerator) {
        return this.wrapped.a(structuregenerator);
    }

    @Override
    public void a(StructureGenerator<?> structuregenerator, StructureStart<?> structurestart) {}

    @Override
    public Map<StructureGenerator<?>, StructureStart<?>> g() {
        return this.wrapped.g();
    }

    @Override
    public void a(Map<StructureGenerator<?>, StructureStart<?>> map) {}

    @Override
    public LongSet b(StructureGenerator<?> structuregenerator) {
        return this.wrapped.b(structuregenerator);
    }

    @Override
    public void a(StructureGenerator<?> structuregenerator, long i) {}

    @Override
    public Map<StructureGenerator<?>, LongSet> w() {
        return this.wrapped.w();
    }

    @Override
    public void b(Map<StructureGenerator<?>, LongSet> map) {}

    @Override
    public BiomeStorage getBiomeIndex() {
        return this.wrapped.getBiomeIndex();
    }

    @Override
    public void setNeedsSaving(boolean flag) {}

    @Override
    public boolean isNeedsSaving() {
        return false;
    }

    @Override
    public ChunkStatus getChunkStatus() {
        return this.wrapped.getChunkStatus();
    }

    @Override
    public void removeTileEntity(BlockPosition blockposition) {}

    @Override
    public void e(BlockPosition blockposition) {}

    @Override
    public void a(NBTTagCompound nbttagcompound) {}

    @Nullable
    @Override
    public NBTTagCompound f(BlockPosition blockposition) {
        return this.wrapped.f(blockposition);
    }

    @Nullable
    @Override
    public NBTTagCompound g(BlockPosition blockposition) {
        return this.wrapped.g(blockposition);
    }

    @Override
    public void a(BiomeStorage biomestorage) {}

    @Override
    public Stream<BlockPosition> n() {
        return this.wrapped.n();
    }

    @Override
    public ProtoChunkTickList<Block> o() {
        return new ProtoChunkTickList<>((block) -> {
            return block.getBlockData().isAir();
        }, this.getPos(), this);
    }

    @Override
    public ProtoChunkTickList<FluidType> p() {
        return new ProtoChunkTickList<>((fluidtype) -> {
            return fluidtype == FluidTypes.EMPTY;
        }, this.getPos(), this);
    }

    @Override
    public BitSet a(WorldGenStage.Features worldgenstage_features) {
        throw (UnsupportedOperationException) SystemUtils.c((Throwable) (new UnsupportedOperationException("Meaningless in this context")));
    }

    @Override
    public BitSet b(WorldGenStage.Features worldgenstage_features) {
        throw (UnsupportedOperationException) SystemUtils.c((Throwable) (new UnsupportedOperationException("Meaningless in this context")));
    }

    public Chunk v() {
        return this.wrapped;
    }

    @Override
    public boolean s() {
        return this.wrapped.s();
    }

    @Override
    public void b(boolean flag) {
        this.wrapped.b(flag);
    }
}
