package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.ticks.TickContainerAccess;
import net.minecraft.world.ticks.TickListEmpty;

public class ProtoChunkExtension extends ProtoChunk {

    private final Chunk wrapped;
    private final boolean allowWrites;

    public ProtoChunkExtension(Chunk chunk, boolean flag) {
        super(chunk.getPos(), ChunkConverter.EMPTY, chunk.levelHeightAccessor, chunk.getLevel().registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY), chunk.getBlendingData());
        this.wrapped = chunk;
        this.allowWrites = flag;
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPosition blockposition) {
        return this.wrapped.getBlockEntity(blockposition);
    }

    @Override
    public IBlockData getBlockState(BlockPosition blockposition) {
        return this.wrapped.getBlockState(blockposition);
    }

    @Override
    public Fluid getFluidState(BlockPosition blockposition) {
        return this.wrapped.getFluidState(blockposition);
    }

    @Override
    public int getMaxLightLevel() {
        return this.wrapped.getMaxLightLevel();
    }

    @Override
    public ChunkSection getSection(int i) {
        return this.allowWrites ? this.wrapped.getSection(i) : super.getSection(i);
    }

    @Nullable
    @Override
    public IBlockData setBlockState(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return this.allowWrites ? this.wrapped.setBlockState(blockposition, iblockdata, flag) : null;
    }

    @Override
    public void setBlockEntity(TileEntity tileentity) {
        if (this.allowWrites) {
            this.wrapped.setBlockEntity(tileentity);
        }

    }

    @Override
    public void addEntity(Entity entity) {
        if (this.allowWrites) {
            this.wrapped.addEntity(entity);
        }

    }

    @Override
    public void setStatus(ChunkStatus chunkstatus) {
        if (this.allowWrites) {
            super.setStatus(chunkstatus);
        }

    }

    @Override
    public ChunkSection[] getSections() {
        return this.wrapped.getSections();
    }

    @Override
    public void setHeightmap(HeightMap.Type heightmap_type, long[] along) {}

    private HeightMap.Type fixType(HeightMap.Type heightmap_type) {
        return heightmap_type == HeightMap.Type.WORLD_SURFACE_WG ? HeightMap.Type.WORLD_SURFACE : (heightmap_type == HeightMap.Type.OCEAN_FLOOR_WG ? HeightMap.Type.OCEAN_FLOOR : heightmap_type);
    }

    @Override
    public HeightMap getOrCreateHeightmapUnprimed(HeightMap.Type heightmap_type) {
        return this.wrapped.getOrCreateHeightmapUnprimed(heightmap_type);
    }

    @Override
    public int getHeight(HeightMap.Type heightmap_type, int i, int j) {
        return this.wrapped.getHeight(this.fixType(heightmap_type), i, j);
    }

    @Override
    public BiomeBase getNoiseBiome(int i, int j, int k) {
        return this.wrapped.getNoiseBiome(i, j, k);
    }

    @Override
    public ChunkCoordIntPair getPos() {
        return this.wrapped.getPos();
    }

    @Nullable
    @Override
    public StructureStart<?> getStartForFeature(StructureGenerator<?> structuregenerator) {
        return this.wrapped.getStartForFeature(structuregenerator);
    }

    @Override
    public void setStartForFeature(StructureGenerator<?> structuregenerator, StructureStart<?> structurestart) {}

    @Override
    public Map<StructureGenerator<?>, StructureStart<?>> getAllStarts() {
        return this.wrapped.getAllStarts();
    }

    @Override
    public void setAllStarts(Map<StructureGenerator<?>, StructureStart<?>> map) {}

    @Override
    public LongSet getReferencesForFeature(StructureGenerator<?> structuregenerator) {
        return this.wrapped.getReferencesForFeature(structuregenerator);
    }

    @Override
    public void addReferenceForFeature(StructureGenerator<?> structuregenerator, long i) {}

    @Override
    public Map<StructureGenerator<?>, LongSet> getAllReferences() {
        return this.wrapped.getAllReferences();
    }

    @Override
    public void setAllReferences(Map<StructureGenerator<?>, LongSet> map) {}

    @Override
    public void setUnsaved(boolean flag) {}

    @Override
    public boolean isUnsaved() {
        return false;
    }

    @Override
    public ChunkStatus getStatus() {
        return this.wrapped.getStatus();
    }

    @Override
    public void removeBlockEntity(BlockPosition blockposition) {}

    @Override
    public void markPosForPostprocessing(BlockPosition blockposition) {}

    @Override
    public void setBlockEntityNbt(NBTTagCompound nbttagcompound) {}

    @Nullable
    @Override
    public NBTTagCompound getBlockEntityNbt(BlockPosition blockposition) {
        return this.wrapped.getBlockEntityNbt(blockposition);
    }

    @Nullable
    @Override
    public NBTTagCompound getBlockEntityNbtForSaving(BlockPosition blockposition) {
        return this.wrapped.getBlockEntityNbtForSaving(blockposition);
    }

    @Override
    public Stream<BlockPosition> getLights() {
        return this.wrapped.getLights();
    }

    @Override
    public TickContainerAccess<Block> getBlockTicks() {
        return this.allowWrites ? this.wrapped.getBlockTicks() : TickListEmpty.emptyContainer();
    }

    @Override
    public TickContainerAccess<FluidType> getFluidTicks() {
        return this.allowWrites ? this.wrapped.getFluidTicks() : TickListEmpty.emptyContainer();
    }

    @Override
    public IChunkAccess.a getTicksForSerialization() {
        return this.wrapped.getTicksForSerialization();
    }

    @Nullable
    @Override
    public BlendingData getBlendingData() {
        return this.wrapped.getBlendingData();
    }

    @Override
    public void setBlendingData(BlendingData blendingdata) {
        this.wrapped.setBlendingData(blendingdata);
    }

    @Override
    public CarvingMask getCarvingMask(WorldGenStage.Features worldgenstage_features) {
        if (this.allowWrites) {
            return super.getCarvingMask(worldgenstage_features);
        } else {
            throw (UnsupportedOperationException) SystemUtils.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
        }
    }

    @Override
    public CarvingMask getOrCreateCarvingMask(WorldGenStage.Features worldgenstage_features) {
        if (this.allowWrites) {
            return super.getOrCreateCarvingMask(worldgenstage_features);
        } else {
            throw (UnsupportedOperationException) SystemUtils.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
        }
    }

    public Chunk getWrapped() {
        return this.wrapped;
    }

    @Override
    public boolean isLightCorrect() {
        return this.wrapped.isLightCorrect();
    }

    @Override
    public void setLightCorrect(boolean flag) {
        this.wrapped.setLightCorrect(flag);
    }

    @Override
    public void fillBiomesFromNoise(BiomeResolver biomeresolver, Climate.Sampler climate_sampler) {
        if (this.allowWrites) {
            this.wrapped.fillBiomesFromNoise(biomeresolver, climate_sampler);
        }

    }
}
