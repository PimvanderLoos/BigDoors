package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFluids;
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
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.TickContainerAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk extends IChunkAccess {

    static final Logger LOGGER = LogManager.getLogger();
    private static final TickingBlockEntity NULL_TICKER = new TickingBlockEntity() {
        @Override
        public void tick() {}

        @Override
        public boolean isRemoved() {
            return true;
        }

        @Override
        public BlockPosition getPos() {
            return BlockPosition.ZERO;
        }

        @Override
        public String getType() {
            return "<null>";
        }
    };
    private final Map<BlockPosition, Chunk.d> tickersInLevel;
    public boolean loaded;
    private boolean clientLightReady;
    public final World level;
    @Nullable
    private Supplier<PlayerChunk.State> fullStatus;
    @Nullable
    private Chunk.c postLoad;
    private final Int2ObjectMap<GameEventDispatcher> gameEventDispatcherSections;
    private final LevelChunkTicks<Block> blockTicks;
    private final LevelChunkTicks<FluidType> fluidTicks;

    public Chunk(World world, ChunkCoordIntPair chunkcoordintpair) {
        this(world, chunkcoordintpair, ChunkConverter.EMPTY, new LevelChunkTicks<>(), new LevelChunkTicks<>(), 0L, (ChunkSection[]) null, (Chunk.c) null, (BlendingData) null);
    }

    public Chunk(World world, ChunkCoordIntPair chunkcoordintpair, ChunkConverter chunkconverter, LevelChunkTicks<Block> levelchunkticks, LevelChunkTicks<FluidType> levelchunkticks1, long i, @Nullable ChunkSection[] achunksection, @Nullable Chunk.c chunk_c, @Nullable BlendingData blendingdata) {
        super(chunkcoordintpair, chunkconverter, world, world.registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY), i, achunksection, blendingdata);
        this.tickersInLevel = Maps.newHashMap();
        this.clientLightReady = false;
        this.level = world;
        this.gameEventDispatcherSections = new Int2ObjectOpenHashMap();
        HeightMap.Type[] aheightmap_type = HeightMap.Type.values();
        int j = aheightmap_type.length;

        for (int k = 0; k < j; ++k) {
            HeightMap.Type heightmap_type = aheightmap_type[k];

            if (ChunkStatus.FULL.heightmapsAfter().contains(heightmap_type)) {
                this.heightmaps.put(heightmap_type, new HeightMap(this, heightmap_type));
            }
        }

        this.postLoad = chunk_c;
        this.blockTicks = levelchunkticks;
        this.fluidTicks = levelchunkticks1;
    }

    public Chunk(WorldServer worldserver, ProtoChunk protochunk, @Nullable Chunk.c chunk_c) {
        this(worldserver, protochunk.getPos(), protochunk.getUpgradeData(), protochunk.unpackBlockTicks(), protochunk.unpackFluidTicks(), protochunk.getInhabitedTime(), protochunk.getSections(), chunk_c, protochunk.getBlendingData());
        Iterator iterator = protochunk.getBlockEntities().values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            this.setBlockEntity(tileentity);
        }

        this.pendingBlockEntities.putAll(protochunk.getBlockEntityNbts());

        for (int i = 0; i < protochunk.getPostProcessing().length; ++i) {
            this.postProcessing[i] = protochunk.getPostProcessing()[i];
        }

        this.setAllStarts(protochunk.getAllStarts());
        this.setAllReferences(protochunk.getAllReferences());
        iterator = protochunk.getHeightmaps().iterator();

        while (iterator.hasNext()) {
            Entry<HeightMap.Type, HeightMap> entry = (Entry) iterator.next();

            if (ChunkStatus.FULL.heightmapsAfter().contains(entry.getKey())) {
                this.setHeightmap((HeightMap.Type) entry.getKey(), ((HeightMap) entry.getValue()).getRawData());
            }
        }

        this.setLightCorrect(protochunk.isLightCorrect());
        this.unsaved = true;
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
    public GameEventDispatcher getEventDispatcher(int i) {
        return (GameEventDispatcher) this.gameEventDispatcherSections.computeIfAbsent(i, (j) -> {
            return new EuclideanGameEventDispatcher(this.level);
        });
    }

    @Override
    public IBlockData getBlockState(BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        if (this.level.isDebug()) {
            IBlockData iblockdata = null;

            if (j == 60) {
                iblockdata = Blocks.BARRIER.defaultBlockState();
            }

            if (j == 70) {
                iblockdata = ChunkProviderDebug.getBlockStateFor(i, k);
            }

            return iblockdata == null ? Blocks.AIR.defaultBlockState() : iblockdata;
        } else {
            try {
                int l = this.getSectionIndex(j);

                if (l >= 0 && l < this.sections.length) {
                    ChunkSection chunksection = this.sections[l];

                    if (!chunksection.hasOnlyAir()) {
                        return chunksection.getBlockState(i & 15, j & 15, k & 15);
                    }
                }

                return Blocks.AIR.defaultBlockState();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting block state");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Block being got");

                crashreportsystemdetails.setDetail("Location", () -> {
                    return CrashReportSystemDetails.formatLocation(this, i, j, k);
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    @Override
    public Fluid getFluidState(BlockPosition blockposition) {
        return this.getFluidState(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public Fluid getFluidState(int i, int j, int k) {
        try {
            int l = this.getSectionIndex(j);

            if (l >= 0 && l < this.sections.length) {
                ChunkSection chunksection = this.sections[l];

                if (!chunksection.hasOnlyAir()) {
                    return chunksection.getFluidState(i & 15, j & 15, k & 15);
                }
            }

            return FluidTypes.EMPTY.defaultFluidState();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting fluid state");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Block being got");

            crashreportsystemdetails.setDetail("Location", () -> {
                return CrashReportSystemDetails.formatLocation(this, i, j, k);
            });
            throw new ReportedException(crashreport);
        }
    }

    @Nullable
    @Override
    public IBlockData setBlockState(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        int i = blockposition.getY();
        ChunkSection chunksection = this.getSection(this.getSectionIndex(i));
        boolean flag1 = chunksection.hasOnlyAir();

        if (flag1 && iblockdata.isAir()) {
            return null;
        } else {
            int j = blockposition.getX() & 15;
            int k = i & 15;
            int l = blockposition.getZ() & 15;
            IBlockData iblockdata1 = chunksection.setBlockState(j, k, l, iblockdata);

            if (iblockdata1 == iblockdata) {
                return null;
            } else {
                Block block = iblockdata.getBlock();

                ((HeightMap) this.heightmaps.get(HeightMap.Type.MOTION_BLOCKING)).update(j, i, l, iblockdata);
                ((HeightMap) this.heightmaps.get(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES)).update(j, i, l, iblockdata);
                ((HeightMap) this.heightmaps.get(HeightMap.Type.OCEAN_FLOOR)).update(j, i, l, iblockdata);
                ((HeightMap) this.heightmaps.get(HeightMap.Type.WORLD_SURFACE)).update(j, i, l, iblockdata);
                boolean flag2 = chunksection.hasOnlyAir();

                if (flag1 != flag2) {
                    this.level.getChunkSource().getLightEngine().updateSectionStatus(blockposition, flag2);
                }

                boolean flag3 = iblockdata1.hasBlockEntity();

                if (!this.level.isClientSide) {
                    iblockdata1.onRemove(this.level, blockposition, iblockdata, flag);
                } else if (!iblockdata1.is(block) && flag3) {
                    this.removeBlockEntity(blockposition);
                }

                if (!chunksection.getBlockState(j, k, l).is(block)) {
                    return null;
                } else {
                    if (!this.level.isClientSide) {
                        iblockdata.onPlace(this.level, blockposition, iblockdata1, flag);
                    }

                    if (iblockdata.hasBlockEntity()) {
                        TileEntity tileentity = this.getBlockEntity(blockposition, Chunk.EnumTileEntityState.CHECK);

                        if (tileentity == null) {
                            tileentity = ((ITileEntity) block).newBlockEntity(blockposition, iblockdata);
                            if (tileentity != null) {
                                this.addAndRegisterBlockEntity(tileentity);
                            }
                        } else {
                            tileentity.setBlockState(iblockdata);
                            this.updateBlockEntityTicker(tileentity);
                        }
                    }

                    this.unsaved = true;
                    return iblockdata1;
                }
            }
        }
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void addEntity(Entity entity) {}

    @Nullable
    private TileEntity createBlockEntity(BlockPosition blockposition) {
        IBlockData iblockdata = this.getBlockState(blockposition);

        return !iblockdata.hasBlockEntity() ? null : ((ITileEntity) iblockdata.getBlock()).newBlockEntity(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPosition blockposition) {
        return this.getBlockEntity(blockposition, Chunk.EnumTileEntityState.CHECK);
    }

    @Nullable
    public TileEntity getBlockEntity(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        TileEntity tileentity = (TileEntity) this.blockEntities.get(blockposition);

        if (tileentity == null) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) this.pendingBlockEntities.remove(blockposition);

            if (nbttagcompound != null) {
                TileEntity tileentity1 = this.promotePendingBlockEntity(blockposition, nbttagcompound);

                if (tileentity1 != null) {
                    return tileentity1;
                }
            }
        }

        if (tileentity == null) {
            if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.IMMEDIATE) {
                tileentity = this.createBlockEntity(blockposition);
                if (tileentity != null) {
                    this.addAndRegisterBlockEntity(tileentity);
                }
            }
        } else if (tileentity.isRemoved()) {
            this.blockEntities.remove(blockposition);
            return null;
        }

        return tileentity;
    }

    public void addAndRegisterBlockEntity(TileEntity tileentity) {
        this.setBlockEntity(tileentity);
        if (this.isInLevel()) {
            this.addGameEventListener(tileentity);
            this.updateBlockEntityTicker(tileentity);
        }

    }

    private boolean isInLevel() {
        return this.loaded || this.level.isClientSide();
    }

    boolean isTicking(BlockPosition blockposition) {
        if (!this.level.getWorldBorder().isWithinBounds(blockposition)) {
            return false;
        } else {
            World world = this.level;

            if (!(world instanceof WorldServer)) {
                return true;
            } else {
                WorldServer worldserver = (WorldServer) world;

                return this.getFullStatus().isOrAfter(PlayerChunk.State.TICKING) && worldserver.areEntitiesLoaded(ChunkCoordIntPair.asLong(blockposition));
            }
        }
    }

    @Override
    public void setBlockEntity(TileEntity tileentity) {
        BlockPosition blockposition = tileentity.getBlockPos();

        if (this.getBlockState(blockposition).hasBlockEntity()) {
            tileentity.setLevel(this.level);
            tileentity.clearRemoved();
            TileEntity tileentity1 = (TileEntity) this.blockEntities.put(blockposition.immutable(), tileentity);

            if (tileentity1 != null && tileentity1 != tileentity) {
                tileentity1.setRemoved();
            }

        }
    }

    @Nullable
    @Override
    public NBTTagCompound getBlockEntityNbtForSaving(BlockPosition blockposition) {
        TileEntity tileentity = this.getBlockEntity(blockposition);
        NBTTagCompound nbttagcompound;

        if (tileentity != null && !tileentity.isRemoved()) {
            nbttagcompound = tileentity.saveWithFullMetadata();
            nbttagcompound.putBoolean("keepPacked", false);
            return nbttagcompound;
        } else {
            nbttagcompound = (NBTTagCompound) this.pendingBlockEntities.get(blockposition);
            if (nbttagcompound != null) {
                nbttagcompound = nbttagcompound.copy();
                nbttagcompound.putBoolean("keepPacked", true);
            }

            return nbttagcompound;
        }
    }

    @Override
    public void removeBlockEntity(BlockPosition blockposition) {
        if (this.isInLevel()) {
            TileEntity tileentity = (TileEntity) this.blockEntities.remove(blockposition);

            if (tileentity != null) {
                this.removeGameEventListener(tileentity);
                tileentity.setRemoved();
            }
        }

        this.removeBlockEntityTicker(blockposition);
    }

    private <T extends TileEntity> void removeGameEventListener(T t0) {
        if (!this.level.isClientSide) {
            Block block = t0.getBlockState().getBlock();

            if (block instanceof ITileEntity) {
                GameEventListener gameeventlistener = ((ITileEntity) block).getListener(this.level, t0);

                if (gameeventlistener != null) {
                    int i = SectionPosition.blockToSectionCoord(t0.getBlockPos().getY());
                    GameEventDispatcher gameeventdispatcher = this.getEventDispatcher(i);

                    gameeventdispatcher.unregister(gameeventlistener);
                    if (gameeventdispatcher.isEmpty()) {
                        this.gameEventDispatcherSections.remove(i);
                    }
                }
            }

        }
    }

    private void removeBlockEntityTicker(BlockPosition blockposition) {
        Chunk.d chunk_d = (Chunk.d) this.tickersInLevel.remove(blockposition);

        if (chunk_d != null) {
            chunk_d.rebind(Chunk.NULL_TICKER);
        }

    }

    public void runPostLoad() {
        if (this.postLoad != null) {
            this.postLoad.run(this);
            this.postLoad = null;
        }

    }

    public boolean isEmpty() {
        return false;
    }

    public void replaceWithPacketData(PacketDataSerializer packetdataserializer, NBTTagCompound nbttagcompound, Consumer<ClientboundLevelChunkPacketData.b> consumer) {
        this.clearAllBlockEntities();
        ChunkSection[] achunksection = this.sections;
        int i = achunksection.length;

        int j;

        for (j = 0; j < i; ++j) {
            ChunkSection chunksection = achunksection[j];

            chunksection.read(packetdataserializer);
        }

        HeightMap.Type[] aheightmap_type = HeightMap.Type.values();

        i = aheightmap_type.length;

        for (j = 0; j < i; ++j) {
            HeightMap.Type heightmap_type = aheightmap_type[j];
            String s = heightmap_type.getSerializationKey();

            if (nbttagcompound.contains(s, 12)) {
                this.setHeightmap(heightmap_type, nbttagcompound.getLongArray(s));
            }
        }

        consumer.accept((blockposition, tileentitytypes, nbttagcompound1) -> {
            TileEntity tileentity = this.getBlockEntity(blockposition, Chunk.EnumTileEntityState.IMMEDIATE);

            if (tileentity != null && nbttagcompound1 != null && tileentity.getType() == tileentitytypes) {
                tileentity.load(nbttagcompound1);
            }

        });
    }

    public void setLoaded(boolean flag) {
        this.loaded = flag;
    }

    public World getLevel() {
        return this.level;
    }

    public Map<BlockPosition, TileEntity> getBlockEntities() {
        return this.blockEntities;
    }

    @Override
    public Stream<BlockPosition> getLights() {
        return StreamSupport.stream(BlockPosition.betweenClosed(this.chunkPos.getMinBlockX(), this.getMinBuildHeight(), this.chunkPos.getMinBlockZ(), this.chunkPos.getMaxBlockX(), this.getMaxBuildHeight() - 1, this.chunkPos.getMaxBlockZ()).spliterator(), false).filter((blockposition) -> {
            return this.getBlockState(blockposition).getLightEmission() != 0;
        });
    }

    public void postProcessGeneration() {
        ChunkCoordIntPair chunkcoordintpair = this.getPos();

        for (int i = 0; i < this.postProcessing.length; ++i) {
            if (this.postProcessing[i] != null) {
                ShortListIterator shortlistiterator = this.postProcessing[i].iterator();

                while (shortlistiterator.hasNext()) {
                    Short oshort = (Short) shortlistiterator.next();
                    BlockPosition blockposition = ProtoChunk.unpackOffsetCoordinates(oshort, this.getSectionYFromSectionIndex(i), chunkcoordintpair);
                    IBlockData iblockdata = this.getBlockState(blockposition);
                    Fluid fluid = iblockdata.getFluidState();

                    if (!fluid.isEmpty()) {
                        fluid.tick(this.level, blockposition);
                    }

                    if (!(iblockdata.getBlock() instanceof BlockFluids)) {
                        IBlockData iblockdata1 = Block.updateFromNeighbourShapes(iblockdata, this.level, blockposition);

                        this.level.setBlock(blockposition, iblockdata1, 20);
                    }
                }

                this.postProcessing[i].clear();
            }
        }

        UnmodifiableIterator unmodifiableiterator = ImmutableList.copyOf(this.pendingBlockEntities.keySet()).iterator();

        while (unmodifiableiterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) unmodifiableiterator.next();

            this.getBlockEntity(blockposition1);
        }

        this.pendingBlockEntities.clear();
        this.upgradeData.upgrade(this);
    }

    @Nullable
    private TileEntity promotePendingBlockEntity(BlockPosition blockposition, NBTTagCompound nbttagcompound) {
        IBlockData iblockdata = this.getBlockState(blockposition);
        TileEntity tileentity;

        if ("DUMMY".equals(nbttagcompound.getString("id"))) {
            if (iblockdata.hasBlockEntity()) {
                tileentity = ((ITileEntity) iblockdata.getBlock()).newBlockEntity(blockposition, iblockdata);
            } else {
                tileentity = null;
                Chunk.LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", blockposition, iblockdata);
            }
        } else {
            tileentity = TileEntity.loadStatic(blockposition, iblockdata, nbttagcompound);
        }

        if (tileentity != null) {
            tileentity.setLevel(this.level);
            this.addAndRegisterBlockEntity(tileentity);
        } else {
            Chunk.LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", iblockdata, blockposition);
        }

        return tileentity;
    }

    public void unpackTicks(long i) {
        this.blockTicks.unpack(i);
        this.fluidTicks.unpack(i);
    }

    public void registerTickContainerInLevel(WorldServer worldserver) {
        worldserver.getBlockTicks().addContainer(this.chunkPos, this.blockTicks);
        worldserver.getFluidTicks().addContainer(this.chunkPos, this.fluidTicks);
    }

    public void unregisterTickContainerFromLevel(WorldServer worldserver) {
        worldserver.getBlockTicks().removeContainer(this.chunkPos);
        worldserver.getFluidTicks().removeContainer(this.chunkPos);
    }

    @Override
    public ChunkStatus getStatus() {
        return ChunkStatus.FULL;
    }

    public PlayerChunk.State getFullStatus() {
        return this.fullStatus == null ? PlayerChunk.State.BORDER : (PlayerChunk.State) this.fullStatus.get();
    }

    public void setFullStatus(Supplier<PlayerChunk.State> supplier) {
        this.fullStatus = supplier;
    }

    public void clearAllBlockEntities() {
        this.blockEntities.values().forEach(TileEntity::setRemoved);
        this.blockEntities.clear();
        this.tickersInLevel.values().forEach((chunk_d) -> {
            chunk_d.rebind(Chunk.NULL_TICKER);
        });
        this.tickersInLevel.clear();
    }

    public void registerAllBlockEntitiesAfterLevelLoad() {
        this.blockEntities.values().forEach((tileentity) -> {
            this.addGameEventListener(tileentity);
            this.updateBlockEntityTicker(tileentity);
        });
    }

    private <T extends TileEntity> void addGameEventListener(T t0) {
        if (!this.level.isClientSide) {
            Block block = t0.getBlockState().getBlock();

            if (block instanceof ITileEntity) {
                GameEventListener gameeventlistener = ((ITileEntity) block).getListener(this.level, t0);

                if (gameeventlistener != null) {
                    GameEventDispatcher gameeventdispatcher = this.getEventDispatcher(SectionPosition.blockToSectionCoord(t0.getBlockPos().getY()));

                    gameeventdispatcher.register(gameeventlistener);
                }
            }

        }
    }

    private <T extends TileEntity> void updateBlockEntityTicker(T t0) {
        IBlockData iblockdata = t0.getBlockState();
        BlockEntityTicker<T> blockentityticker = iblockdata.getTicker(this.level, t0.getType());

        if (blockentityticker == null) {
            this.removeBlockEntityTicker(t0.getBlockPos());
        } else {
            this.tickersInLevel.compute(t0.getBlockPos(), (blockposition, chunk_d) -> {
                TickingBlockEntity tickingblockentity = this.createTicker(t0, blockentityticker);

                if (chunk_d != null) {
                    chunk_d.rebind(tickingblockentity);
                    return chunk_d;
                } else if (this.isInLevel()) {
                    Chunk.d chunk_d1 = new Chunk.d(tickingblockentity);

                    this.level.addBlockEntityTicker(chunk_d1);
                    return chunk_d1;
                } else {
                    return null;
                }
            });
        }

    }

    private <T extends TileEntity> TickingBlockEntity createTicker(T t0, BlockEntityTicker<T> blockentityticker) {
        return new Chunk.a<>(t0, blockentityticker);
    }

    public boolean isClientLightReady() {
        return this.clientLightReady;
    }

    public void setClientLightReady(boolean flag) {
        this.clientLightReady = flag;
    }

    @FunctionalInterface
    public interface c {

        void run(Chunk chunk);
    }

    public static enum EnumTileEntityState {

        IMMEDIATE, QUEUED, CHECK;

        private EnumTileEntityState() {}
    }

    private class d implements TickingBlockEntity {

        private TickingBlockEntity ticker;

        d(TickingBlockEntity tickingblockentity) {
            this.ticker = tickingblockentity;
        }

        void rebind(TickingBlockEntity tickingblockentity) {
            this.ticker = tickingblockentity;
        }

        @Override
        public void tick() {
            this.ticker.tick();
        }

        @Override
        public boolean isRemoved() {
            return this.ticker.isRemoved();
        }

        @Override
        public BlockPosition getPos() {
            return this.ticker.getPos();
        }

        @Override
        public String getType() {
            return this.ticker.getType();
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
        public void tick() {
            if (!this.blockEntity.isRemoved() && this.blockEntity.hasLevel()) {
                BlockPosition blockposition = this.blockEntity.getBlockPos();

                if (Chunk.this.isTicking(blockposition)) {
                    try {
                        GameProfilerFiller gameprofilerfiller = Chunk.this.level.getProfiler();

                        gameprofilerfiller.push(this::getType);
                        IBlockData iblockdata = Chunk.this.getBlockState(blockposition);

                        if (this.blockEntity.getType().isValid(iblockdata)) {
                            this.ticker.tick(Chunk.this.level, this.blockEntity.getBlockPos(), iblockdata, this.blockEntity);
                            this.loggedInvalidBlockState = false;
                        } else if (!this.loggedInvalidBlockState) {
                            this.loggedInvalidBlockState = true;
                            Chunk.LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", new org.apache.logging.log4j.util.Supplier[]{this::getType, this::getPos, () -> {
                                        return iblockdata;
                                    }});
                        }

                        gameprofilerfiller.pop();
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking block entity");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Block entity being ticked");

                        this.blockEntity.fillCrashReportCategory(crashreportsystemdetails);
                        throw new ReportedException(crashreport);
                    }
                }
            }

        }

        @Override
        public boolean isRemoved() {
            return this.blockEntity.isRemoved();
        }

        @Override
        public BlockPosition getPos() {
            return this.blockEntity.getBlockPos();
        }

        @Override
        public String getType() {
            return TileEntityTypes.getKey(this.blockEntity.getType()).toString();
        }

        public String toString() {
            String s = this.getType();

            return "Level ticker for " + s + "@" + this.getPos();
        }
    }
}
