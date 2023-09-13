package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickListChunk;
import net.minecraft.world.level.TickListEmpty;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ITileEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.EuclideanGameEventDispatcher;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.ChunkProviderDebug;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk implements IChunkAccess {

    static final Logger LOGGER = LogManager.getLogger();
    private static final TickingBlockEntity NULL_TICKER = new TickingBlockEntity() {
        @Override
        public void a() {}

        @Override
        public boolean b() {
            return true;
        }

        @Override
        public BlockPosition c() {
            return BlockPosition.ZERO;
        }

        @Override
        public String d() {
            return "<null>";
        }
    };
    @Nullable
    public static final ChunkSection EMPTY_SECTION = null;
    private final ChunkSection[] sections;
    private BiomeStorage biomes;
    private final Map<BlockPosition, NBTTagCompound> pendingBlockEntities;
    private final Map<BlockPosition, Chunk.c> tickersInLevel;
    public boolean loaded;
    public final World level;
    public final Map<HeightMap.Type, HeightMap> heightmaps;
    private final ChunkConverter upgradeData;
    public final Map<BlockPosition, TileEntity> blockEntities;
    private final Map<StructureGenerator<?>, StructureStart<?>> structureStarts;
    private final Map<StructureGenerator<?>, LongSet> structuresRefences;
    private final ShortList[] postProcessing;
    private TickList<Block> blockTicks;
    private TickList<FluidType> liquidTicks;
    private volatile boolean unsaved;
    private long inhabitedTime;
    @Nullable
    private Supplier<PlayerChunk.State> fullStatus;
    @Nullable
    private Consumer<Chunk> postLoad;
    private final ChunkCoordIntPair chunkPos;
    private volatile boolean isLightCorrect;
    private final Int2ObjectMap<GameEventDispatcher> gameEventDispatcherSections;

    public Chunk(World world, ChunkCoordIntPair chunkcoordintpair, BiomeStorage biomestorage) {
        this(world, chunkcoordintpair, biomestorage, ChunkConverter.EMPTY, TickListEmpty.b(), TickListEmpty.b(), 0L, (ChunkSection[]) null, (Consumer) null);
    }

    public Chunk(World world, ChunkCoordIntPair chunkcoordintpair, BiomeStorage biomestorage, ChunkConverter chunkconverter, TickList<Block> ticklist, TickList<FluidType> ticklist1, long i, @Nullable ChunkSection[] achunksection, @Nullable Consumer<Chunk> consumer) {
        this.pendingBlockEntities = Maps.newHashMap();
        this.tickersInLevel = Maps.newHashMap();
        this.heightmaps = Maps.newEnumMap(HeightMap.Type.class);
        this.blockEntities = Maps.newHashMap();
        this.structureStarts = Maps.newHashMap();
        this.structuresRefences = Maps.newHashMap();
        this.level = world;
        this.chunkPos = chunkcoordintpair;
        this.upgradeData = chunkconverter;
        this.gameEventDispatcherSections = new Int2ObjectOpenHashMap();
        HeightMap.Type[] aheightmap_type = HeightMap.Type.values();
        int j = aheightmap_type.length;

        for (int k = 0; k < j; ++k) {
            HeightMap.Type heightmap_type = aheightmap_type[k];

            if (ChunkStatus.FULL.h().contains(heightmap_type)) {
                this.heightmaps.put(heightmap_type, new HeightMap(this, heightmap_type));
            }
        }

        this.biomes = biomestorage;
        this.blockTicks = ticklist;
        this.liquidTicks = ticklist1;
        this.inhabitedTime = i;
        this.postLoad = consumer;
        this.sections = new ChunkSection[world.getSectionsCount()];
        if (achunksection != null) {
            if (this.sections.length == achunksection.length) {
                System.arraycopy(achunksection, 0, this.sections, 0, this.sections.length);
            } else {
                Chunk.LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", achunksection.length, this.sections.length);
            }
        }

        this.postProcessing = new ShortList[world.getSectionsCount()];
    }

    public Chunk(WorldServer worldserver, ProtoChunk protochunk, @Nullable Consumer<Chunk> consumer) {
        this(worldserver, protochunk.getPos(), protochunk.getBiomeIndex(), protochunk.q(), protochunk.o(), protochunk.p(), protochunk.getInhabitedTime(), protochunk.getSections(), consumer);
        Iterator iterator = protochunk.y().values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            this.setTileEntity(tileentity);
        }

        this.pendingBlockEntities.putAll(protochunk.A());

        for (int i = 0; i < protochunk.k().length; ++i) {
            this.postProcessing[i] = protochunk.k()[i];
        }

        this.a(protochunk.g());
        this.b(protochunk.w());
        iterator = protochunk.e().iterator();

        while (iterator.hasNext()) {
            Entry<HeightMap.Type, HeightMap> entry = (Entry) iterator.next();

            if (ChunkStatus.FULL.h().contains(entry.getKey())) {
                this.a((HeightMap.Type) entry.getKey(), ((HeightMap) entry.getValue()).a());
            }
        }

        this.b(protochunk.s());
        this.unsaved = true;
    }

    @Override
    public GameEventDispatcher a(int i) {
        return (GameEventDispatcher) this.gameEventDispatcherSections.computeIfAbsent(i, (j) -> {
            return new EuclideanGameEventDispatcher(this.level);
        });
    }

    @Override
    public HeightMap a(HeightMap.Type heightmap_type) {
        return (HeightMap) this.heightmaps.computeIfAbsent(heightmap_type, (heightmap_type1) -> {
            return new HeightMap(this, heightmap_type1);
        });
    }

    @Override
    public Set<BlockPosition> c() {
        Set<BlockPosition> set = Sets.newHashSet(this.pendingBlockEntities.keySet());

        set.addAll(this.blockEntities.keySet());
        return set;
    }

    @Override
    public ChunkSection[] getSections() {
        return this.sections;
    }

    @Override
    public IBlockData getType(BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        if (this.level.isDebugWorld()) {
            IBlockData iblockdata = null;

            if (j == 60) {
                iblockdata = Blocks.BARRIER.getBlockData();
            }

            if (j == 70) {
                iblockdata = ChunkProviderDebug.a(i, k);
            }

            return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
        } else {
            try {
                int l = this.getSectionIndex(j);

                if (l >= 0 && l < this.sections.length) {
                    ChunkSection chunksection = this.sections[l];

                    if (!ChunkSection.a(chunksection)) {
                        return chunksection.getType(i & 15, j & 15, k & 15);
                    }
                }

                return Blocks.AIR.getBlockData();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Getting block state");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being got");

                crashreportsystemdetails.a("Location", () -> {
                    return CrashReportSystemDetails.a(this, i, j, k);
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    @Override
    public Fluid getFluid(BlockPosition blockposition) {
        return this.a(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public Fluid a(int i, int j, int k) {
        try {
            int l = this.getSectionIndex(j);

            if (l >= 0 && l < this.sections.length) {
                ChunkSection chunksection = this.sections[l];

                if (!ChunkSection.a(chunksection)) {
                    return chunksection.b(i & 15, j & 15, k & 15);
                }
            }

            return FluidTypes.EMPTY.h();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Getting fluid state");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being got");

            crashreportsystemdetails.a("Location", () -> {
                return CrashReportSystemDetails.a(this, i, j, k);
            });
            throw new ReportedException(crashreport);
        }
    }

    @Nullable
    @Override
    public IBlockData setType(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        int i = blockposition.getY();
        int j = this.getSectionIndex(i);
        ChunkSection chunksection = this.sections[j];

        if (chunksection == Chunk.EMPTY_SECTION) {
            if (iblockdata.isAir()) {
                return null;
            }

            chunksection = new ChunkSection(SectionPosition.a(i));
            this.sections[j] = chunksection;
        }

        boolean flag1 = chunksection.c();
        int k = blockposition.getX() & 15;
        int l = i & 15;
        int i1 = blockposition.getZ() & 15;
        IBlockData iblockdata1 = chunksection.setType(k, l, i1, iblockdata);

        if (iblockdata1 == iblockdata) {
            return null;
        } else {
            Block block = iblockdata.getBlock();

            ((HeightMap) this.heightmaps.get(HeightMap.Type.MOTION_BLOCKING)).a(k, i, i1, iblockdata);
            ((HeightMap) this.heightmaps.get(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES)).a(k, i, i1, iblockdata);
            ((HeightMap) this.heightmaps.get(HeightMap.Type.OCEAN_FLOOR)).a(k, i, i1, iblockdata);
            ((HeightMap) this.heightmaps.get(HeightMap.Type.WORLD_SURFACE)).a(k, i, i1, iblockdata);
            boolean flag2 = chunksection.c();

            if (flag1 != flag2) {
                this.level.getChunkProvider().getLightEngine().a(blockposition, flag2);
            }

            boolean flag3 = iblockdata1.isTileEntity();

            if (!this.level.isClientSide) {
                iblockdata1.remove(this.level, blockposition, iblockdata, flag);
            } else if (!iblockdata1.a(block) && flag3) {
                this.removeTileEntity(blockposition);
            }

            if (!chunksection.getType(k, l, i1).a(block)) {
                return null;
            } else {
                if (!this.level.isClientSide) {
                    iblockdata.onPlace(this.level, blockposition, iblockdata1, flag);
                }

                if (iblockdata.isTileEntity()) {
                    TileEntity tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);

                    if (tileentity == null) {
                        tileentity = ((ITileEntity) block).createTile(blockposition, iblockdata);
                        if (tileentity != null) {
                            this.b(tileentity);
                        }
                    } else {
                        tileentity.b(iblockdata);
                        this.f(tileentity);
                    }
                }

                this.unsaved = true;
                return iblockdata1;
            }
        }
    }

    @Deprecated
    @Override
    public void a(Entity entity) {}

    @Override
    public int getHighestBlock(HeightMap.Type heightmap_type, int i, int j) {
        return ((HeightMap) this.heightmaps.get(heightmap_type)).a(i & 15, j & 15) - 1;
    }

    @Override
    public BlockPosition b(HeightMap.Type heightmap_type) {
        ChunkCoordIntPair chunkcoordintpair = this.getPos();
        int i = this.getMinBuildHeight();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = chunkcoordintpair.d(); j <= chunkcoordintpair.f(); ++j) {
            for (int k = chunkcoordintpair.e(); k <= chunkcoordintpair.g(); ++k) {
                int l = this.getHighestBlock(heightmap_type, j & 15, k & 15);

                if (l > i) {
                    i = l;
                    blockposition_mutableblockposition.d(j, l, k);
                }
            }
        }

        return blockposition_mutableblockposition.immutableCopy();
    }

    @Nullable
    private TileEntity j(BlockPosition blockposition) {
        IBlockData iblockdata = this.getType(blockposition);

        return !iblockdata.isTileEntity() ? null : ((ITileEntity) iblockdata.getBlock()).createTile(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
    }

    @Nullable
    public TileEntity a(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        TileEntity tileentity = (TileEntity) this.blockEntities.get(blockposition);

        if (tileentity == null) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) this.pendingBlockEntities.remove(blockposition);

            if (nbttagcompound != null) {
                TileEntity tileentity1 = this.a(blockposition, nbttagcompound);

                if (tileentity1 != null) {
                    return tileentity1;
                }
            }
        }

        if (tileentity == null) {
            if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.IMMEDIATE) {
                tileentity = this.j(blockposition);
                if (tileentity != null) {
                    this.b(tileentity);
                }
            }
        } else if (tileentity.isRemoved()) {
            this.blockEntities.remove(blockposition);
            return null;
        }

        return tileentity;
    }

    public void b(TileEntity tileentity) {
        this.setTileEntity(tileentity);
        if (this.E()) {
            this.e(tileentity);
            this.f(tileentity);
        }

    }

    private boolean E() {
        return this.loaded || this.level.isClientSide();
    }

    boolean k(BlockPosition blockposition) {
        return !this.level.getWorldBorder().a(blockposition) ? false : (!(this.level instanceof WorldServer) ? true : this.getState().isAtLeast(PlayerChunk.State.TICKING) && ((WorldServer) this.level).b(ChunkCoordIntPair.a(blockposition)));
    }

    @Override
    public void setTileEntity(TileEntity tileentity) {
        BlockPosition blockposition = tileentity.getPosition();

        if (this.getType(blockposition).isTileEntity()) {
            tileentity.setWorld(this.level);
            tileentity.p();
            TileEntity tileentity1 = (TileEntity) this.blockEntities.put(blockposition.immutableCopy(), tileentity);

            if (tileentity1 != null && tileentity1 != tileentity) {
                tileentity1.aa_();
            }

        }
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.pendingBlockEntities.put(new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z")), nbttagcompound);
    }

    @Nullable
    @Override
    public NBTTagCompound g(BlockPosition blockposition) {
        TileEntity tileentity = this.getTileEntity(blockposition);
        NBTTagCompound nbttagcompound;

        if (tileentity != null && !tileentity.isRemoved()) {
            nbttagcompound = tileentity.save(new NBTTagCompound());
            nbttagcompound.setBoolean("keepPacked", false);
            return nbttagcompound;
        } else {
            nbttagcompound = (NBTTagCompound) this.pendingBlockEntities.get(blockposition);
            if (nbttagcompound != null) {
                nbttagcompound = nbttagcompound.clone();
                nbttagcompound.setBoolean("keepPacked", true);
            }

            return nbttagcompound;
        }
    }

    @Override
    public void removeTileEntity(BlockPosition blockposition) {
        if (this.E()) {
            TileEntity tileentity = (TileEntity) this.blockEntities.remove(blockposition);

            if (tileentity != null) {
                this.c(tileentity);
                tileentity.aa_();
            }
        }

        this.l(blockposition);
    }

    private <T extends TileEntity> void c(T t0) {
        if (!this.level.isClientSide) {
            Block block = t0.getBlock().getBlock();

            if (block instanceof ITileEntity) {
                GameEventListener gameeventlistener = ((ITileEntity) block).a(this.level, t0);

                if (gameeventlistener != null) {
                    int i = SectionPosition.a(t0.getPosition().getY());
                    GameEventDispatcher gameeventdispatcher = this.a(i);

                    gameeventdispatcher.b(gameeventlistener);
                    if (gameeventdispatcher.a()) {
                        this.gameEventDispatcherSections.remove(i);
                    }
                }
            }

        }
    }

    private void l(BlockPosition blockposition) {
        Chunk.c chunk_c = (Chunk.c) this.tickersInLevel.remove(blockposition);

        if (chunk_c != null) {
            chunk_c.a(Chunk.NULL_TICKER);
        }

    }

    public void addEntities() {
        if (this.postLoad != null) {
            this.postLoad.accept(this);
            this.postLoad = null;
        }

    }

    public void markDirty() {
        this.unsaved = true;
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public ChunkCoordIntPair getPos() {
        return this.chunkPos;
    }

    public void a(@Nullable BiomeStorage biomestorage, PacketDataSerializer packetdataserializer, NBTTagCompound nbttagcompound, BitSet bitset) {
        boolean flag = biomestorage != null;

        if (flag) {
            this.blockEntities.values().forEach(this::d);
            this.blockEntities.clear();
        } else {
            this.blockEntities.values().removeIf((tileentity) -> {
                int i = this.getSectionIndex(tileentity.getPosition().getY());

                if (bitset.get(i)) {
                    tileentity.aa_();
                    return true;
                } else {
                    return false;
                }
            });
        }

        for (int i = 0; i < this.sections.length; ++i) {
            ChunkSection chunksection = this.sections[i];

            if (!bitset.get(i)) {
                if (flag && chunksection != Chunk.EMPTY_SECTION) {
                    this.sections[i] = Chunk.EMPTY_SECTION;
                }
            } else {
                if (chunksection == Chunk.EMPTY_SECTION) {
                    chunksection = new ChunkSection(this.getSectionYFromSectionIndex(i));
                    this.sections[i] = chunksection;
                }

                chunksection.a(packetdataserializer);
            }
        }

        if (biomestorage != null) {
            this.biomes = biomestorage;
        }

        HeightMap.Type[] aheightmap_type = HeightMap.Type.values();
        int j = aheightmap_type.length;

        for (int k = 0; k < j; ++k) {
            HeightMap.Type heightmap_type = aheightmap_type[k];
            String s = heightmap_type.a();

            if (nbttagcompound.hasKeyOfType(s, 12)) {
                this.a(heightmap_type, nbttagcompound.getLongArray(s));
            }
        }

    }

    private void d(TileEntity tileentity) {
        tileentity.aa_();
        this.tickersInLevel.remove(tileentity.getPosition());
    }

    @Override
    public BiomeStorage getBiomeIndex() {
        return this.biomes;
    }

    public void setLoaded(boolean flag) {
        this.loaded = flag;
    }

    public World getWorld() {
        return this.level;
    }

    @Override
    public Collection<Entry<HeightMap.Type, HeightMap>> e() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    public Map<BlockPosition, TileEntity> getTileEntities() {
        return this.blockEntities;
    }

    @Override
    public NBTTagCompound f(BlockPosition blockposition) {
        return (NBTTagCompound) this.pendingBlockEntities.get(blockposition);
    }

    @Override
    public Stream<BlockPosition> n() {
        return StreamSupport.stream(BlockPosition.b(this.chunkPos.d(), this.getMinBuildHeight(), this.chunkPos.e(), this.chunkPos.f(), this.getMaxBuildHeight() - 1, this.chunkPos.g()).spliterator(), false).filter((blockposition) -> {
            return this.getType(blockposition).f() != 0;
        });
    }

    @Override
    public TickList<Block> o() {
        return this.blockTicks;
    }

    @Override
    public TickList<FluidType> p() {
        return this.liquidTicks;
    }

    @Override
    public void setNeedsSaving(boolean flag) {
        this.unsaved = flag;
    }

    @Override
    public boolean isNeedsSaving() {
        return this.unsaved;
    }

    @Nullable
    @Override
    public StructureStart<?> a(StructureGenerator<?> structuregenerator) {
        return (StructureStart) this.structureStarts.get(structuregenerator);
    }

    @Override
    public void a(StructureGenerator<?> structuregenerator, StructureStart<?> structurestart) {
        this.structureStarts.put(structuregenerator, structurestart);
    }

    @Override
    public Map<StructureGenerator<?>, StructureStart<?>> g() {
        return this.structureStarts;
    }

    @Override
    public void a(Map<StructureGenerator<?>, StructureStart<?>> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll(map);
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
    }

    @Override
    public Map<StructureGenerator<?>, LongSet> w() {
        return this.structuresRefences;
    }

    @Override
    public void b(Map<StructureGenerator<?>, LongSet> map) {
        this.structuresRefences.clear();
        this.structuresRefences.putAll(map);
    }

    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    @Override
    public void setInhabitedTime(long i) {
        this.inhabitedTime = i;
    }

    public void A() {
        ChunkCoordIntPair chunkcoordintpair = this.getPos();

        for (int i = 0; i < this.postProcessing.length; ++i) {
            if (this.postProcessing[i] != null) {
                ShortListIterator shortlistiterator = this.postProcessing[i].iterator();

                while (shortlistiterator.hasNext()) {
                    Short oshort = (Short) shortlistiterator.next();
                    BlockPosition blockposition = ProtoChunk.a(oshort, this.getSectionYFromSectionIndex(i), chunkcoordintpair);
                    IBlockData iblockdata = this.getType(blockposition);
                    IBlockData iblockdata1 = Block.b(iblockdata, (GeneratorAccess) this.level, blockposition);

                    this.level.setTypeAndData(blockposition, iblockdata1, 20);
                }

                this.postProcessing[i].clear();
            }
        }

        this.B();
        UnmodifiableIterator unmodifiableiterator = ImmutableList.copyOf(this.pendingBlockEntities.keySet()).iterator();

        while (unmodifiableiterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) unmodifiableiterator.next();

            this.getTileEntity(blockposition1);
        }

        this.pendingBlockEntities.clear();
        this.upgradeData.a(this);
    }

    @Nullable
    private TileEntity a(BlockPosition blockposition, NBTTagCompound nbttagcompound) {
        IBlockData iblockdata = this.getType(blockposition);
        TileEntity tileentity;

        if ("DUMMY".equals(nbttagcompound.getString("id"))) {
            if (iblockdata.isTileEntity()) {
                tileentity = ((ITileEntity) iblockdata.getBlock()).createTile(blockposition, iblockdata);
            } else {
                tileentity = null;
                Chunk.LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", blockposition, iblockdata);
            }
        } else {
            tileentity = TileEntity.create(blockposition, iblockdata, nbttagcompound);
        }

        if (tileentity != null) {
            tileentity.setWorld(this.level);
            this.b(tileentity);
        } else {
            Chunk.LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", iblockdata, blockposition);
        }

        return tileentity;
    }

    @Override
    public ChunkConverter q() {
        return this.upgradeData;
    }

    @Override
    public ShortList[] k() {
        return this.postProcessing;
    }

    public void B() {
        if (this.blockTicks instanceof ProtoChunkTickList) {
            ((ProtoChunkTickList) this.blockTicks).a(this.level.getBlockTickList(), (blockposition) -> {
                return this.getType(blockposition).getBlock();
            });
            this.blockTicks = TickListEmpty.b();
        } else if (this.blockTicks instanceof TickListChunk) {
            ((TickListChunk) this.blockTicks).a(this.level.getBlockTickList());
            this.blockTicks = TickListEmpty.b();
        }

        if (this.liquidTicks instanceof ProtoChunkTickList) {
            ((ProtoChunkTickList) this.liquidTicks).a(this.level.getFluidTickList(), (blockposition) -> {
                return this.getFluid(blockposition).getType();
            });
            this.liquidTicks = TickListEmpty.b();
        } else if (this.liquidTicks instanceof TickListChunk) {
            ((TickListChunk) this.liquidTicks).a(this.level.getFluidTickList());
            this.liquidTicks = TickListEmpty.b();
        }

    }

    public void a(WorldServer worldserver) {
        RegistryBlocks registryblocks;

        if (this.blockTicks == TickListEmpty.b()) {
            registryblocks = IRegistry.BLOCK;
            Objects.requireNonNull(registryblocks);
            this.blockTicks = new TickListChunk<>(registryblocks::getKey, worldserver.getBlockTickList().a(this.chunkPos, true, false), worldserver.getTime());
            this.setNeedsSaving(true);
        }

        if (this.liquidTicks == TickListEmpty.b()) {
            registryblocks = IRegistry.FLUID;
            Objects.requireNonNull(registryblocks);
            this.liquidTicks = new TickListChunk<>(registryblocks::getKey, worldserver.getFluidTickList().a(this.chunkPos, true, false), worldserver.getTime());
            this.setNeedsSaving(true);
        }

    }

    @Override
    public int getMinBuildHeight() {
        return this.level.getMinBuildHeight();
    }

    @Override
    public int getHeight() {
        return this.level.getHeight();
    }

    @Override
    public ChunkStatus getChunkStatus() {
        return ChunkStatus.FULL;
    }

    public PlayerChunk.State getState() {
        return this.fullStatus == null ? PlayerChunk.State.BORDER : (PlayerChunk.State) this.fullStatus.get();
    }

    public void a(Supplier<PlayerChunk.State> supplier) {
        this.fullStatus = supplier;
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

    public void C() {
        this.blockEntities.values().forEach(this::d);
    }

    public void D() {
        this.blockEntities.values().forEach((tileentity) -> {
            this.e(tileentity);
            this.f(tileentity);
        });
    }

    private <T extends TileEntity> void e(T t0) {
        if (!this.level.isClientSide) {
            Block block = t0.getBlock().getBlock();

            if (block instanceof ITileEntity) {
                GameEventListener gameeventlistener = ((ITileEntity) block).a(this.level, t0);

                if (gameeventlistener != null) {
                    GameEventDispatcher gameeventdispatcher = this.a(SectionPosition.a(t0.getPosition().getY()));

                    gameeventdispatcher.a(gameeventlistener);
                }
            }

        }
    }

    private <T extends TileEntity> void f(T t0) {
        IBlockData iblockdata = t0.getBlock();
        BlockEntityTicker<T> blockentityticker = iblockdata.a(this.level, t0.getTileType());

        if (blockentityticker == null) {
            this.l(t0.getPosition());
        } else {
            this.tickersInLevel.compute(t0.getPosition(), (blockposition, chunk_c) -> {
                TickingBlockEntity tickingblockentity = this.a(t0, blockentityticker);

                if (chunk_c != null) {
                    chunk_c.a(tickingblockentity);
                    return chunk_c;
                } else if (this.E()) {
                    Chunk.c chunk_c1 = new Chunk.c(tickingblockentity);

                    this.level.a((TickingBlockEntity) chunk_c1);
                    return chunk_c1;
                } else {
                    return null;
                }
            });
        }

    }

    private <T extends TileEntity> TickingBlockEntity a(T t0, BlockEntityTicker<T> blockentityticker) {
        return new Chunk.a<>(t0, blockentityticker);
    }

    public static enum EnumTileEntityState {

        IMMEDIATE, QUEUED, CHECK;

        private EnumTileEntityState() {}
    }

    private class c implements TickingBlockEntity {

        private TickingBlockEntity ticker;

        c(TickingBlockEntity tickingblockentity) {
            this.ticker = tickingblockentity;
        }

        void a(TickingBlockEntity tickingblockentity) {
            this.ticker = tickingblockentity;
        }

        @Override
        public void a() {
            this.ticker.a();
        }

        @Override
        public boolean b() {
            return this.ticker.b();
        }

        @Override
        public BlockPosition c() {
            return this.ticker.c();
        }

        @Override
        public String d() {
            return this.ticker.d();
        }

        public String toString() {
            return this.ticker.toString() + " <wrapped>";
        }
    }

    private class a<T extends TileEntity> implements TickingBlockEntity {

        private final T blockEntity;
        private final BlockEntityTicker<T> ticker;
        private boolean loggedInvalidBlockState;

        a(TileEntity tileentity, BlockEntityTicker blockentityticker) {
            this.blockEntity = tileentity;
            this.ticker = blockentityticker;
        }

        @Override
        public void a() {
            if (!this.blockEntity.isRemoved() && this.blockEntity.hasWorld()) {
                BlockPosition blockposition = this.blockEntity.getPosition();

                if (Chunk.this.k(blockposition)) {
                    try {
                        GameProfilerFiller gameprofilerfiller = Chunk.this.level.getMethodProfiler();

                        gameprofilerfiller.a(this::d);
                        IBlockData iblockdata = Chunk.this.getType(blockposition);

                        if (this.blockEntity.getTileType().isValidBlock(iblockdata)) {
                            this.ticker.tick(Chunk.this.level, this.blockEntity.getPosition(), iblockdata, this.blockEntity);
                            this.loggedInvalidBlockState = false;
                        } else if (!this.loggedInvalidBlockState) {
                            this.loggedInvalidBlockState = true;
                            Chunk.LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", new org.apache.logging.log4j.util.Supplier[]{this::d, this::c, () -> {
                                        return iblockdata;
                                    }});
                        }

                        gameprofilerfiller.exit();
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Ticking block entity");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block entity being ticked");

                        this.blockEntity.a(crashreportsystemdetails);
                        throw new ReportedException(crashreport);
                    }
                }
            }

        }

        @Override
        public boolean b() {
            return this.blockEntity.isRemoved();
        }

        @Override
        public BlockPosition c() {
            return this.blockEntity.getPosition();
        }

        @Override
        public String d() {
            return TileEntityTypes.a(this.blockEntity.getTileType()).toString();
        }

        public String toString() {
            String s = this.d();

            return "Level ticker for " + s + "@" + this.c();
        }
    }
}
