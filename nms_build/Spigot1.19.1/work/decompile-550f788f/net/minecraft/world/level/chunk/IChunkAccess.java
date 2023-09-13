package net.minecraft.world.level.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.QuartPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.ticks.SerializableTickContainer;
import net.minecraft.world.ticks.TickContainerAccess;
import org.slf4j.Logger;

public abstract class IChunkAccess implements IBlockAccess, BiomeManager.Provider, StructureAccess {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final LongSet EMPTY_REFERENCE_SET = new LongOpenHashSet();
    protected final ShortList[] postProcessing;
    protected volatile boolean unsaved;
    private volatile boolean isLightCorrect;
    protected final ChunkCoordIntPair chunkPos;
    private long inhabitedTime;
    /** @deprecated */
    @Nullable
    @Deprecated
    private BiomeSettingsGeneration carverBiomeSettings;
    @Nullable
    protected NoiseChunk noiseChunk;
    protected final ChunkConverter upgradeData;
    @Nullable
    protected BlendingData blendingData;
    public final Map<HeightMap.Type, HeightMap> heightmaps = Maps.newEnumMap(HeightMap.Type.class);
    private final Map<Structure, StructureStart> structureStarts = Maps.newHashMap();
    private final Map<Structure, LongSet> structuresRefences = Maps.newHashMap();
    protected final Map<BlockPosition, NBTTagCompound> pendingBlockEntities = Maps.newHashMap();
    public final Map<BlockPosition, TileEntity> blockEntities = Maps.newHashMap();
    protected final LevelHeightAccessor levelHeightAccessor;
    protected final ChunkSection[] sections;

    public IChunkAccess(ChunkCoordIntPair chunkcoordintpair, ChunkConverter chunkconverter, LevelHeightAccessor levelheightaccessor, IRegistry<BiomeBase> iregistry, long i, @Nullable ChunkSection[] achunksection, @Nullable BlendingData blendingdata) {
        this.chunkPos = chunkcoordintpair;
        this.upgradeData = chunkconverter;
        this.levelHeightAccessor = levelheightaccessor;
        this.sections = new ChunkSection[levelheightaccessor.getSectionsCount()];
        this.inhabitedTime = i;
        this.postProcessing = new ShortList[levelheightaccessor.getSectionsCount()];
        this.blendingData = blendingdata;
        if (achunksection != null) {
            if (this.sections.length == achunksection.length) {
                System.arraycopy(achunksection, 0, this.sections, 0, this.sections.length);
            } else {
                IChunkAccess.LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", achunksection.length, this.sections.length);
            }
        }

        replaceMissingSections(levelheightaccessor, iregistry, this.sections);
    }

    private static void replaceMissingSections(LevelHeightAccessor levelheightaccessor, IRegistry<BiomeBase> iregistry, ChunkSection[] achunksection) {
        for (int i = 0; i < achunksection.length; ++i) {
            if (achunksection[i] == null) {
                achunksection[i] = new ChunkSection(levelheightaccessor.getSectionYFromSectionIndex(i), iregistry);
            }
        }

    }

    public GameEventDispatcher getEventDispatcher(int i) {
        return GameEventDispatcher.NOOP;
    }

    @Nullable
    public abstract IBlockData setBlockState(BlockPosition blockposition, IBlockData iblockdata, boolean flag);

    public abstract void setBlockEntity(TileEntity tileentity);

    public abstract void addEntity(Entity entity);

    @Nullable
    public ChunkSection getHighestSection() {
        ChunkSection[] achunksection = this.getSections();

        for (int i = achunksection.length - 1; i >= 0; --i) {
            ChunkSection chunksection = achunksection[i];

            if (!chunksection.hasOnlyAir()) {
                return chunksection;
            }
        }

        return null;
    }

    public int getHighestSectionPosition() {
        ChunkSection chunksection = this.getHighestSection();

        return chunksection == null ? this.getMinBuildHeight() : chunksection.bottomBlockY();
    }

    public Set<BlockPosition> getBlockEntitiesPos() {
        Set<BlockPosition> set = Sets.newHashSet(this.pendingBlockEntities.keySet());

        set.addAll(this.blockEntities.keySet());
        return set;
    }

    public ChunkSection[] getSections() {
        return this.sections;
    }

