package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk implements IChunkAccess {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkCoordIntPair chunkPos;
    private volatile boolean isDirty;
    @Nullable
    private BiomeStorage biomes;
    @Nullable
    private volatile LightEngine lightEngine;
    private final Map<HeightMap.Type, HeightMap> heightmaps;
    private volatile ChunkStatus status;
    private final Map<BlockPosition, TileEntity> blockEntities;
    private final Map<BlockPosition, NBTTagCompound> blockEntityNbts;
    private final ChunkSection[] sections;
    private final List<NBTTagCompound> entities;
    private final List<BlockPosition> lights;
    private final ShortList[] postProcessing;
    private final Map<StructureGenerator<?>, StructureStart<?>> structureStarts;
    private final Map<StructureGenerator<?>, LongSet> structuresRefences;
    private final ChunkConverter upgradeData;
    private final ProtoChunkTickList<Block> blockTicks;
    private final ProtoChunkTickList<FluidType> liquidTicks;
    private final LevelHeightAccessor levelHeightAccessor;
    private long inhabitedTime;
    private final Map<WorldGenStage.Features, BitSet> carvingMasks;
    private volatile boolean isLightCorrect;

    public ProtoChunk(ChunkCoordIntPair chunkcoordintpair, ChunkConverter chunkconverter, LevelHeightAccessor levelheightaccessor) {
        this(chunkcoordintpair, chunkconverter, (ChunkSection[]) null, new ProtoChunkTickList<>((block) -> {
            return block == null || block.getBlockData().isAir();
        }, chunkcoordintpair, levelheightaccessor), new ProtoChunkTickList<>((fluidtype) -> {
            return fluidtype == null || fluidtype == FluidTypes.EMPTY;
        }, chunkcoordintpair, levelheightaccessor), levelheightaccessor);
    }

    public ProtoChunk(ChunkCoordIntPair chunkcoordintpair, ChunkConverter chunkconverter, @Nullable ChunkSection[] achunksection, ProtoChunkTickList<Block> protochunkticklist, ProtoChunkTickList<FluidType> protochunkticklist1, LevelHeightAccessor levelheightaccessor) {
        this.heightmaps = Maps.newEnumMap(HeightMap.Type.class);
        this.status = ChunkStatus.EMPTY;
        this.blockEntities = Maps.newHashMap();
        this.blockEntityNbts = Maps.newHashMap();
        this.entities = Lists.newArrayList();
        this.lights = Lists.newArrayList();
        this.structureStarts = Maps.newHashMap();
        this.structuresRefences = Maps.newHashMap();
        this.carvingMasks = new Object2ObjectArrayMap();
        this.chunkPos = chunkcoordintpair;
        this.upgradeData = chunkconverter;
        this.blockTicks = protochunkticklist;
        this.liquidTicks = protochunkticklist1;
        this.levelHeightAccessor = levelheightaccessor;
        this.sections = new ChunkSection[levelheightaccessor.getSectionsCount()];
        if (achunksection != null) {
            if (this.sections.length == achunksection.length) {
                System.arraycopy(achunksection, 0, this.sections, 0, this.sections.length);
            } else {
                ProtoChunk.LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", achunksection.length, this.sections.length);
            }
        }

        this.postProcessing = new ShortList[levelheightaccessor.getSectionsCount()];
    }

    @Override
    public IBlockData getType(BlockPosition blockposition) {
        int i = blockposition.getY();

        if (this.d(i)) {
            return Blocks.VOID_AIR.getBlockData();
        } else {
            ChunkSection chunksection = this.getSections()[this.getSectionIndex(i)];

            return ChunkSection.a(chunksection) ? Blocks.AIR.getBlockData() : chunksection.getType(blockposition.getX() & 15, i & 15, blockposition.getZ() & 15);
        }
    }

    @Override
    public Fluid getFluid(BlockPosition blockposition) {
        int i = blockposition.getY();

        if (this.d(i)) {
            return FluidTypes.EMPTY.h();
        } else {
            ChunkSection chunksection = this.getSections()[this.getSectionIndex(i)];

            return ChunkSection.a(chunksection) ? FluidTypes.EMPTY.h() : chunksection.b(blockposition.getX() & 15, i & 15, blockposition.getZ() & 15);
        }
    }

    @Override
    public Stream<BlockPosition> n() {
        return this.lights.stream();
    }

    public ShortList[] x() {
        ShortList[] ashortlist = new ShortList[this.getSectionsCount()];
        Iterator iterator = this.lights.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            IChunkAccess.a(ashortlist, this.getSectionIndex(blockposition.getY())).add(k(blockposition));
        }

        return ashortlist;
    }

    public void b(short short0, int i) {
        this.j(a(short0, this.getSectionYFromSectionIndex(i), this.chunkPos));
    }

    public void j(BlockPosition blockposition) {
        this.lights.add(blockposition.immutableCopy());
    }

    @Nullable
    @Override
    public IBlockData setType(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        if (j >= this.getMinBuildHeight() && j < this.getMaxBuildHeight()) {
            int l = this.getSectionIndex(j);

            if (this.sections[l] == Chunk.EMPTY_SECTION && iblockdata.a(Blocks.AIR)) {
                return iblockdata;
            } else {
                if (iblockdata.f() > 0) {
                    this.lights.add(new BlockPosition((i & 15) + this.getPos().d(), j, (k & 15) + this.getPos().e()));
                }

                ChunkSection chunksection = this.b(l);
                IBlockData iblockdata1 = chunksection.setType(i & 15, j & 15, k & 15, iblockdata);

                if (this.status.b(ChunkStatus.FEATURES) && iblockdata != iblockdata1 && (iblockdata.b((IBlockAccess) this, blockposition) != iblockdata1.b((IBlockAccess) this, blockposition) || iblockdata.f() != iblockdata1.f() || iblockdata.e() || iblockdata1.e())) {
                    this.lightEngine.a(blockposition);
                }

                EnumSet<HeightMap.Type> enumset = this.getChunkStatus().h();
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
                    HeightMap.a(this, enumset1);
                }

                iterator = enumset.iterator();

                while (iterator.hasNext()) {
                    heightmap_type = (HeightMap.Type) iterator.next();
                    ((HeightMap) this.heightmaps.get(heightmap_type)).a(i & 15, j, k & 15, iblockdata);
                }

                return iblockdata1;
            }
        } else {
            return Blocks.VOID_AIR.getBlockData();
        }
    }

    @Override
    public void setTileEntity(TileEntity tileentity) {
        this.blockEntities.put(tileentity.getPosition(), tileentity);
    }

    @Override
    public Set<BlockPosition> c() {
        Set<BlockPosition> set = Sets.newHashSet(this.blockEntityNbts.keySet());

        set.addAll(this.blockEntities.keySet());
        return set;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return (TileEntity) this.blockEntities.get(blockposition);
    }

    public Map<BlockPosition, TileEntity> y() {
        return this.blockEntities;
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.entities.add(nbttagcompound);
    }

    @Override
    public void a(Entity entity) {
        if (!entity.isPassenger()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entity.e(nbttagcompound);
            this.b(nbttagcompound);
        }
    }

    public List<NBTTagCompound> z() {
        return this.entities;
    }

    public void a(BiomeStorage biomestorage) {
        this.biomes = biomestorage;
    }

    @Nullable
    @Override
    public BiomeStorage getBiomeIndex() {
        return this.biomes;
    }

    @Override
    public void setNeedsSaving(boolean flag) {
        this.isDirty = flag;
    }

    @Override
    public boolean isNeedsSaving() {
        return this.isDirty;
    }

    @Override
    public ChunkStatus getChunkStatus() {
        return this.status;
    }

    public void a(ChunkStatus chunkstatus) {
        this.status = chunkstatus;
        this.setNeedsSaving(true);
    }

    @Override
    public ChunkSection[] getSections() {
        return this.sections;
    }

    @Override
    public Collection<Entry<HeightMap.Type, HeightMap>> e() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    @Override
    public HeightMap a(HeightMap.Type heightmap_type) {
        return (HeightMap) this.heightmaps.computeIfAbsent(heightmap_type, (heightmap_type1) -> {
            return new HeightMap(this, heightmap_type1);
        });
    }

    @Override
    public int getHighestBlock(HeightMap.Type heightmap_type, int i, int j) {
        HeightMap heightmap = (HeightMap) this.heightmaps.get(heightmap_type);

        if (heightmap == null) {
            HeightMap.a(this, EnumSet.of(heightmap_type));
            heightmap = (HeightMap) this.heightmaps.get(heightmap_type);
        }

        return heightmap.a(i & 15, j & 15) - 1;
    }

    @Override
    public BlockPosition b(HeightMap.Type heightmap_type) {
        int i = this.getMinBuildHeight();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = this.chunkPos.d(); j <= this.chunkPos.f(); ++j) {
            for (int k = this.chunkPos.e(); k <= this.chunkPos.g(); ++k) {
                int l = this.getHighestBlock(heightmap_type, j & 15, k & 15);

                if (l > i) {
                    i = l;
                    blockposition_mutableblockposition.d(j, l, k);
                }
            }
        }

        return blockposition_mutableblockposition.immutableCopy();
    }

    @Override
    public ChunkCoordIntPair getPos() {
        return this.chunkPos;
    }

    @Nullable
    @Override
    public StructureStart<?> a(StructureGenerator<?> structuregenerator) {
        return (StructureStart) this.structureStarts.get(structuregenerator);
    }

    @Override
    public void a(StructureGenerator<?> structuregenerator, StructureStart<?> structurestart) {
        this.structureStarts.put(structuregenerator, structurestart);
        this.isDirty = true;
    }

    @Override
    public Map<StructureGenerator<?>, StructureStart<?>> g() {
        return Collections.unmodifiableMap(this.structureStarts);
    }

    @Override
    public void a(Map<StructureGenerator<?>, StructureStart<?>> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll(map);
        this.isDirty = true;
    }

    @Override
    public LongSet b(StructureGenerator<?> structuregenerator) {
        return (LongSet) this.structuresRefences.computeIfAbsent(structuregenerator, (structuregenerator1) -> {
            return new LongOpenHashSet();
        });
    }

    @Override
    public void a(StructureGenerator<?> structuregenerator, long i) {
        ((LongSet) this.structuresRefences.computeIfAbsent(structuregenerator, (structuregenerator1) -> {
            return new LongOpenHashSet();
        })).add(i);
        this.isDirty = true;
    }

    @Override
    public Map<StructureGenerator<?>, LongSet> w() {
        return Collections.unmodifiableMap(this.structuresRefences);
    }

    @Override
    public void b(Map<StructureGenerator<?>, LongSet> map) {
        this.structuresRefences.clear();
        this.structuresRefences.putAll(map);
        this.isDirty = true;
    }

    public static short k(BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        int l = i & 15;
        int i1 = j & 15;
        int j1 = k & 15;

        return (short) (l | i1 << 4 | j1 << 8);
    }

    public static BlockPosition a(short short0, int i, ChunkCoordIntPair chunkcoordintpair) {
        int j = SectionPosition.a(chunkcoordintpair.x, short0 & 15);
        int k = SectionPosition.a(i, short0 >>> 4 & 15);
        int l = SectionPosition.a(chunkcoordintpair.z, short0 >>> 8 & 15);

        return new BlockPosition(j, k, l);
    }

    @Override
    public void e(BlockPosition blockposition) {
        if (!this.isOutsideWorld(blockposition)) {
            IChunkAccess.a(this.postProcessing, this.getSectionIndex(blockposition.getY())).add(k(blockposition));
        }

    }

    @Override
    public ShortList[] k() {
        return this.postProcessing;
    }

    @Override
    public void a(short short0, int i) {
        IChunkAccess.a(this.postProcessing, i).add(short0);
    }

    @Override
    public ProtoChunkTickList<Block> o() {
        return this.blockTicks;
    }

    @Override
    public ProtoChunkTickList<FluidType> p() {
        return this.liquidTicks;
    }

    @Override
    public ChunkConverter q() {
        return this.upgradeData;
    }

    @Override
    public void setInhabitedTime(long i) {
        this.inhabitedTime = i;
    }

    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.blockEntityNbts.put(new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z")), nbttagcompound);
    }

    public Map<BlockPosition, NBTTagCompound> A() {
        return Collections.unmodifiableMap(this.blockEntityNbts);
    }

    @Override
    public NBTTagCompound f(BlockPosition blockposition) {
        return (NBTTagCompound) this.blockEntityNbts.get(blockposition);
    }

    @Nullable
    @Override
    public NBTTagCompound g(BlockPosition blockposition) {
        TileEntity tileentity = this.getTileEntity(blockposition);

        return tileentity != null ? tileentity.save(new NBTTagCompound()) : (NBTTagCompound) this.blockEntityNbts.get(blockposition);
    }

    @Override
    public void removeTileEntity(BlockPosition blockposition) {
        this.blockEntities.remove(blockposition);
        this.blockEntityNbts.remove(blockposition);
    }

    @Nullable
    public BitSet a(WorldGenStage.Features worldgenstage_features) {
        return (BitSet) this.carvingMasks.get(worldgenstage_features);
    }

    public BitSet b(WorldGenStage.Features worldgenstage_features) {
        return (BitSet) this.carvingMasks.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
            return new BitSet(65536);
        });
    }

    public void a(WorldGenStage.Features worldgenstage_features, BitSet bitset) {
        this.carvingMasks.put(worldgenstage_features, bitset);
    }

    public void a(LightEngine lightengine) {
        this.lightEngine = lightengine;
    }

    @Override
    public boolean s() {
        return this.isLightCorrect;
    }

    @Override
    public void b(boolean flag) {
        this.isLightCorrect = flag;
        this.setNeedsSaving(true);
    }

    @Override
    public int getMinBuildHeight() {
        return this.levelHeightAccessor.getMinBuildHeight();
    }

    @Override
    public int getHeight() {
        return this.levelHeightAccessor.getHeight();
    }
}
