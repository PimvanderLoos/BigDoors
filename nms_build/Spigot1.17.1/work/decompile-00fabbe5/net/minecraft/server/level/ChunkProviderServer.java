package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
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
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.WorldPersistentData;

public class ChunkProviderServer extends IChunkProvider {

    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.a();
    private final ChunkMapDistance distanceManager;
    public final ChunkGenerator generator;
    final WorldServer level;
    final Thread mainThread;
    final LightEngineThreaded lightEngine;
    private final ChunkProviderServer.a mainThreadProcessor;
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

    public ChunkProviderServer(WorldServer worldserver, Convertable.ConversionSession convertable_conversionsession, DataFixer datafixer, DefinedStructureManager definedstructuremanager, Executor executor, ChunkGenerator chunkgenerator, int i, boolean flag, WorldLoadListener worldloadlistener, ChunkStatusUpdateListener chunkstatusupdatelistener, Supplier<WorldPersistentData> supplier) {
        this.level = worldserver;
        this.mainThreadProcessor = new ChunkProviderServer.a(worldserver);
        this.generator = chunkgenerator;
        this.mainThread = Thread.currentThread();
        File file = convertable_conversionsession.a(worldserver.getDimensionKey());
        File file1 = new File(file, "data");

        file1.mkdirs();
        this.dataStorage = new WorldPersistentData(file1, datafixer);
        this.chunkMap = new PlayerChunkMap(worldserver, convertable_conversionsession, datafixer, definedstructuremanager, executor, this.mainThreadProcessor, this, this.getChunkGenerator(), worldloadlistener, chunkstatusupdatelistener, supplier, i, flag);
        this.lightEngine = this.chunkMap.a();
        this.distanceManager = this.chunkMap.e();
        this.clearCache();
    }

    @Override
    public LightEngineThreaded getLightEngine() {
        return this.lightEngine;
    }

    @Nullable
    private PlayerChunk getChunk(long i) {
        return this.chunkMap.getVisibleChunk(i);
    }

    public int b() {
        return this.chunkMap.c();
    }