    public ChunkSection getSection(int i) {
        return this.getSections()[i];
    }

    public Collection<Entry<HeightMap.Type, HeightMap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    public void setHeightmap(HeightMap.Type heightmap_type, long[] along) {
        this.getOrCreateHeightmapUnprimed(heightmap_type).setRawData(this, heightmap_type, along);
    }

    public HeightMap getOrCreateHeightmapUnprimed(HeightMap.Type heightmap_type) {
        return (HeightMap) this.heightmaps.computeIfAbsent(heightmap_type, (heightmap_type1) -> {
            return new HeightMap(this, heightmap_type1);
        });
    }

    public boolean hasPrimedHeightmap(HeightMap.Type heightmap_type) {
        return this.heightmaps.get(heightmap_type) != null;
    }

    public int getHeight(HeightMap.Type heightmap_type, int i, int j) {
        HeightMap heightmap = (HeightMap) this.heightmaps.get(heightmap_type);

        if (heightmap == null) {
            if (SharedConstants.IS_RUNNING_IN_IDE && this instanceof Chunk) {
                IChunkAccess.LOGGER.error("Unprimed heightmap: " + heightmap_type + " " + i + " " + j);
            }

            HeightMap.primeHeightmaps(this, EnumSet.of(heightmap_type));
            heightmap = (HeightMap) this.heightmaps.get(heightmap_type);
        }

        return heightmap.getFirstAvailable(i & 15, j & 15) - 1;
    }

    public ChunkCoordIntPair getPos() {
        return this.chunkPos;
    }

    @Nullable
    @Override
    public StructureStart getStartForStructure(Structure structure) {
        return (StructureStart) this.structureStarts.get(structure);
    }

    @Override
    public void setStartForStructure(Structure structure, StructureStart structurestart) {
        this.structureStarts.put(structure, structurestart);
        this.unsaved = true;
    }

    public Map<Structure, StructureStart> getAllStarts() {
        return Collections.unmodifiableMap(this.structureStarts);
    }

    public void setAllStarts(Map<Structure, StructureStart> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll(map);
        this.unsaved = true;
    }

    @Override
    public LongSet getReferencesForStructure(Structure structure) {
        return (LongSet) this.structuresRefences.getOrDefault(structure, IChunkAccess.EMPTY_REFERENCE_SET);
    }

    @Override
    public void addReferenceForStructure(Structure structure, long i) {
        ((LongSet) this.structuresRefences.computeIfAbsent(structure, (structure1) -> {
            return new LongOpenHashSet();
        })).add(i);
        this.unsaved = true;
    }

    @Override
    public Map<Structure, LongSet> getAllReferences() {
        return Collections.unmodifiableMap(this.structuresRefences);
    }

    @Override
    public void setAllReferences(Map<Structure, LongSet> map) {
        this.structuresRefences.clear();
        this.structuresRefences.putAll(map);
        this.unsaved = true;
    }

    public boolean isYSpaceEmpty(int i, int j) {
        if (i < this.getMinBuildHeight()) {
            i = this.getMinBuildHeight();
        }

        if (j >= this.getMaxBuildHeight()) {
            j = this.getMaxBuildHeight() - 1;
        }

        for (int k = i; k <= j; k += 16) {
            if (!this.getSection(this.getSectionIndex(k)).hasOnlyAir()) {
                return false;
            }
        }

        return true;
    }

    public void setUnsaved(boolean flag) {
        this.unsaved = flag;
    }

    public boolean isUnsaved() {
        return this.unsaved;
    }

    public abstract ChunkStatus getStatus();

    public abstract void removeBlockEntity(BlockPosition blockposition);

    public void markPosForPostprocessing(BlockPosition blockposition) {
        IChunkAccess.LOGGER.warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", blockposition);
    }

    public ShortList[] getPostProcessing() {
        return this.postProcessing;
    }

    public void addPackedPostProcess(short short0, int i) {
        getOrCreateOffsetList(this.getPostProcessing(), i).add(short0);
    }

    public void setBlockEntityNbt(NBTTagCompound nbttagcompound) {
        this.pendingBlockEntities.put(TileEntity.getPosFromTag(nbttagcompound), nbttagcompound);
    }

    @Nullable
    public NBTTagCompound getBlockEntityNbt(BlockPosition blockposition) {
        return (NBTTagCompound) this.pendingBlockEntities.get(blockposition);
    }

