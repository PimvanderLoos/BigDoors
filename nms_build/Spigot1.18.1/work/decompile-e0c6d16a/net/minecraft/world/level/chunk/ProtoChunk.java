package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ProtoChunkTickList;
import net.minecraft.world.ticks.TickContainerAccess;

public class ProtoChunk extends IChunkAccess {

    @Nullable
    private volatile LightEngine lightEngine;
    private volatile ChunkStatus status;
    private final List<NBTTagCompound> entities;
    private final List<BlockPosition> lights;
    private final Map<WorldGenStage.Features, CarvingMask> carvingMasks;
    @Nullable
    private BelowZeroRetrogen belowZeroRetrogen;
    private final ProtoChunkTickList<Block> blockTicks;
    private final ProtoChunkTickList<FluidType> fluidTicks;

    public ProtoChunk(ChunkCoordIntPair chunkcoordintpair, ChunkConverter chunkconverter, LevelHeightAccessor levelheightaccessor, IRegistry<BiomeBase> iregistry, @Nullable BlendingData blendingdata) {
        this(chunkcoordintpair, chunkconverter, (ChunkSection[]) null, new ProtoChunkTickList<>(), new ProtoChunkTickList<>(), levelheightaccessor, iregistry, blendingdata);
    }

    public ProtoChunk(ChunkCoordIntPair chunkcoordintpair, ChunkConverter chunkconverter, @Nullable ChunkSection[] achunksection, ProtoChunkTickList<Block> protochunkticklist, ProtoChunkTickList<FluidType> protochunkticklist1, LevelHeightAccessor levelheightaccessor, IRegistry<BiomeBase> iregistry, @Nullable BlendingData blendingdata) {
        super(chunkcoordintpair, chunkconverter, levelheightaccessor, iregistry, 0L, achunksection, blendingdata);
        this.status = ChunkStatus.EMPTY;
        this.entities = Lists.newArrayList();
        this.lights = Lists.newArrayList();
        this.carvingMasks = new Object2ObjectArrayMap();
        this.blockTicks = protochunkticklist;
        this.fluidTicks = protochunkticklist1;
    }

    @Override
    public TickContainerAccess<Block> getBlockTicks() {
        return this.blockTicks;
    }

    @Override
    public TickContainerAccess<FluidType> getFluidTicks() {
        return this.fluidTicks;
    }

    @Override
    public IChunkAccess.a getTicksForSerialization() {
        return new IChunkAccess.a(this.blockTicks, this.fluidTicks);
    }

    @Override
    public IBlockData getBlockState(BlockPosition blockposition) {
        int i = blockposition.getY();

        if (this.isOutsideBuildHeight(i)) {
            return Blocks.VOID_AIR.defaultBlockState();
        } else {
            ChunkSection chunksection = this.getSection(this.getSectionIndex(i));

            return chunksection.hasOnlyAir() ? Blocks.AIR.defaultBlockState() : chunksection.getBlockState(blockposition.getX() & 15, i & 15, blockposition.getZ() & 15);
        }
    }

    @Override
    public Fluid getFluidState(BlockPosition blockposition) {
        int i = blockposition.getY();

        if (this.isOutsideBuildHeight(i)) {
            return FluidTypes.EMPTY.defaultFluidState();
        } else {
            ChunkSection chunksection = this.getSection(this.getSectionIndex(i));

            return chunksection.hasOnlyAir() ? FluidTypes.EMPTY.defaultFluidState() : chunksection.getFluidState(blockposition.getX() & 15, i & 15, blockposition.getZ() & 15);
        }
    }

    @Override
    public Stream<BlockPosition> getLights() {
        return this.lights.stream();
    }

    public ShortList[] getPackedLights() {
        ShortList[] ashortlist = new ShortList[this.getSectionsCount()];
        Iterator iterator = this.lights.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            IChunkAccess.getOrCreateOffsetList(ashortlist, this.getSectionIndex(blockposition.getY())).add(packOffsetCoordinates(blockposition));
        }

