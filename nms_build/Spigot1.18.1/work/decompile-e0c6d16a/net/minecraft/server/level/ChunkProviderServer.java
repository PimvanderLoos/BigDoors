package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.progress.WorldLoadListener;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.util.thread.IAsyncTaskHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.WorldPersistentData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer extends IChunkProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
    private final ChunkMapDistance distanceManager;
    final WorldServer level;
    final Thread mainThread;
    final LightEngineThreaded lightEngine;
    private final ChunkProviderServer.b mainThreadProcessor;
    public final PlayerChunkMap chunkMap;
    private final WorldPersistentData dataStorage;
    private long lastInhabitedUpdate;
    public boolean spawnEnemies = true;
    public boolean spawnFriendlies = true;
    private static final int CACHE_SIZE = 4;
    private final long[] lastChunkPos = new long[4];
    private final ChunkStatus[] lastChunkStatus = new ChunkStatus[4];
    private final IChunkAccess[] lastChunk = new IChunkAccess[4];
    @Nullable
    @VisibleForDebug
    private SpawnerCreature.d lastSpawnState;

    public ChunkProviderServer(WorldServer worldserver, Convertable.ConversionSession convertable_conversionsession, DataFixer datafixer, DefinedStructureManager definedstructuremanager, Executor executor, ChunkGenerator chunkgenerator, int i, int j, boolean flag, WorldLoadListener worldloadlistener, ChunkStatusUpdateListener chunkstatusupdatelistener, Supplier<WorldPersistentData> supplier) {
        this.level = worldserver;
        this.mainThreadProcessor = new ChunkProviderServer.b(worldserver);
        this.mainThread = Thread.currentThread();
        File file = convertable_conversionsession.getDimensionPath(worldserver.dimension()).resolve("data").toFile();

        file.mkdirs();
        this.dataStorage = new WorldPersistentData(file, datafixer);
        this.chunkMap = new PlayerChunkMap(worldserver, convertable_conversionsession, datafixer, definedstructuremanager, executor, this.mainThreadProcessor, this, chunkgenerator, worldloadlistener, chunkstatusupdatelistener, supplier, i, flag);
        this.lightEngine = this.chunkMap.getLightEngine();
        this.distanceManager = this.chunkMap.getDistanceManager();
        this.distanceManager.updateSimulationDistance(j);
        this.clearCache();
    }

    @Override
    public LightEngineThreaded getLightEngine() {
        return this.lightEngine;
    }

    @Nullable
    private PlayerChunk getVisibleChunkIfPresent(long i) {
        return this.chunkMap.getVisibleChunkIfPresent(i);
    }

    public int getTickingGenerated() {
        return this.chunkMap.getTickingGenerated();
    }

    private void storeInCache(long i, IChunkAccess ichunkaccess, ChunkStatus chunkstatus) {
        for (int j = 3; j > 0; --j) {
            this.lastChunkPos[j] = this.lastChunkPos[j - 1];
            this.lastChunkStatus[j] = this.lastChunkStatus[j - 1];
            this.lastChunk[j] = this.lastChunk[j - 1];
        }

        this.lastChunkPos[0] = i;
        this.lastChunkStatus[0] = chunkstatus;
        this.lastChunk[0] = ichunkaccess;
    }

    @Nullable
    @Override
    public IChunkAccess getChunk(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        if (Thread.currentThread() != this.mainThread) {
            return (IChunkAccess) CompletableFuture.supplyAsync(() -> {
                return this.getChunk(i, j, chunkstatus, flag);
            }, this.mainThreadProcessor).join();
        } else {
            GameProfilerFiller gameprofilerfiller = this.level.getProfiler();

            gameprofilerfiller.incrementCounter("getChunk");
            long k = ChunkCoordIntPair.asLong(i, j);

            IChunkAccess ichunkaccess;

            for (int l = 0; l < 4; ++l) {
                if (k == this.lastChunkPos[l] && chunkstatus == this.lastChunkStatus[l]) {
                    ichunkaccess = this.lastChunk[l];
                    if (ichunkaccess != null || !flag) {
                        return ichunkaccess;
                    }
                }
            }

            gameprofilerfiller.incrementCounter("getChunkCacheMiss");
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = this.getChunkFutureMainThread(i, j, chunkstatus, flag);
            ChunkProviderServer.b chunkproviderserver_b = this.mainThreadProcessor;

            Objects.requireNonNull(completablefuture);
            chunkproviderserver_b.managedBlock(completablefuture::isDone);
            ichunkaccess = (IChunkAccess) ((Either) completablefuture.join()).map((ichunkaccess1) -> {
                return ichunkaccess1;
            }, (playerchunk_failure) -> {
                if (flag) {
                    throw (IllegalStateException) SystemUtils.pauseInIde(new IllegalStateException("Chunk not there when requested: " + playerchunk_failure));
                } else {
                    return null;
                }
            });
            this.storeInCache(k, ichunkaccess, chunkstatus);
            return ichunkaccess;
        }
    }

    @Nullable
    @Override
    public Chunk getChunkNow(int i, int j) {
        if (Thread.currentThread() != this.mainThread) {
            return null;
        } else {
            this.level.getProfiler().incrementCounter("getChunkNow");
            long k = ChunkCoordIntPair.asLong(i, j);

            for (int l = 0; l < 4; ++l) {
                if (k == this.lastChunkPos[l] && this.lastChunkStatus[l] == ChunkStatus.FULL) {
                    IChunkAccess ichunkaccess = this.lastChunk[l];

                    return ichunkaccess instanceof Chunk ? (Chunk) ichunkaccess : null;
                }
            }

            PlayerChunk playerchunk = this.getVisibleChunkIfPresent(k);

            if (playerchunk == null) {
                return null;
            } else {
                Either<IChunkAccess, PlayerChunk.Failure> either = (Either) playerchunk.getFutureIfPresent(ChunkStatus.FULL).getNow((Object) null);

                if (either == null) {
                    return null;
                } else {
                    IChunkAccess ichunkaccess1 = (IChunkAccess) either.left().orElse((Object) null);

                    if (ichunkaccess1 != null) {
                        this.storeInCache(k, ichunkaccess1, ChunkStatus.FULL);
                        if (ichunkaccess1 instanceof Chunk) {
                            return (Chunk) ichunkaccess1;
                        }
                    }

                    return null;
                }
            }
        }
    }

    private void clearCache() {
        Arrays.fill(this.lastChunkPos, ChunkCoordIntPair.INVALID_CHUNK_POS);
        Arrays.fill(this.lastChunkStatus, (Object) null);
        Arrays.fill(this.lastChunk, (Object) null);
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> getChunkFuture(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        boolean flag1 = Thread.currentThread() == this.mainThread;
        CompletableFuture completablefuture;

        if (flag1) {
            completablefuture = this.getChunkFutureMainThread(i, j, chunkstatus, flag);
            ChunkProviderServer.b chunkproviderserver_b = this.mainThreadProcessor;

            Objects.requireNonNull(completablefuture);
            chunkproviderserver_b.managedBlock(completablefuture::isDone);
        } else {
            completablefuture = CompletableFuture.supplyAsync(() -> {
                return this.getChunkFutureMainThread(i, j, chunkstatus, flag);
            }, this.mainThreadProcessor).thenCompose((completablefuture1) -> {
                return completablefuture1;
            });
        }

        return completablefuture;
    }

    private CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> getChunkFutureMainThread(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
        long k = chunkcoordintpair.toLong();
        int l = 33 + ChunkStatus.getDistance(chunkstatus);
        PlayerChunk playerchunk = this.getVisibleChunkIfPresent(k);

        if (flag) {
            this.distanceManager.addTicket(TicketType.UNKNOWN, chunkcoordintpair, l, chunkcoordintpair);
            if (this.chunkAbsent(playerchunk, l)) {
                GameProfilerFiller gameprofilerfiller = this.level.getProfiler();

                gameprofilerfiller.push("chunkLoad");
                this.runDistanceManagerUpdates();
                playerchunk = this.getVisibleChunkIfPresent(k);
                gameprofilerfiller.pop();
                if (this.chunkAbsent(playerchunk, l)) {
                    throw (IllegalStateException) SystemUtils.pauseInIde(new IllegalStateException("No chunk holder after ticket has been added"));
                }
            }
        }

        return this.chunkAbsent(playerchunk, l) ? PlayerChunk.UNLOADED_CHUNK_FUTURE : playerchunk.getOrScheduleFuture(chunkstatus, this.chunkMap);
    }

    private boolean chunkAbsent(@Nullable PlayerChunk playerchunk, int i) {
        return playerchunk == null || playerchunk.getTicketLevel() > i;
    }

    @Override
    public boolean hasChunk(int i, int j) {
        PlayerChunk playerchunk = this.getVisibleChunkIfPresent((new ChunkCoordIntPair(i, j)).toLong());
        int k = 33 + ChunkStatus.getDistance(ChunkStatus.FULL);

        return !this.chunkAbsent(playerchunk, k);
    }

    @Override
    public IBlockAccess getChunkForLighting(int i, int j) {
        long k = ChunkCoordIntPair.asLong(i, j);
        PlayerChunk playerchunk = this.getVisibleChunkIfPresent(k);

        if (playerchunk == null) {
            return null;
        } else {
            int l = ChunkProviderServer.CHUNK_STATUSES.size() - 1;

            while (true) {
                ChunkStatus chunkstatus = (ChunkStatus) ChunkProviderServer.CHUNK_STATUSES.get(l);
                Optional<IChunkAccess> optional = ((Either) playerchunk.getFutureIfPresentUnchecked(chunkstatus).getNow(PlayerChunk.UNLOADED_CHUNK)).left();

                if (optional.isPresent()) {
                    return (IBlockAccess) optional.get();
                }

                if (chunkstatus == ChunkStatus.LIGHT.getParent()) {
                    return null;
                }

                --l;
            }
        }
    }

    @Override
    public World getLevel() {
        return this.level;
    }

    public boolean pollTask() {
        return this.mainThreadProcessor.pollTask();
    }

    boolean runDistanceManagerUpdates() {
        boolean flag = this.distanceManager.runAllUpdates(this.chunkMap);
        boolean flag1 = this.chunkMap.promoteChunkMap();

        if (!flag && !flag1) {
            return false;
        } else {
            this.clearCache();
            return true;
        }
    }

    public boolean isPositionTicking(long i) {
        PlayerChunk playerchunk = this.getVisibleChunkIfPresent(i);

        if (playerchunk == null) {
            return false;
        } else if (!this.level.shouldTickBlocksAt(i)) {
            return false;
        } else {
            Either<Chunk, PlayerChunk.Failure> either = (Either) playerchunk.getTickingChunkFuture().getNow((Object) null);

            return either != null && either.left().isPresent();
        }
    }

    public void save(boolean flag) {
        this.runDistanceManagerUpdates();
        this.chunkMap.saveAllChunks(flag);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.lightEngine.close();
        this.chunkMap.close();
    }

    @Override
    public void tick(BooleanSupplier booleansupplier) {
        this.level.getProfiler().push("purge");
        this.distanceManager.purgeStaleTickets();
        this.runDistanceManagerUpdates();
        this.level.getProfiler().popPush("chunks");
        this.tickChunks();
        this.level.getProfiler().popPush("unload");
        this.chunkMap.tick(booleansupplier);
        this.level.getProfiler().pop();
        this.clearCache();
    }

    private void tickChunks() {
        long i = this.level.getGameTime();
        long j = i - this.lastInhabitedUpdate;

        this.lastInhabitedUpdate = i;
        boolean flag = this.level.isDebug();

        if (flag) {
            this.chunkMap.tick();
        } else {
            WorldData worlddata = this.level.getLevelData();
            GameProfilerFiller gameprofilerfiller = this.level.getProfiler();

            gameprofilerfiller.push("pollingChunks");
            int k = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
            boolean flag1 = worlddata.getGameTime() % 400L == 0L;

            gameprofilerfiller.push("naturalSpawnCount");
            int l = this.distanceManager.getNaturalSpawnChunkCount();
            SpawnerCreature.d spawnercreature_d = SpawnerCreature.createState(l, this.level.getAllEntities(), this::getFullChunk, new LocalMobCapCalculator(this.chunkMap));

            this.lastSpawnState = spawnercreature_d;
            gameprofilerfiller.popPush("filteringLoadedChunks");
            List<ChunkProviderServer.a> list = Lists.newArrayListWithCapacity(l);
            Iterator iterator = this.chunkMap.getChunks().iterator();

            while (iterator.hasNext()) {
                PlayerChunk playerchunk = (PlayerChunk) iterator.next();
                Chunk chunk = playerchunk.getTickingChunk();

                if (chunk != null) {
                    list.add(new ChunkProviderServer.a(chunk, playerchunk));
                }
            }

            gameprofilerfiller.popPush("spawnAndTick");
            boolean flag2 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);

            Collections.shuffle(list);
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                ChunkProviderServer.a chunkproviderserver_a = (ChunkProviderServer.a) iterator1.next();
                Chunk chunk1 = chunkproviderserver_a.chunk;
                ChunkCoordIntPair chunkcoordintpair = chunk1.getPos();

                if (this.level.isPositionEntityTicking(chunkcoordintpair) && this.chunkMap.anyPlayerCloseEnoughForSpawning(chunkcoordintpair)) {
                    chunk1.incrementInhabitedTime(j);
                    if (flag2 && (this.spawnEnemies || this.spawnFriendlies) && this.level.getWorldBorder().isWithinBounds(chunkcoordintpair)) {
                        SpawnerCreature.spawnForChunk(this.level, chunk1, spawnercreature_d, this.spawnFriendlies, this.spawnEnemies, flag1);
                    }

                    if (this.level.shouldTickBlocksAt(chunkcoordintpair.toLong())) {
                        this.level.tickChunk(chunk1, k);
                    }
                }
            }

            gameprofilerfiller.popPush("customSpawners");
            if (flag2) {
                this.level.tickCustomSpawners(this.spawnEnemies, this.spawnFriendlies);
            }

            gameprofilerfiller.popPush("broadcast");
            list.forEach((chunkproviderserver_a1) -> {
                chunkproviderserver_a1.holder.broadcastChanges(chunkproviderserver_a1.chunk);
            });
            gameprofilerfiller.pop();
            gameprofilerfiller.pop();
            this.chunkMap.tick();
        }
    }

    private void getFullChunk(long i, Consumer<Chunk> consumer) {
        PlayerChunk playerchunk = this.getVisibleChunkIfPresent(i);

        if (playerchunk != null) {
            ((Either) playerchunk.getFullChunkFuture().getNow(PlayerChunk.UNLOADED_LEVEL_CHUNK)).left().ifPresent(consumer);
        }

    }

    @Override
    public String gatherStats() {
        return Integer.toString(this.getLoadedChunksCount());
    }

    @VisibleForTesting
    public int getPendingTasksCount() {
        return this.mainThreadProcessor.getPendingTasksCount();
    }

    public ChunkGenerator getGenerator() {
        return this.chunkMap.generator();
    }

    @Override
    public int getLoadedChunksCount() {
        return this.chunkMap.size();
    }

    public void blockChanged(BlockPosition blockposition) {
        int i = SectionPosition.blockToSectionCoord(blockposition.getX());
        int j = SectionPosition.blockToSectionCoord(blockposition.getZ());
        PlayerChunk playerchunk = this.getVisibleChunkIfPresent(ChunkCoordIntPair.asLong(i, j));

        if (playerchunk != null) {
            playerchunk.blockChanged(blockposition);
        }

    }

    @Override
    public void onLightUpdate(EnumSkyBlock enumskyblock, SectionPosition sectionposition) {
        this.mainThreadProcessor.execute(() -> {
            PlayerChunk playerchunk = this.getVisibleChunkIfPresent(sectionposition.chunk().toLong());

            if (playerchunk != null) {
                playerchunk.sectionLightChanged(enumskyblock, sectionposition.y());
            }

        });
    }

    public <T> void addRegionTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.distanceManager.addRegionTicket(tickettype, chunkcoordintpair, i, t0);
    }

    public <T> void removeRegionTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.distanceManager.removeRegionTicket(tickettype, chunkcoordintpair, i, t0);
    }

    @Override
    public void updateChunkForced(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        this.distanceManager.updateChunkForced(chunkcoordintpair, flag);
    }

    public void move(EntityPlayer entityplayer) {
        if (entityplayer.isRemoved()) {
            ChunkProviderServer.LOGGER.info("Skipping update from removed player '{}'", entityplayer);
        } else {
            this.chunkMap.move(entityplayer);
        }

    }

    public void removeEntity(Entity entity) {
        this.chunkMap.removeEntity(entity);
    }

    public void addEntity(Entity entity) {
        this.chunkMap.addEntity(entity);
    }

    public void broadcastAndSend(Entity entity, Packet<?> packet) {
        this.chunkMap.broadcastAndSend(entity, packet);
    }

    public void broadcast(Entity entity, Packet<?> packet) {
        this.chunkMap.broadcast(entity, packet);
    }

    public void setViewDistance(int i) {
        this.chunkMap.setViewDistance(i);
    }

    public void setSimulationDistance(int i) {
        this.distanceManager.updateSimulationDistance(i);
    }

    @Override
    public void setSpawnSettings(boolean flag, boolean flag1) {
        this.spawnEnemies = flag;
        this.spawnFriendlies = flag1;
    }

    public String getChunkDebugData(ChunkCoordIntPair chunkcoordintpair) {
        return this.chunkMap.getChunkDebugData(chunkcoordintpair);
    }

    public WorldPersistentData getDataStorage() {
        return this.dataStorage;
    }

    public VillagePlace getPoiManager() {
        return this.chunkMap.getPoiManager();
    }

    public ChunkScanAccess chunkScanner() {
        return this.chunkMap.chunkScanner();
    }

    @Nullable
    @VisibleForDebug
    public SpawnerCreature.d getLastSpawnState() {
        return this.lastSpawnState;
    }

    private final class b extends IAsyncTaskHandler<Runnable> {

        b(World world) {
            super("Chunk source main thread executor for " + world.dimension().location());
        }

        @Override
        protected Runnable wrapRunnable(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean shouldRun(Runnable runnable) {
            return true;
        }

        @Override
        protected boolean scheduleExecutables() {
            return true;
        }

        @Override
        protected Thread getRunningThread() {
            return ChunkProviderServer.this.mainThread;
        }

        @Override
        protected void doRunTask(Runnable runnable) {
            ChunkProviderServer.this.level.getProfiler().incrementCounter("runTask");
            super.doRunTask(runnable);
        }

        @Override
        protected boolean pollTask() {
            if (ChunkProviderServer.this.runDistanceManagerUpdates()) {
                return true;
            } else {
                ChunkProviderServer.this.lightEngine.tryScheduleUpdate();
                return super.pollTask();
            }
        }
    }

    private static record a(Chunk a, PlayerChunk b) {

        final Chunk chunk;
        final PlayerChunk holder;

        a(Chunk chunk, PlayerChunk playerchunk) {
            this.chunk = chunk;
            this.holder = playerchunk;
        }

        public Chunk chunk() {
            return this.chunk;
        }

        public PlayerChunk holder() {
            return this.holder;
        }
    }
}