    @Nullable
    public abstract NBTTagCompound getBlockEntityNbtForSaving(BlockPosition blockposition);

    public abstract Stream<BlockPosition> getLights();

    public abstract TickContainerAccess<Block> getBlockTicks();

    public abstract TickContainerAccess<FluidType> getFluidTicks();

    public abstract IChunkAccess.a getTicksForSerialization();

    public ChunkConverter getUpgradeData() {
        return this.upgradeData;
    }

    public boolean isOldNoiseGeneration() {
        return this.blendingData != null;
    }

    @Nullable
    public BlendingData getBlendingData() {
        return this.blendingData;
    }

    public void setBlendingData(BlendingData blendingdata) {
        this.blendingData = blendingdata;
    }

    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    public void incrementInhabitedTime(long i) {
        this.inhabitedTime += i;
    }

    public void setInhabitedTime(long i) {
        this.inhabitedTime = i;
    }

    public static ShortList getOrCreateOffsetList(ShortList[] ashortlist, int i) {
        if (ashortlist[i] == null) {
            ashortlist[i] = new ShortArrayList();
        }

        return ashortlist[i];
    }

    public boolean isLightCorrect() {
        return this.isLightCorrect;
    }

    public void setLightCorrect(boolean flag) {
        this.isLightCorrect = flag;
        this.setUnsaved(true);
    }

    @Override
    public int getMinBuildHeight() {
        return this.levelHeightAccessor.getMinBuildHeight();
    }

    @Override
    public int getHeight() {
        return this.levelHeightAccessor.getHeight();
    }

    public NoiseChunk getOrCreateNoiseChunk(Function<IChunkAccess, NoiseChunk> function) {
        if (this.noiseChunk == null) {
            this.noiseChunk = (NoiseChunk) function.apply(this);
        }

        return this.noiseChunk;
    }

    /** @deprecated */
    @Deprecated
    public BiomeSettingsGeneration carverBiome(Supplier<BiomeSettingsGeneration> supplier) {
        if (this.carverBiomeSettings == null) {
            this.carverBiomeSettings = (BiomeSettingsGeneration) supplier.get();
        }

        return this.carverBiomeSettings;
    }

    @Override
    public Holder<BiomeBase> getNoiseBiome(int i, int j, int k) {
        try {
            int l = QuartPos.fromBlock(this.getMinBuildHeight());
            int i1 = l + QuartPos.fromBlock(this.getHeight()) - 1;
            int j1 = MathHelper.clamp(j, l, i1);
            int k1 = this.getSectionIndex(QuartPos.toBlock(j1));

            return this.sections[k1].getNoiseBiome(i & 3, j1 & 3, k & 3);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting biome");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Biome being got");

            crashreportsystemdetails.setDetail("Location", () -> {
                return CrashReportSystemDetails.formatLocation(this, i, j, k);
            });
            throw new ReportedException(crashreport);
        }
    }

    public void fillBiomesFromNoise(BiomeResolver biomeresolver, Climate.Sampler climate_sampler) {
        ChunkCoordIntPair chunkcoordintpair = this.getPos();
        int i = QuartPos.fromBlock(chunkcoordintpair.getMinBlockX());
        int j = QuartPos.fromBlock(chunkcoordintpair.getMinBlockZ());
        LevelHeightAccessor levelheightaccessor = this.getHeightAccessorForGeneration();

        for (int k = levelheightaccessor.getMinSection(); k < levelheightaccessor.getMaxSection(); ++k) {
            ChunkSection chunksection = this.getSection(this.getSectionIndexFromSectionY(k));

            chunksection.fillBiomesFromNoise(biomeresolver, climate_sampler, i, j);
        }

    }

    public boolean hasAnyStructureReferences() {
        return !this.getAllReferences().isEmpty();
    }

    @Nullable
    public BelowZeroRetrogen getBelowZeroRetrogen() {
        return null;
    }

    public boolean isUpgrading() {
        return this.getBelowZeroRetrogen() != null;
    }

    public LevelHeightAccessor getHeightAccessorForGeneration() {
        return this;
    }

    public static record a(SerializableTickContainer<Block> blocks, SerializableTickContainer<FluidType> fluids) {

    }
}