        return ashortlist;
    }

    public void addLight(short short0, int i) {
        this.addLight(unpackOffsetCoordinates(short0, this.getSectionYFromSectionIndex(i), this.chunkPos));
    }

    public void addLight(BlockPosition blockposition) {
        this.lights.add(blockposition.immutable());
    }

    @Nullable
    @Override
    public IBlockData setBlockState(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        if (j >= this.getMinBuildHeight() && j < this.getMaxBuildHeight()) {
            int l = this.getSectionIndex(j);

            if (this.sections[l].hasOnlyAir() && iblockdata.is(Blocks.AIR)) {
                return iblockdata;
            } else {
                if (iblockdata.getLightEmission() > 0) {
                    this.lights.add(new BlockPosition((i & 15) + this.getPos().getMinBlockX(), j, (k & 15) + this.getPos().getMinBlockZ()));
                }

                ChunkSection chunksection = this.getSection(l);
                IBlockData iblockdata1 = chunksection.setBlockState(i & 15, j & 15, k & 15, iblockdata);

                if (this.status.isOrAfter(ChunkStatus.FEATURES) && iblockdata != iblockdata1 && (iblockdata.getLightBlock(this, blockposition) != iblockdata1.getLightBlock(this, blockposition) || iblockdata.getLightEmission() != iblockdata1.getLightEmission() || iblockdata.useShapeForLightOcclusion() || iblockdata1.useShapeForLightOcclusion())) {
                    this.lightEngine.checkBlock(blockposition);
                }

                EnumSet<HeightMap.Type> enumset = this.getStatus().heightmapsAfter();
                EnumSet<HeightMap.Type> enumset1 = null;
                Iterator iterator = enumset.iterator();

                HeightMap.Type heightmap_type;

                while (iterator.hasNext()) {
                    heightmap_type = (HeightMap.Type) iterator.next();
                    HeightMap heightmap = (HeightMap) this.heightmaps.get(heightmap_type);

                    if (heightmap == null) {
                        if (enumset1 == null) {
                            enumset1 = EnumSet.noneOf(HeightMap.Type.class);
                        }

                        enumset1.add(heightmap_type);
                    }
                }

                if (enumset1 != null) {
                    HeightMap.primeHeightmaps(this, enumset1);
                }

                iterator = enumset.iterator();

                while (iterator.hasNext()) {
                    heightmap_type = (HeightMap.Type) iterator.next();
                    ((HeightMap) this.heightmaps.get(heightmap_type)).update(i & 15, j, k & 15, iblockdata);
                }

                return iblockdata1;
            }
        } else {
            return Blocks.VOID_AIR.defaultBlockState();
        }
    }

    @Override
    public void setBlockEntity(TileEntity tileentity) {
        this.blockEntities.put(tileentity.getBlockPos(), tileentity);
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPosition blockposition) {
        return (TileEntity) this.blockEntities.get(blockposition);
    }

    public Map<BlockPosition, TileEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void addEntity(NBTTagCompound nbttagcompound) {
        this.entities.add(nbttagcompound);
    }

    @Override
    public void addEntity(Entity entity) {
        if (!entity.isPassenger()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entity.save(nbttagcompound);
            this.addEntity(nbttagcompound);
        }
    }

    @Override
    public void setStartForFeature(StructureGenerator<?> structuregenerator, StructureStart<?> structurestart) {
        BelowZeroRetrogen belowzeroretrogen = this.getBelowZeroRetrogen();

        if (belowzeroretrogen != null && structurestart.isValid()) {
            StructureBoundingBox structureboundingbox = structurestart.getBoundingBox();
            LevelHeightAccessor levelheightaccessor = this.getHeightAccessorForGeneration();

            if (structureboundingbox.minY() < levelheightaccessor.getMinBuildHeight() || structureboundingbox.maxY() >= levelheightaccessor.getMaxBuildHeight()) {
                return;
            }
        }

        super.setStartForFeature(structuregenerator, structurestart);
    }

    public List<NBTTagCompound> getEntities() {
        return this.entities;
    }

    @Override
    public ChunkStatus getStatus() {
        return this.status;
    }

    public void setStatus(ChunkStatus chunkstatus) {
        this.status = chunkstatus;
        if (this.belowZeroRetrogen != null && chunkstatus.isOrAfter(this.belowZeroRetrogen.targetStatus())) {
            this.setBelowZeroRetrogen((BelowZeroRetrogen) null);
        }

        this.setUnsaved(true);
    }

    @Override
    public BiomeBase getNoiseBiome(int i, int j, int k) {
        if (!this.getStatus().isOrAfter(ChunkStatus.BIOMES) && (this.belowZeroRetrogen == null || !this.belowZeroRetrogen.targetStatus().isOrAfter(ChunkStatus.BIOMES))) {
            throw new IllegalStateException("Asking for biomes before we have biomes");
        } else {
            return super.getNoiseBiome(i, j, k);
        }
    }

    public static short packOffsetCoordinates(BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        int l = i & 15;
        int i1 = j & 15;
        int j1 = k & 15;

        return (short) (l | i1 << 4 | j1 << 8);
    }

    public static BlockPosition unpackOffsetCoordinates(short short0, int i, ChunkCoordIntPair chunkcoordintpair) {
        int j = SectionPosition.sectionToBlockCoord(chunkcoordintpair.x, short0 & 15);
        int k = SectionPosition.sectionToBlockCoord(i, short0 >>> 4 & 15);
        int l = SectionPosition.sectionToBlockCoord(chunkcoordintpair.z, short0 >>> 8 & 15);

        return new BlockPosition(j, k, l);
    }

    @Override
    public void markPosForPostprocessing(BlockPosition blockposition) {
        if (!this.isOutsideBuildHeight(blockposition)) {
            IChunkAccess.getOrCreateOffsetList(this.postProcessing, this.getSectionIndex(blockposition.getY())).add(packOffsetCoordinates(blockposition));
        }

    }

    @Override
    public void addPackedPostProcess(short short0, int i) {
        IChunkAccess.getOrCreateOffsetList(this.postProcessing, i).add(short0);
    }

    public Map<BlockPosition, NBTTagCompound> getBlockEntityNbts() {
        return Collections.unmodifiableMap(this.pendingBlockEntities);
    }

    @Nullable
    @Override
    public NBTTagCompound getBlockEntityNbtForSaving(BlockPosition blockposition) {
        TileEntity tileentity = this.getBlockEntity(blockposition);

        return tileentity != null ? tileentity.saveWithFullMetadata() : (NBTTagCompound) this.pendingBlockEntities.get(blockposition);
    }

    @Override
    public void removeBlockEntity(BlockPosition blockposition) {
        this.blockEntities.remove(blockposition);
        this.pendingBlockEntities.remove(blockposition);
    }

    @Nullable
    public CarvingMask getCarvingMask(WorldGenStage.Features worldgenstage_features) {
        return (CarvingMask) this.carvingMasks.get(worldgenstage_features);
    }

    public CarvingMask getOrCreateCarvingMask(WorldGenStage.Features worldgenstage_features) {
        return (CarvingMask) this.carvingMasks.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
            return new CarvingMask(this.getHeight(), this.getMinBuildHeight());
        });
    }

    public void setCarvingMask(WorldGenStage.Features worldgenstage_features, CarvingMask carvingmask) {
        this.carvingMasks.put(worldgenstage_features, carvingmask);
    }

    public void setLightEngine(LightEngine lightengine) {
        this.lightEngine = lightengine;
    }

    public void setBelowZeroRetrogen(@Nullable BelowZeroRetrogen belowzeroretrogen) {
        this.belowZeroRetrogen = belowzeroretrogen;
    }

    @Nullable
    @Override
    public BelowZeroRetrogen getBelowZeroRetrogen() {
        return this.belowZeroRetrogen;
    }

    private static <T> LevelChunkTicks<T> unpackTicks(ProtoChunkTickList<T> protochunkticklist) {
        return new LevelChunkTicks<>(protochunkticklist.scheduledTicks());
    }

    public LevelChunkTicks<Block> unpackBlockTicks() {
        return unpackTicks(this.blockTicks);
    }

    public LevelChunkTicks<FluidType> unpackFluidTicks() {
        return unpackTicks(this.fluidTicks);
    }

    @Override
    public LevelHeightAccessor getHeightAccessorForGeneration() {
        return (LevelHeightAccessor) (this.isUpgrading() ? BelowZeroRetrogen.UPGRADE_HEIGHT_ACCESSOR : this);
    }
}