    private void a(long i, IChunkAccess ichunkaccess, ChunkStatus chunkstatus) {
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
    public IChunkAccess getChunkAt(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        if (Thread.currentThread() != this.mainThread) {
            return (IChunkAccess) CompletableFuture.supplyAsync(() -> {
                return this.getChunkAt(i, j, chunkstatus, flag);
            }, this.mainThreadProcessor).join();
        } else {
            GameProfilerFiller gameprofilerfiller = this.level.getMethodProfiler();

            gameprofilerfiller.c("getChunk");
            long k = ChunkCoordIntPair.pair(i, j);

            IChunkAccess ichunkaccess;

            for (int l = 0; l < 4; ++l) {
                if (k == this.lastChunkPos[l] && chunkstatus == this.lastChunkStatus[l]) {
                    ichunkaccess = this.lastChunk[l];
                    if (ichunkaccess != null || !flag) {
                        return ichunkaccess;
                    }
                }
            }

            gameprofilerfiller.c("getChunkCacheMiss");
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = this.getChunkFutureMainThread(i, j, chunkstatus, flag);
            ChunkProviderServer.a chunkproviderserver_a = this.mainThreadProcessor;

            Objects.requireNonNull(completablefuture);
            chunkproviderserver_a.awaitTasks(completablefuture::isDone);
            ichunkaccess = (IChunkAccess) ((Either) completablefuture.join()).map((ichunkaccess1) -> {
                return ichunkaccess1;
            }, (playerchunk_failure) -> {
                if (flag) {
                    throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException("Chunk not there when requested: " + playerchunk_failure)));
                } else {
                    return null;
                }
            });
            this.a(k, ichunkaccess, chunkstatus);
            return ichunkaccess;
        }
    }

    @Nullable
    @Override
    public Chunk a(int i, int j) {
        if (Thread.currentThread() != this.mainThread) {
            return null;
        } else {
            this.level.getMethodProfiler().c("getChunkNow");
            long k = ChunkCoordIntPair.pair(i, j);

            for (int l = 0; l < 4; ++l) {
                if (k == this.lastChunkPos[l] && this.lastChunkStatus[l] == ChunkStatus.FULL) {
                    IChunkAccess ichunkaccess = this.lastChunk[l];

                    return ichunkaccess instanceof Chunk ? (Chunk) ichunkaccess : null;
                }
            }

            PlayerChunk playerchunk = this.getChunk(k);

            if (playerchunk == null) {
                return null;
            } else {
                Either<IChunkAccess, PlayerChunk.Failure> either = (Either) playerchunk.b(ChunkStatus.FULL).getNow((Object) null);

                if (either == null) {
                    return null;
                } else {
                    IChunkAccess ichunkaccess1 = (IChunkAccess) either.left().orElse((Object) null);

                    if (ichunkaccess1 != null) {
                        this.a(k, ichunkaccess1, ChunkStatus.FULL);
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

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> b(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        boolean flag1 = Thread.currentThread() == this.mainThread;
        CompletableFuture completablefuture;

        if (flag1) {
            completablefuture = this.getChunkFutureMainThread(i, j, chunkstatus, flag);
            ChunkProviderServer.a chunkproviderserver_a = this.mainThreadProcessor;

            Objects.requireNonNull(completablefuture);
            chunkproviderserver_a.awaitTasks(completablefuture::isDone);
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
        long k = chunkcoordintpair.pair();
        int l = 33 + ChunkStatus.a(chunkstatus);
        PlayerChunk playerchunk = this.getChunk(k);

        if (flag) {
            this.distanceManager.a(TicketType.UNKNOWN, chunkcoordintpair, l, chunkcoordintpair);
            if (this.a(playerchunk, l)) {
                GameProfilerFiller gameprofilerfiller = this.level.getMethodProfiler();

                gameprofilerfiller.enter("chunkLoad");
                this.tickDistanceManager();
                playerchunk = this.getChunk(k);
                gameprofilerfiller.exit();
                if (this.a(playerchunk, l)) {
                    throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException("No chunk holder after ticket has been added")));
                }
            }
        }

        return this.a(playerchunk, l) ? PlayerChunk.UNLOADED_CHUNK_FUTURE : playerchunk.a(chunkstatus, this.chunkMap);
    }

    private boolean a(@Nullable PlayerChunk playerchunk, int i) {
        return playerchunk == null || playerchunk.getTicketLevel() > i;
    }

    @Override
    public boolean isLoaded(int i, int j) {
        PlayerChunk playerchunk = this.getChunk((new ChunkCoordIntPair(i, j)).pair());
        int k = 33 + ChunkStatus.a(ChunkStatus.FULL);

        return !this.a(playerchunk, k);
    }

    @Override
    public IBlockAccess c(int i, int j) {
        long k = ChunkCoordIntPair.pair(i, j);
        PlayerChunk playerchunk = this.getChunk(k);

        if (playerchunk == null) {
            return null;
        } else {
            int l = ChunkProviderServer.CHUNK_STATUSES.size() - 1;

            while (true) {
                ChunkStatus chunkstatus = (ChunkStatus) ChunkProviderServer.CHUNK_STATUSES.get(l);
                Optional<IChunkAccess> optional = ((Either) playerchunk.getStatusFutureUnchecked(chunkstatus).getNow(PlayerChunk.UNLOADED_CHUNK)).left();

                if (optional.isPresent()) {
                    return (IBlockAccess) optional.get();
                }

                if (chunkstatus == ChunkStatus.LIGHT.e()) {
                    return null;
                }

                --l;
            }
        }
    }

    @Override
    public World getWorld() {
        return this.level;
    }

    public boolean runTasks() {
        return this.mainThreadProcessor.executeNext();
    }

    boolean tickDistanceManager() {
        boolean flag = this.distanceManager.a(this.chunkMap);
        boolean flag1 = this.chunkMap.b();

        if (!flag && !flag1) {
            return false;
        } else {
            this.clearCache();
            return true;
        }
    }

    public boolean a(long i) {
        return this.a(i, PlayerChunk::a);
    }

    private boolean a(long i, Function<PlayerChunk, CompletableFuture<Either<Chunk, PlayerChunk.Failure>>> function) {
        PlayerChunk playerchunk = this.getChunk(i);

        if (playerchunk == null) {
            return false;
        } else {
            Either<Chunk, PlayerChunk.Failure> either = (Either) ((CompletableFuture) function.apply(playerchunk)).getNow(PlayerChunk.UNLOADED_LEVEL_CHUNK);

            return either.left().isPresent();
        }
    }

    public void save(boolean flag) {
        this.tickDistanceManager();
        this.chunkMap.save(flag);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.lightEngine.close();
        this.chunkMap.close();
    }

    @Override
    public void tick(BooleanSupplier booleansupplier) {
        this.level.getMethodProfiler().enter("purge");
        this.distanceManager.purgeTickets();
        this.tickDistanceManager();
        this.level.getMethodProfiler().exitEnter("chunks");
        this.tickChunks();
        this.level.getMethodProfiler().exitEnter("unload");
        this.chunkMap.unloadChunks(booleansupplier);
        this.level.getMethodProfiler().exit();
        this.clearCache();
    }

    private void tickChunks() {
        long i = this.level.getTime();
        long j = i - this.lastInhabitedUpdate;

        this.lastInhabitedUpdate = i;
        WorldData worlddata = this.level.getWorldData();
        boolean flag = this.level.isDebugWorld();
        boolean flag1 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);

        if (!flag) {
            this.level.getMethodProfiler().enter("pollingChunks");
            int k = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
            boolean flag2 = worlddata.getTime() % 400L == 0L;

            this.level.getMethodProfiler().enter("naturalSpawnCount");
            int l = this.distanceManager.b();
            SpawnerCreature.d spawnercreature_d = SpawnerCreature.a(l, this.level.C(), this::a);

            this.lastSpawnState = spawnercreature_d;
            this.level.getMethodProfiler().exit();
            List<PlayerChunk> list = Lists.newArrayList(this.chunkMap.f());

            Collections.shuffle(list);
            list.forEach((playerchunk) -> {
                Optional<Chunk> optional = ((Either) playerchunk.a().getNow(PlayerChunk.UNLOADED_LEVEL_CHUNK)).left();

                if (optional.isPresent()) {
                    Chunk chunk = (Chunk) optional.get();
                    ChunkCoordIntPair chunkcoordintpair = chunk.getPos();

                    if (this.level.a(chunkcoordintpair) && !this.chunkMap.isOutsideOfRange(chunkcoordintpair)) {
                        chunk.setInhabitedTime(chunk.getInhabitedTime() + j);
                        if (flag1 && (this.spawnEnemies || this.spawnFriendlies) && this.level.getWorldBorder().isInBounds(chunkcoordintpair)) {
                            SpawnerCreature.a(this.level, chunk, spawnercreature_d, this.spawnFriendlies, this.spawnEnemies, flag2);
                        }

                        this.level.a(chunk, k);
                    }
                }
            });
            this.level.getMethodProfiler().enter("customSpawners");
            if (flag1) {
                this.level.doMobSpawning(this.spawnEnemies, this.spawnFriendlies);
            }

            this.level.getMethodProfiler().exitEnter("broadcast");
            list.forEach((playerchunk) -> {
                Optional optional = ((Either) playerchunk.a().getNow(PlayerChunk.UNLOADED_LEVEL_CHUNK)).left();

                Objects.requireNonNull(playerchunk);
                optional.ifPresent(playerchunk::a);
            });
            this.level.getMethodProfiler().exit();
            this.level.getMethodProfiler().exit();
        }

        this.chunkMap.g();
    }

    private void a(long i, Consumer<Chunk> consumer) {
        PlayerChunk playerchunk = this.getChunk(i);

        if (playerchunk != null) {
            ((Either) playerchunk.c().getNow(PlayerChunk.UNLOADED_LEVEL_CHUNK)).left().ifPresent(consumer);
        }

    }

    @Override
    public String getName() {
        return Integer.toString(this.h());
    }

    @VisibleForTesting
    public int f() {
        return this.mainThreadProcessor.bm();
    }

    public ChunkGenerator getChunkGenerator() {
        return this.generator;
    }

    @Override
    public int h() {
        return this.chunkMap.d();
    }

    public void flagDirty(BlockPosition blockposition) {
        int i = SectionPosition.a(blockposition.getX());
        int j = SectionPosition.a(blockposition.getZ());
        PlayerChunk playerchunk = this.getChunk(ChunkCoordIntPair.pair(i, j));

        if (playerchunk != null) {
            playerchunk.a(blockposition);
        }

    }

    @Override
    public void a(EnumSkyBlock enumskyblock, SectionPosition sectionposition) {
        this.mainThreadProcessor.execute(() -> {
            PlayerChunk playerchunk = this.getChunk(sectionposition.r().pair());

            if (playerchunk != null) {
                playerchunk.a(enumskyblock, sectionposition.b());
            }

        });
    }

    public <T> void addTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.distanceManager.addTicket(tickettype, chunkcoordintpair, i, t0);
    }

    public <T> void removeTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.distanceManager.removeTicket(tickettype, chunkcoordintpair, i, t0);
    }

    @Override
    public void a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        this.distanceManager.a(chunkcoordintpair, flag);
    }

    public void movePlayer(EntityPlayer entityplayer) {
        this.chunkMap.movePlayer(entityplayer);
    }

    public void removeEntity(Entity entity) {
        this.chunkMap.removeEntity(entity);
    }

    public void addEntity(Entity entity) {
        this.chunkMap.addEntity(entity);
    }

    public void broadcastIncludingSelf(Entity entity, Packet<?> packet) {
        this.chunkMap.broadcastIncludingSelf(entity, packet);
    }

    public void broadcast(Entity entity, Packet<?> packet) {
        this.chunkMap.broadcast(entity, packet);
    }

    public void setViewDistance(int i) {
        this.chunkMap.setViewDistance(i);
    }

    @Override
    public void a(boolean flag, boolean flag1) {
        this.spawnEnemies = flag;
        this.spawnFriendlies = flag1;
    }

    public String a(ChunkCoordIntPair chunkcoordintpair) {
        return this.chunkMap.a(chunkcoordintpair);
    }

    public WorldPersistentData getWorldPersistentData() {
        return this.dataStorage;
    }

    public VillagePlace j() {
        return this.chunkMap.h();
    }

    @Nullable
    @VisibleForDebug
    public SpawnerCreature.d k() {
        return this.lastSpawnState;
    }

    private final class a extends IAsyncTaskHandler<Runnable> {

        a(World world) {
            super("Chunk source main thread executor for " + world.getDimensionKey().a());
        }

        @Override
        protected Runnable postToMainThread(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean canExecute(Runnable runnable) {
            return true;
        }

        @Override
        protected boolean isNotMainThread() {
            return true;
        }

        @Override
        protected Thread getThread() {
            return ChunkProviderServer.this.mainThread;
        }

        @Override
        protected void executeTask(Runnable runnable) {
            ChunkProviderServer.this.level.getMethodProfiler().c("runTask");
            super.executeTask(runnable);
        }

        @Override
        protected boolean executeNext() {
            if (ChunkProviderServer.this.tickDistanceManager()) {
                return true;
            } else {
                ChunkProviderServer.this.lightEngine.queueUpdate();
                return super.executeNext();
            }
        }
    }
}
