package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.SystemUtils;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.protocol.game.PacketPlayOutAttachEntity;
import net.minecraft.network.protocol.game.PacketPlayOutLightUpdate;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutViewCentre;
import net.minecraft.server.level.progress.WorldLoadListener;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.MathHelper;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.util.thread.IAsyncTaskHandler;
import net.minecraft.util.thread.Mailbox;
import net.minecraft.util.thread.ThreadedMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.boss.EntityComplexPart;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkConverter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.ILightAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunkExtension;
import net.minecraft.world.level.chunk.storage.ChunkRegionLoader;
import net.minecraft.world.level.chunk.storage.IChunkLoader;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.WorldPersistentData;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerChunkMap extends IChunkLoader implements PlayerChunk.e {

    private static final byte CHUNK_TYPE_REPLACEABLE = -1;
    private static final byte CHUNK_TYPE_UNKNOWN = 0;
    private static final byte CHUNK_TYPE_FULL = 1;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CHUNK_SAVED_PER_TICK = 200;
    private static final int MIN_VIEW_DISTANCE = 3;
    public static final int MAX_VIEW_DISTANCE = 33;
    public static final int MAX_CHUNK_DISTANCE = 33 + ChunkStatus.b();
    public static final int FORCED_TICKET_LEVEL = 31;
    public final Long2ObjectLinkedOpenHashMap<PlayerChunk> updatingChunkMap = new Long2ObjectLinkedOpenHashMap();
    public volatile Long2ObjectLinkedOpenHashMap<PlayerChunk> visibleChunkMap;
    private final Long2ObjectLinkedOpenHashMap<PlayerChunk> pendingUnloads;
    private final LongSet entitiesInLevel;
    public final WorldServer level;
    private final LightEngineThreaded lightEngine;
    private final IAsyncTaskHandler<Runnable> mainThreadExecutor;
    public final ChunkGenerator generator;
    private final Supplier<WorldPersistentData> overworldDataStorage;
    private final VillagePlace poiManager;
    public final LongSet toDrop;
    private boolean modified;
    private final ChunkTaskQueueSorter queueSorter;
    private final Mailbox<ChunkTaskQueueSorter.a<Runnable>> worldgenMailbox;
    private final Mailbox<ChunkTaskQueueSorter.a<Runnable>> mainThreadMailbox;
    public final WorldLoadListener progressListener;
    private final ChunkStatusUpdateListener chunkStatusListener;
    public final PlayerChunkMap.a distanceManager;
    private final AtomicInteger tickingGenerated;
    private final DefinedStructureManager structureManager;
    private final String storageName;
    private final PlayerMap playerMap;
    public final Int2ObjectMap<PlayerChunkMap.EntityTracker> entityMap;
    private final Long2ByteMap chunkTypeCache;
    private final Queue<Runnable> unloadQueue;
    int viewDistance;

    public PlayerChunkMap(WorldServer worldserver, Convertable.ConversionSession convertable_conversionsession, DataFixer datafixer, DefinedStructureManager definedstructuremanager, Executor executor, IAsyncTaskHandler<Runnable> iasynctaskhandler, ILightAccess ilightaccess, ChunkGenerator chunkgenerator, WorldLoadListener worldloadlistener, ChunkStatusUpdateListener chunkstatusupdatelistener, Supplier<WorldPersistentData> supplier, int i, boolean flag) {
        super(new File(convertable_conversionsession.a(worldserver.getDimensionKey()), "region"), datafixer, flag);
        this.visibleChunkMap = this.updatingChunkMap.clone();
        this.pendingUnloads = new Long2ObjectLinkedOpenHashMap();
        this.entitiesInLevel = new LongOpenHashSet();
        this.toDrop = new LongOpenHashSet();
        this.tickingGenerated = new AtomicInteger();
        this.playerMap = new PlayerMap();
        this.entityMap = new Int2ObjectOpenHashMap();
        this.chunkTypeCache = new Long2ByteOpenHashMap();
        this.unloadQueue = Queues.newConcurrentLinkedQueue();
        this.structureManager = definedstructuremanager;
        File file = convertable_conversionsession.a(worldserver.getDimensionKey());

        this.storageName = file.getName();
        this.level = worldserver;
        this.generator = chunkgenerator;
        this.mainThreadExecutor = iasynctaskhandler;
        ThreadedMailbox<Runnable> threadedmailbox = ThreadedMailbox.a(executor, "worldgen");

        Objects.requireNonNull(iasynctaskhandler);
        Mailbox<Runnable> mailbox = Mailbox.a("main", iasynctaskhandler::a);

        this.progressListener = worldloadlistener;
        this.chunkStatusListener = chunkstatusupdatelistener;
        ThreadedMailbox<Runnable> threadedmailbox1 = ThreadedMailbox.a(executor, "light");

        this.queueSorter = new ChunkTaskQueueSorter(ImmutableList.of(threadedmailbox, mailbox, threadedmailbox1), executor, Integer.MAX_VALUE);
        this.worldgenMailbox = this.queueSorter.a(threadedmailbox, false);
        this.mainThreadMailbox = this.queueSorter.a(mailbox, false);
        this.lightEngine = new LightEngineThreaded(ilightaccess, this, this.level.getDimensionManager().hasSkyLight(), threadedmailbox1, this.queueSorter.a(threadedmailbox1, false));
        this.distanceManager = new PlayerChunkMap.a(executor, iasynctaskhandler);
        this.overworldDataStorage = supplier;
        this.poiManager = new VillagePlace(new File(file, "poi"), datafixer, flag, worldserver);
        this.setViewDistance(i);
    }

    private static double a(ChunkCoordIntPair chunkcoordintpair, Entity entity) {
        double d0 = (double) SectionPosition.a(chunkcoordintpair.x, 8);
        double d1 = (double) SectionPosition.a(chunkcoordintpair.z, 8);
        double d2 = d0 - entity.locX();
        double d3 = d1 - entity.locZ();

        return d2 * d2 + d3 * d3;
    }

    private static int a(ChunkCoordIntPair chunkcoordintpair, EntityPlayer entityplayer, boolean flag) {
        int i;
        int j;

        if (flag) {
            SectionPosition sectionposition = entityplayer.O();

            i = sectionposition.a();
            j = sectionposition.c();
        } else {
            i = SectionPosition.a(entityplayer.cW());
            j = SectionPosition.a(entityplayer.dc());
        }

        return a(chunkcoordintpair, i, j);
    }

    private static int b(ChunkCoordIntPair chunkcoordintpair, Entity entity) {
        return a(chunkcoordintpair, SectionPosition.a(entity.cW()), SectionPosition.a(entity.dc()));
    }

    private static int a(ChunkCoordIntPair chunkcoordintpair, int i, int j) {
        int k = chunkcoordintpair.x - i;
        int l = chunkcoordintpair.z - j;

        return Math.max(Math.abs(k), Math.abs(l));
    }

    protected LightEngineThreaded a() {
        return this.lightEngine;
    }

    @Nullable
    protected PlayerChunk getUpdatingChunk(long i) {
        return (PlayerChunk) this.updatingChunkMap.get(i);
    }

    @Nullable
    protected PlayerChunk getVisibleChunk(long i) {
        return (PlayerChunk) this.visibleChunkMap.get(i);
    }

    protected IntSupplier c(long i) {
        return () -> {
            PlayerChunk playerchunk = this.getVisibleChunk(i);

            return playerchunk == null ? ChunkTaskQueue.PRIORITY_LEVEL_COUNT - 1 : Math.min(playerchunk.k(), ChunkTaskQueue.PRIORITY_LEVEL_COUNT - 1);
        };
    }

    public String a(ChunkCoordIntPair chunkcoordintpair) {
        PlayerChunk playerchunk = this.getVisibleChunk(chunkcoordintpair.pair());

        if (playerchunk == null) {
            return "null";
        } else {
            String s = playerchunk.getTicketLevel() + "\n";
            ChunkStatus chunkstatus = playerchunk.e();
            IChunkAccess ichunkaccess = playerchunk.f();

            if (chunkstatus != null) {
                s = s + "St: \u00a7" + chunkstatus.c() + chunkstatus + "\u00a7r\n";
            }

            if (ichunkaccess != null) {
                s = s + "Ch: \u00a7" + ichunkaccess.getChunkStatus().c() + ichunkaccess.getChunkStatus() + "\u00a7r\n";
            }

            PlayerChunk.State playerchunk_state = playerchunk.h();

            s = s + "\u00a7" + playerchunk_state.ordinal() + playerchunk_state;
            return s + "\u00a7r";
        }
    }

    private CompletableFuture<Either<List<IChunkAccess>, PlayerChunk.Failure>> a(ChunkCoordIntPair chunkcoordintpair, int i, IntFunction<ChunkStatus> intfunction) {
        List<CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> list = Lists.newArrayList();
        int j = chunkcoordintpair.x;
        int k = chunkcoordintpair.z;

        for (int l = -i; l <= i; ++l) {
            for (int i1 = -i; i1 <= i; ++i1) {
                int j1 = Math.max(Math.abs(i1), Math.abs(l));
                final ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(j + i1, k + l);
                long k1 = chunkcoordintpair1.pair();
                PlayerChunk playerchunk = this.getUpdatingChunk(k1);

                if (playerchunk == null) {
                    return CompletableFuture.completedFuture(Either.right(new PlayerChunk.Failure() {
                        public String toString() {
                            return "Unloaded " + chunkcoordintpair1;
                        }
                    }));
                }

                ChunkStatus chunkstatus = (ChunkStatus) intfunction.apply(j1);
                CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = playerchunk.a(chunkstatus, this);

                list.add(completablefuture);
            }
        }

        CompletableFuture<List<Either<IChunkAccess, PlayerChunk.Failure>>> completablefuture1 = SystemUtils.b((List) list);

        return completablefuture1.thenApply((list1) -> {
            List<IChunkAccess> list2 = Lists.newArrayList();
            final int l1 = 0;

            for (Iterator iterator = list1.iterator(); iterator.hasNext(); ++l1) {
                final Either<IChunkAccess, PlayerChunk.Failure> either = (Either) iterator.next();
                Optional<IChunkAccess> optional = either.left();

                if (!optional.isPresent()) {
                    return Either.right(new PlayerChunk.Failure() {
                        public String toString() {
                            ChunkCoordIntPair chunkcoordintpair2 = new ChunkCoordIntPair(j + l1 % (i * 2 + 1), k + l1 / (i * 2 + 1));

                            return "Unloaded " + chunkcoordintpair2 + " " + either.right().get();
                        }
                    });
                }

                list2.add((IChunkAccess) optional.get());
            }

            return Either.left(list2);
        });
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> b(ChunkCoordIntPair chunkcoordintpair) {
        return this.a(chunkcoordintpair, 2, (i) -> {
            return ChunkStatus.FULL;
        }).thenApplyAsync((either) -> {
            return either.mapLeft((list) -> {
                return (Chunk) list.get(list.size() / 2);
            });
        }, this.mainThreadExecutor);
    }

    @Nullable
    PlayerChunk a(long i, int j, @Nullable PlayerChunk playerchunk, int k) {
        if (k > PlayerChunkMap.MAX_CHUNK_DISTANCE && j > PlayerChunkMap.MAX_CHUNK_DISTANCE) {
            return playerchunk;
        } else {
            if (playerchunk != null) {
                playerchunk.a(j);
            }

            if (playerchunk != null) {
                if (j > PlayerChunkMap.MAX_CHUNK_DISTANCE) {
                    this.toDrop.add(i);
                } else {
                    this.toDrop.remove(i);
                }
            }

            if (j <= PlayerChunkMap.MAX_CHUNK_DISTANCE && playerchunk == null) {
                playerchunk = (PlayerChunk) this.pendingUnloads.remove(i);
                if (playerchunk != null) {
                    playerchunk.a(j);
                } else {
                    playerchunk = new PlayerChunk(new ChunkCoordIntPair(i), j, this.level, this.lightEngine, this.queueSorter, this);
                }

                this.updatingChunkMap.put(i, playerchunk);
                this.modified = true;
            }

            return playerchunk;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.queueSorter.close();
            this.poiManager.close();
        } finally {
            super.close();
        }

    }

    protected void save(boolean flag) {
        if (flag) {
            List<PlayerChunk> list = (List) this.visibleChunkMap.values().stream().filter(PlayerChunk::hasBeenLoaded).peek(PlayerChunk::m).collect(Collectors.toList());
            MutableBoolean mutableboolean = new MutableBoolean();

            do {
                mutableboolean.setFalse();
                list.stream().map((playerchunk) -> {
                    CompletableFuture completablefuture;

                    do {
                        completablefuture = playerchunk.getChunkSave();
                        IAsyncTaskHandler iasynctaskhandler = this.mainThreadExecutor;

                        Objects.requireNonNull(completablefuture);
                        iasynctaskhandler.awaitTasks(completablefuture::isDone);
                    } while (completablefuture != playerchunk.getChunkSave());

                    return (IChunkAccess) completablefuture.join();
                }).filter((ichunkaccess) -> {
                    return ichunkaccess instanceof ProtoChunkExtension || ichunkaccess instanceof Chunk;
                }).filter(this::saveChunk).forEach((ichunkaccess) -> {
                    mutableboolean.setTrue();
                });
            } while (mutableboolean.isTrue());

            this.b(() -> {
                return true;
            });
            this.j();
        } else {
            this.visibleChunkMap.values().stream().filter(PlayerChunk::hasBeenLoaded).forEach((playerchunk) -> {
                IChunkAccess ichunkaccess = (IChunkAccess) playerchunk.getChunkSave().getNow((Object) null);

                if (ichunkaccess instanceof ProtoChunkExtension || ichunkaccess instanceof Chunk) {
                    this.saveChunk(ichunkaccess);
                    playerchunk.m();
                }

            });
        }

    }

    protected void unloadChunks(BooleanSupplier booleansupplier) {
        GameProfilerFiller gameprofilerfiller = this.level.getMethodProfiler();

        gameprofilerfiller.enter("poi");
        this.poiManager.a(booleansupplier);
        gameprofilerfiller.exitEnter("chunk_unload");
        if (!this.level.isSavingDisabled()) {
            this.b(booleansupplier);
        }

        gameprofilerfiller.exit();
    }

    private void b(BooleanSupplier booleansupplier) {
        LongIterator longiterator = this.toDrop.iterator();

        for (int i = 0; longiterator.hasNext() && (booleansupplier.getAsBoolean() || i < 200 || this.toDrop.size() > 2000); longiterator.remove()) {
            long j = longiterator.nextLong();
            PlayerChunk playerchunk = (PlayerChunk) this.updatingChunkMap.remove(j);

            if (playerchunk != null) {
                this.pendingUnloads.put(j, playerchunk);
                this.modified = true;
                ++i;
                this.a(j, playerchunk);
            }
        }

        Runnable runnable;

        while ((booleansupplier.getAsBoolean() || this.unloadQueue.size() > 2000) && (runnable = (Runnable) this.unloadQueue.poll()) != null) {
            runnable.run();
        }

    }

    private void a(long i, PlayerChunk playerchunk) {
        CompletableFuture<IChunkAccess> completablefuture = playerchunk.getChunkSave();
        Consumer consumer = (ichunkaccess) -> {
            CompletableFuture<IChunkAccess> completablefuture1 = playerchunk.getChunkSave();

            if (completablefuture1 != completablefuture) {
                this.a(i, playerchunk);
            } else {
                if (this.pendingUnloads.remove(i, playerchunk) && ichunkaccess != null) {
                    if (ichunkaccess instanceof Chunk) {
                        ((Chunk) ichunkaccess).setLoaded(false);
                    }

                    this.saveChunk(ichunkaccess);
                    if (this.entitiesInLevel.remove(i) && ichunkaccess instanceof Chunk) {
                        Chunk chunk = (Chunk) ichunkaccess;

                        this.level.unloadChunk(chunk);
                    }

                    this.lightEngine.a(ichunkaccess.getPos());
                    this.lightEngine.queueUpdate();
                    this.progressListener.a(ichunkaccess.getPos(), (ChunkStatus) null);
                }

            }
        };
        Queue queue = this.unloadQueue;

        Objects.requireNonNull(this.unloadQueue);
        completablefuture.thenAcceptAsync(consumer, queue::add).whenComplete((ovoid, throwable) -> {
            if (throwable != null) {
                PlayerChunkMap.LOGGER.error("Failed to save chunk {}", playerchunk.i(), throwable);
            }

        });
    }

    protected boolean b() {
        if (!this.modified) {
            return false;
        } else {
            this.visibleChunkMap = this.updatingChunkMap.clone();
            this.modified = false;
            return true;
        }
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> a(PlayerChunk playerchunk, ChunkStatus chunkstatus) {
        ChunkCoordIntPair chunkcoordintpair = playerchunk.i();

        if (chunkstatus == ChunkStatus.EMPTY) {
            return this.f(chunkcoordintpair);
        } else {
            if (chunkstatus == ChunkStatus.LIGHT) {
                this.distanceManager.a(TicketType.LIGHT, chunkcoordintpair, 33 + ChunkStatus.a(ChunkStatus.LIGHT), chunkcoordintpair);
            }

            Optional<IChunkAccess> optional = ((Either) playerchunk.a(chunkstatus.e(), this).getNow(PlayerChunk.UNLOADED_CHUNK)).left();

            if (optional.isPresent() && ((IChunkAccess) optional.get()).getChunkStatus().b(chunkstatus)) {
                CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = chunkstatus.a(this.level, this.structureManager, this.lightEngine, (ichunkaccess) -> {
                    return this.c(playerchunk);
                }, (IChunkAccess) optional.get());

                this.progressListener.a(chunkcoordintpair, chunkstatus);
                return completablefuture;
            } else {
                return this.b(playerchunk, chunkstatus);
            }
        }
    }

    private CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> f(ChunkCoordIntPair chunkcoordintpair) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.level.getMethodProfiler().c("chunkLoad");
                NBTTagCompound nbttagcompound = this.readChunkData(chunkcoordintpair);

                if (nbttagcompound != null) {
                    boolean flag = nbttagcompound.hasKeyOfType("Level", 10) && nbttagcompound.getCompound("Level").hasKeyOfType("Status", 8);

                    if (flag) {
                        ProtoChunk protochunk = ChunkRegionLoader.loadChunk(this.level, this.structureManager, this.poiManager, chunkcoordintpair, nbttagcompound);

                        this.a(chunkcoordintpair, protochunk.getChunkStatus().getType());
                        return Either.left(protochunk);
                    }

                    PlayerChunkMap.LOGGER.error("Chunk file at {} is missing level data, skipping", chunkcoordintpair);
                }
            } catch (ReportedException reportedexception) {
                Throwable throwable = reportedexception.getCause();

                if (!(throwable instanceof IOException)) {
                    this.g(chunkcoordintpair);
                    throw reportedexception;
                }

                PlayerChunkMap.LOGGER.error("Couldn't load chunk {}", chunkcoordintpair, throwable);
            } catch (Exception exception) {
                PlayerChunkMap.LOGGER.error("Couldn't load chunk {}", chunkcoordintpair, exception);
            }

            this.g(chunkcoordintpair);
            return Either.left(new ProtoChunk(chunkcoordintpair, ChunkConverter.EMPTY, this.level));
        }, this.mainThreadExecutor);
    }

    private void g(ChunkCoordIntPair chunkcoordintpair) {
        this.chunkTypeCache.put(chunkcoordintpair.pair(), (byte) -1);
    }

    private byte a(ChunkCoordIntPair chunkcoordintpair, ChunkStatus.Type chunkstatus_type) {
        return this.chunkTypeCache.put(chunkcoordintpair.pair(), (byte) (chunkstatus_type == ChunkStatus.Type.PROTOCHUNK ? -1 : 1));
    }

    private CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> b(PlayerChunk playerchunk, ChunkStatus chunkstatus) {
        ChunkCoordIntPair chunkcoordintpair = playerchunk.i();
        CompletableFuture<Either<List<IChunkAccess>, PlayerChunk.Failure>> completablefuture = this.a(chunkcoordintpair, chunkstatus.f(), (i) -> {
            return this.a(chunkstatus, i);
        });

        this.level.getMethodProfiler().c(() -> {
            return "chunkGenerate " + chunkstatus.d();
        });
        Executor executor = (runnable) -> {
            this.worldgenMailbox.a(ChunkTaskQueueSorter.a(playerchunk, runnable));
        };

        return completablefuture.thenComposeAsync((either) -> {
            return (CompletionStage) either.map((list) -> {
                try {
                    CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture1 = chunkstatus.a(executor, this.level, this.generator, this.structureManager, this.lightEngine, (ichunkaccess) -> {
                        return this.c(playerchunk);
                    }, list);

                    this.progressListener.a(chunkcoordintpair, chunkstatus);
                    return completablefuture1;
                } catch (Exception exception) {
                    exception.getStackTrace();
                    CrashReport crashreport = CrashReport.a(exception, "Exception generating new chunk");
                    CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");

                    crashreportsystemdetails.a("Location", (Object) String.format("%d,%d", chunkcoordintpair.x, chunkcoordintpair.z));
                    crashreportsystemdetails.a("Position hash", (Object) ChunkCoordIntPair.pair(chunkcoordintpair.x, chunkcoordintpair.z));
                    crashreportsystemdetails.a("Generator", (Object) this.generator);
                    throw new ReportedException(crashreport);
                }
            }, (playerchunk_failure) -> {
                this.c(chunkcoordintpair);
                return CompletableFuture.completedFuture(Either.right(playerchunk_failure));
            });
        }, executor);
    }

    protected void c(ChunkCoordIntPair chunkcoordintpair) {
        this.mainThreadExecutor.a(SystemUtils.a(() -> {
            this.distanceManager.b(TicketType.LIGHT, chunkcoordintpair, 33 + ChunkStatus.a(ChunkStatus.LIGHT), chunkcoordintpair);
        }, () -> {
            return "release light ticket " + chunkcoordintpair;
        }));
    }

    private ChunkStatus a(ChunkStatus chunkstatus, int i) {
        ChunkStatus chunkstatus1;

        if (i == 0) {
            chunkstatus1 = chunkstatus.e();
        } else {
            chunkstatus1 = ChunkStatus.a(ChunkStatus.a(chunkstatus) + i);
        }

        return chunkstatus1;
    }

    private static void a(WorldServer worldserver, List<NBTTagCompound> list) {
        if (!list.isEmpty()) {
            worldserver.b(EntityTypes.a(list, (World) worldserver));
        }

    }

    private CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> c(PlayerChunk playerchunk) {
        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = playerchunk.getStatusFutureUnchecked(ChunkStatus.FULL.e());

        return completablefuture.thenApplyAsync((either) -> {
            ChunkStatus chunkstatus = PlayerChunk.getChunkStatus(playerchunk.getTicketLevel());

            return !chunkstatus.b(ChunkStatus.FULL) ? PlayerChunk.UNLOADED_CHUNK : either.mapLeft((ichunkaccess) -> {
                ChunkCoordIntPair chunkcoordintpair = playerchunk.i();
                ProtoChunk protochunk = (ProtoChunk) ichunkaccess;
                Chunk chunk;

                if (protochunk instanceof ProtoChunkExtension) {
                    chunk = ((ProtoChunkExtension) protochunk).v();
                } else {
                    chunk = new Chunk(this.level, protochunk, (chunk1) -> {
                        a(this.level, protochunk.z());
                    });
                    playerchunk.a(new ProtoChunkExtension(chunk));
                }

                chunk.a(() -> {
                    return PlayerChunk.getChunkState(playerchunk.getTicketLevel());
                });
                chunk.addEntities();
                if (this.entitiesInLevel.add(chunkcoordintpair.pair())) {
                    chunk.setLoaded(true);
                    chunk.D();
                }

                return chunk;
            });
        }, (runnable) -> {
            Mailbox mailbox = this.mainThreadMailbox;
            long i = playerchunk.i().pair();

            Objects.requireNonNull(playerchunk);
            mailbox.a(ChunkTaskQueueSorter.a(runnable, i, playerchunk::getTicketLevel));
        });
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> a(PlayerChunk playerchunk) {
        ChunkCoordIntPair chunkcoordintpair = playerchunk.i();
        CompletableFuture<Either<List<IChunkAccess>, PlayerChunk.Failure>> completablefuture = this.a(chunkcoordintpair, 1, (i) -> {
            return ChunkStatus.FULL;
        });
        CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture1 = completablefuture.thenApplyAsync((either) -> {
            return either.flatMap((list) -> {
                Chunk chunk = (Chunk) list.get(list.size() / 2);

                chunk.A();
                return Either.left(chunk);
            });
        }, (runnable) -> {
            this.mainThreadMailbox.a(ChunkTaskQueueSorter.a(playerchunk, runnable));
        });

        completablefuture1.thenAcceptAsync((either) -> {
            either.ifLeft((chunk) -> {
                this.tickingGenerated.getAndIncrement();
                Packet<?>[] apacket = new Packet[2];

                this.a(chunkcoordintpair, false).forEach((entityplayer) -> {
                    this.a(entityplayer, apacket, chunk);
                });
            });
        }, (runnable) -> {
            this.mainThreadMailbox.a(ChunkTaskQueueSorter.a(playerchunk, runnable));
        });
        return completablefuture1;
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> b(PlayerChunk playerchunk) {
        return this.a(playerchunk.i(), 1, ChunkStatus::a).thenApplyAsync((either) -> {
            return either.mapLeft((list) -> {
                Chunk chunk = (Chunk) list.get(list.size() / 2);

                chunk.B();
                return chunk;
            });
        }, (runnable) -> {
            this.mainThreadMailbox.a(ChunkTaskQueueSorter.a(playerchunk, runnable));
        });
    }

    public int c() {
        return this.tickingGenerated.get();
    }

    public boolean saveChunk(IChunkAccess ichunkaccess) {
        this.poiManager.a(ichunkaccess.getPos());
        if (!ichunkaccess.isNeedsSaving()) {
            return false;
        } else {
            ichunkaccess.setNeedsSaving(false);
            ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

            try {
                ChunkStatus chunkstatus = ichunkaccess.getChunkStatus();

                if (chunkstatus.getType() != ChunkStatus.Type.LEVELCHUNK) {
                    if (this.h(chunkcoordintpair)) {
                        return false;
                    }

                    if (chunkstatus == ChunkStatus.EMPTY && ichunkaccess.g().values().stream().noneMatch(StructureStart::e)) {
                        return false;
                    }
                }

                this.level.getMethodProfiler().c("chunkSave");
                NBTTagCompound nbttagcompound = ChunkRegionLoader.saveChunk(this.level, ichunkaccess);

                this.a(chunkcoordintpair, nbttagcompound);
                this.a(chunkcoordintpair, chunkstatus.getType());
                return true;
            } catch (Exception exception) {
                PlayerChunkMap.LOGGER.error("Failed to save chunk {},{}", chunkcoordintpair.x, chunkcoordintpair.z, exception);
                return false;
            }
        }
    }

    private boolean h(ChunkCoordIntPair chunkcoordintpair) {
        byte b0 = this.chunkTypeCache.get(chunkcoordintpair.pair());

        if (b0 != 0) {
            return b0 == 1;
        } else {
            NBTTagCompound nbttagcompound;

            try {
                nbttagcompound = this.readChunkData(chunkcoordintpair);
                if (nbttagcompound == null) {
                    this.g(chunkcoordintpair);
                    return false;
                }
            } catch (Exception exception) {
                PlayerChunkMap.LOGGER.error("Failed to read chunk {}", chunkcoordintpair, exception);
                this.g(chunkcoordintpair);
                return false;
            }

            ChunkStatus.Type chunkstatus_type = ChunkRegionLoader.a(nbttagcompound);

            return this.a(chunkcoordintpair, chunkstatus_type) == 1;
        }
    }

    protected void setViewDistance(int i) {
        int j = MathHelper.clamp(i + 1, 3, 33);

        if (j != this.viewDistance) {
            int k = this.viewDistance;

            this.viewDistance = j;
            this.distanceManager.a(this.viewDistance);
            ObjectIterator objectiterator = this.updatingChunkMap.values().iterator();

            while (objectiterator.hasNext()) {
                PlayerChunk playerchunk = (PlayerChunk) objectiterator.next();
                ChunkCoordIntPair chunkcoordintpair = playerchunk.i();
                Packet<?>[] apacket = new Packet[2];

                this.a(chunkcoordintpair, false).forEach((entityplayer) -> {
                    int l = a(chunkcoordintpair, entityplayer, true);
                    boolean flag = l <= k;
                    boolean flag1 = l <= this.viewDistance;

                    this.sendChunk(entityplayer, chunkcoordintpair, apacket, flag, flag1);
                });
            }
        }

    }

    protected void sendChunk(EntityPlayer entityplayer, ChunkCoordIntPair chunkcoordintpair, Packet<?>[] apacket, boolean flag, boolean flag1) {
        if (entityplayer.level == this.level) {
            if (flag1 && !flag) {
                PlayerChunk playerchunk = this.getVisibleChunk(chunkcoordintpair.pair());

                if (playerchunk != null) {
                    Chunk chunk = playerchunk.getChunk();

                    if (chunk != null) {
                        this.a(entityplayer, apacket, chunk);
                    }

                    PacketDebug.a(this.level, chunkcoordintpair);
                }
            }

            if (!flag1 && flag) {
                entityplayer.a(chunkcoordintpair);
            }

        }
    }

    public int d() {
        return this.visibleChunkMap.size();
    }

    protected ChunkMapDistance e() {
        return this.distanceManager;
    }

    protected Iterable<PlayerChunk> f() {
        return Iterables.unmodifiableIterable(this.visibleChunkMap.values());
    }

    void a(Writer writer) throws IOException {
        CSVWriter csvwriter = CSVWriter.a().a("x").a("z").a("level").a("in_memory").a("status").a("full_status").a("accessible_ready").a("ticking_ready").a("entity_ticking_ready").a("ticket").a("spawning").a("block_entity_count").a(writer);
        ObjectBidirectionalIterator objectbidirectionaliterator = this.visibleChunkMap.long2ObjectEntrySet().iterator();

        while (objectbidirectionaliterator.hasNext()) {
            Entry<PlayerChunk> entry = (Entry) objectbidirectionaliterator.next();
            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(entry.getLongKey());
            PlayerChunk playerchunk = (PlayerChunk) entry.getValue();
            Optional<IChunkAccess> optional = Optional.ofNullable(playerchunk.f());
            Optional<Chunk> optional1 = optional.flatMap((ichunkaccess) -> {
                return ichunkaccess instanceof Chunk ? Optional.of((Chunk) ichunkaccess) : Optional.empty();
            });

            csvwriter.a(chunkcoordintpair.x, chunkcoordintpair.z, playerchunk.getTicketLevel(), optional.isPresent(), optional.map(IChunkAccess::getChunkStatus).orElse((Object) null), optional1.map(Chunk::getState).orElse((Object) null), a(playerchunk.c()), a(playerchunk.a()), a(playerchunk.b()), this.distanceManager.c(entry.getLongKey()), !this.isOutsideOfRange(chunkcoordintpair), optional1.map((chunk) -> {
                return chunk.getTileEntities().size();
            }).orElse(0));
        }

    }

    private static String a(CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture) {
        try {
            Either<Chunk, PlayerChunk.Failure> either = (Either) completablefuture.getNow((Object) null);

            return either != null ? (String) either.map((chunk) -> {
                return "done";
            }, (playerchunk_failure) -> {
                return "unloaded";
            }) : "not completed";
        } catch (CompletionException completionexception) {
            return "failed " + completionexception.getCause().getMessage();
        } catch (CancellationException cancellationexception) {
            return "cancelled";
        }
    }

    @Nullable
    private NBTTagCompound readChunkData(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        NBTTagCompound nbttagcompound = this.read(chunkcoordintpair);

        return nbttagcompound == null ? null : this.getChunkData(this.level.getDimensionKey(), this.overworldDataStorage, nbttagcompound);
    }

    boolean isOutsideOfRange(ChunkCoordIntPair chunkcoordintpair) {
        long i = chunkcoordintpair.pair();

        return !this.distanceManager.d(i) ? true : this.playerMap.a(i).noneMatch((entityplayer) -> {
            return !entityplayer.isSpectator() && a(chunkcoordintpair, (Entity) entityplayer) < 16384.0D;
        });
    }

    private boolean b(EntityPlayer entityplayer) {
        return entityplayer.isSpectator() && !this.level.getGameRules().getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS);
    }

    void a(EntityPlayer entityplayer, boolean flag) {
        boolean flag1 = this.b(entityplayer);
        boolean flag2 = this.playerMap.c(entityplayer);
        int i = SectionPosition.a(entityplayer.cW());
        int j = SectionPosition.a(entityplayer.dc());

        if (flag) {
            this.playerMap.a(ChunkCoordIntPair.pair(i, j), entityplayer, flag1);
            this.c(entityplayer);
            if (!flag1) {
                this.distanceManager.a(SectionPosition.a((Entity) entityplayer), entityplayer);
            }
        } else {
            SectionPosition sectionposition = entityplayer.O();

            this.playerMap.a(sectionposition.r().pair(), entityplayer);
            if (!flag2) {
                this.distanceManager.b(sectionposition, entityplayer);
            }
        }

        for (int k = i - this.viewDistance; k <= i + this.viewDistance; ++k) {
            for (int l = j - this.viewDistance; l <= j + this.viewDistance; ++l) {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(k, l);

                this.sendChunk(entityplayer, chunkcoordintpair, new Packet[2], !flag, flag);
            }
        }

    }

    private SectionPosition c(EntityPlayer entityplayer) {
        SectionPosition sectionposition = SectionPosition.a((Entity) entityplayer);

        entityplayer.a(sectionposition);
        entityplayer.connection.sendPacket(new PacketPlayOutViewCentre(sectionposition.a(), sectionposition.c()));
        return sectionposition;
    }

    public void movePlayer(EntityPlayer entityplayer) {
        ObjectIterator objectiterator = this.entityMap.values().iterator();

        while (objectiterator.hasNext()) {
            PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();

            if (playerchunkmap_entitytracker.entity == entityplayer) {
                playerchunkmap_entitytracker.track(this.level.getPlayers());
            } else {
                playerchunkmap_entitytracker.updatePlayer(entityplayer);
            }
        }

        int i = SectionPosition.a(entityplayer.cW());
        int j = SectionPosition.a(entityplayer.dc());
        SectionPosition sectionposition = entityplayer.O();
        SectionPosition sectionposition1 = SectionPosition.a((Entity) entityplayer);
        long k = sectionposition.r().pair();
        long l = sectionposition1.r().pair();
        boolean flag = this.playerMap.d(entityplayer);
        boolean flag1 = this.b(entityplayer);
        boolean flag2 = sectionposition.s() != sectionposition1.s();

        if (flag2 || flag != flag1) {
            this.c(entityplayer);
            if (!flag) {
                this.distanceManager.b(sectionposition, entityplayer);
            }

            if (!flag1) {
                this.distanceManager.a(sectionposition1, entityplayer);
            }

            if (!flag && flag1) {
                this.playerMap.a(entityplayer);
            }

            if (flag && !flag1) {
                this.playerMap.b(entityplayer);
            }

            if (k != l) {
                this.playerMap.a(k, l, entityplayer);
            }
        }

        int i1 = sectionposition.a();
        int j1 = sectionposition.c();
        int k1;
        int l1;

        if (Math.abs(i1 - i) <= this.viewDistance * 2 && Math.abs(j1 - j) <= this.viewDistance * 2) {
            k1 = Math.min(i, i1) - this.viewDistance;
            l1 = Math.min(j, j1) - this.viewDistance;
            int i2 = Math.max(i, i1) + this.viewDistance;
            int j2 = Math.max(j, j1) + this.viewDistance;

            for (int k2 = k1; k2 <= i2; ++k2) {
                for (int l2 = l1; l2 <= j2; ++l2) {
                    ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(k2, l2);
                    boolean flag3 = a(chunkcoordintpair, i1, j1) <= this.viewDistance;
                    boolean flag4 = a(chunkcoordintpair, i, j) <= this.viewDistance;

                    this.sendChunk(entityplayer, chunkcoordintpair, new Packet[2], flag3, flag4);
                }
            }
        } else {
            ChunkCoordIntPair chunkcoordintpair1;
            boolean flag5;
            boolean flag6;

            for (k1 = i1 - this.viewDistance; k1 <= i1 + this.viewDistance; ++k1) {
                for (l1 = j1 - this.viewDistance; l1 <= j1 + this.viewDistance; ++l1) {
                    chunkcoordintpair1 = new ChunkCoordIntPair(k1, l1);
                    flag5 = true;
                    flag6 = false;
                    this.sendChunk(entityplayer, chunkcoordintpair1, new Packet[2], true, false);
                }
            }

            for (k1 = i - this.viewDistance; k1 <= i + this.viewDistance; ++k1) {
                for (l1 = j - this.viewDistance; l1 <= j + this.viewDistance; ++l1) {
                    chunkcoordintpair1 = new ChunkCoordIntPair(k1, l1);
                    flag5 = false;
                    flag6 = true;
                    this.sendChunk(entityplayer, chunkcoordintpair1, new Packet[2], false, true);
                }
            }
        }

    }

    @Override
    public Stream<EntityPlayer> a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        return this.playerMap.a(chunkcoordintpair.pair()).filter((entityplayer) -> {
            int i = a(chunkcoordintpair, entityplayer, true);

            return i > this.viewDistance ? false : !flag || i == this.viewDistance;
        });
    }

    protected void addEntity(Entity entity) {
        if (!(entity instanceof EntityComplexPart)) {
            EntityTypes<?> entitytypes = entity.getEntityType();
            int i = entitytypes.getChunkRange() * 16;

            if (i != 0) {
                int j = entitytypes.getUpdateInterval();

                if (this.entityMap.containsKey(entity.getId())) {
                    throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException("Entity is already tracked!")));
                } else {
                    PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = new PlayerChunkMap.EntityTracker(entity, i, j, entitytypes.isDeltaTracking());

                    this.entityMap.put(entity.getId(), playerchunkmap_entitytracker);
                    playerchunkmap_entitytracker.track(this.level.getPlayers());
                    if (entity instanceof EntityPlayer) {
                        EntityPlayer entityplayer = (EntityPlayer) entity;

                        this.a(entityplayer, true);
                        ObjectIterator objectiterator = this.entityMap.values().iterator();

                        while (objectiterator.hasNext()) {
                            PlayerChunkMap.EntityTracker playerchunkmap_entitytracker1 = (PlayerChunkMap.EntityTracker) objectiterator.next();

                            if (playerchunkmap_entitytracker1.entity != entityplayer) {
                                playerchunkmap_entitytracker1.updatePlayer(entityplayer);
                            }
                        }
                    }

                }
            }
        }
    }

    protected void removeEntity(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            this.a(entityplayer, false);
            ObjectIterator objectiterator = this.entityMap.values().iterator();

            while (objectiterator.hasNext()) {
                PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();

                playerchunkmap_entitytracker.clear(entityplayer);
            }
        }

        PlayerChunkMap.EntityTracker playerchunkmap_entitytracker1 = (PlayerChunkMap.EntityTracker) this.entityMap.remove(entity.getId());

        if (playerchunkmap_entitytracker1 != null) {
            playerchunkmap_entitytracker1.a();
        }

    }

    protected void g() {
        List<EntityPlayer> list = Lists.newArrayList();
        List<EntityPlayer> list1 = this.level.getPlayers();

        PlayerChunkMap.EntityTracker playerchunkmap_entitytracker;
        ObjectIterator objectiterator;

        for (objectiterator = this.entityMap.values().iterator(); objectiterator.hasNext(); playerchunkmap_entitytracker.serverEntity.a()) {
            playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();
            SectionPosition sectionposition = playerchunkmap_entitytracker.lastSectionPos;
            SectionPosition sectionposition1 = SectionPosition.a(playerchunkmap_entitytracker.entity);

            if (!Objects.equals(sectionposition, sectionposition1)) {
                playerchunkmap_entitytracker.track(list1);
                Entity entity = playerchunkmap_entitytracker.entity;

                if (entity instanceof EntityPlayer) {
                    list.add((EntityPlayer) entity);
                }

                playerchunkmap_entitytracker.lastSectionPos = sectionposition1;
            }
        }

        if (!list.isEmpty()) {
            objectiterator = this.entityMap.values().iterator();

            while (objectiterator.hasNext()) {
                playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();
                playerchunkmap_entitytracker.track(list);
            }
        }

    }

    public void broadcast(Entity entity, Packet<?> packet) {
        PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) this.entityMap.get(entity.getId());

        if (playerchunkmap_entitytracker != null) {
            playerchunkmap_entitytracker.broadcast(packet);
        }

    }

    protected void broadcastIncludingSelf(Entity entity, Packet<?> packet) {
        PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) this.entityMap.get(entity.getId());

        if (playerchunkmap_entitytracker != null) {
            playerchunkmap_entitytracker.broadcastIncludingSelf(packet);
        }

    }

    private void a(EntityPlayer entityplayer, Packet<?>[] apacket, Chunk chunk) {
        if (apacket[0] == null) {
            apacket[0] = new PacketPlayOutMapChunk(chunk);
            apacket[1] = new PacketPlayOutLightUpdate(chunk.getPos(), this.lightEngine, (BitSet) null, (BitSet) null, true);
        }

        entityplayer.a(chunk.getPos(), apacket[0], apacket[1]);
        PacketDebug.a(this.level, chunk.getPos());
        List<Entity> list = Lists.newArrayList();
        List<Entity> list1 = Lists.newArrayList();
        ObjectIterator objectiterator = this.entityMap.values().iterator();

        while (objectiterator.hasNext()) {
            PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();
            Entity entity = playerchunkmap_entitytracker.entity;

            if (entity != entityplayer && entity.cU().equals(chunk.getPos())) {
                playerchunkmap_entitytracker.updatePlayer(entityplayer);
                if (entity instanceof EntityInsentient && ((EntityInsentient) entity).getLeashHolder() != null) {
                    list.add(entity);
                }

                if (!entity.getPassengers().isEmpty()) {
                    list1.add(entity);
                }
            }
        }

        Iterator iterator;
        Entity entity1;

        if (!list.isEmpty()) {
            iterator = list.iterator();

            while (iterator.hasNext()) {
                entity1 = (Entity) iterator.next();
                entityplayer.connection.sendPacket(new PacketPlayOutAttachEntity(entity1, ((EntityInsentient) entity1).getLeashHolder()));
            }
        }

        if (!list1.isEmpty()) {
            iterator = list1.iterator();

            while (iterator.hasNext()) {
                entity1 = (Entity) iterator.next();
                entityplayer.connection.sendPacket(new PacketPlayOutMount(entity1));
            }
        }

    }

    protected VillagePlace h() {
        return this.poiManager;
    }

    public String i() {
        return this.storageName;
    }

    public CompletableFuture<Void> a(Chunk chunk) {
        return this.mainThreadExecutor.f(() -> {
            chunk.a(this.level);
        });
    }

    void a(ChunkCoordIntPair chunkcoordintpair, PlayerChunk.State playerchunk_state) {
        this.chunkStatusListener.onChunkStatusChange(chunkcoordintpair, playerchunk_state);
    }

    private class a extends ChunkMapDistance {

        protected a(Executor executor, Executor executor1) {
            super(executor, executor1);
        }

        @Override
        protected boolean a(long i) {
            return PlayerChunkMap.this.toDrop.contains(i);
        }

        @Nullable
        @Override
        protected PlayerChunk b(long i) {
            return PlayerChunkMap.this.getUpdatingChunk(i);
        }

        @Nullable
        @Override
        protected PlayerChunk a(long i, int j, @Nullable PlayerChunk playerchunk, int k) {
            return PlayerChunkMap.this.a(i, j, playerchunk, k);
        }
    }

    public class EntityTracker {

        final EntityTrackerEntry serverEntity;
        final Entity entity;
        private final int range;
        SectionPosition lastSectionPos;
        public final Set<ServerPlayerConnection> seenBy = Sets.newIdentityHashSet();

        public EntityTracker(Entity entity, int i, int j, boolean flag) {
            this.serverEntity = new EntityTrackerEntry(PlayerChunkMap.this.level, entity, j, flag, this::broadcast);
            this.entity = entity;
            this.range = i;
            this.lastSectionPos = SectionPosition.a(entity);
        }

        public boolean equals(Object object) {
            return object instanceof PlayerChunkMap.EntityTracker ? ((PlayerChunkMap.EntityTracker) object).entity.getId() == this.entity.getId() : false;
        }

        public int hashCode() {
            return this.entity.getId();
        }

        public void broadcast(Packet<?> packet) {
            Iterator iterator = this.seenBy.iterator();

            while (iterator.hasNext()) {
                ServerPlayerConnection serverplayerconnection = (ServerPlayerConnection) iterator.next();

                serverplayerconnection.sendPacket(packet);
            }

        }

        public void broadcastIncludingSelf(Packet<?> packet) {
            this.broadcast(packet);
            if (this.entity instanceof EntityPlayer) {
                ((EntityPlayer) this.entity).connection.sendPacket(packet);
            }

        }

        public void a() {
            Iterator iterator = this.seenBy.iterator();

            while (iterator.hasNext()) {
                ServerPlayerConnection serverplayerconnection = (ServerPlayerConnection) iterator.next();

                this.serverEntity.a(serverplayerconnection.d());
            }

        }

        public void clear(EntityPlayer entityplayer) {
            if (this.seenBy.remove(entityplayer.connection)) {
                this.serverEntity.a(entityplayer);
            }

        }

        public void updatePlayer(EntityPlayer entityplayer) {
            if (entityplayer != this.entity) {
                Vec3D vec3d = entityplayer.getPositionVector().d(this.serverEntity.b());
                int i = Math.min(this.b(), (PlayerChunkMap.this.viewDistance - 1) * 16);
                boolean flag = vec3d.x >= (double) (-i) && vec3d.x <= (double) i && vec3d.z >= (double) (-i) && vec3d.z <= (double) i && this.entity.a(entityplayer);

                if (flag) {
                    if (this.seenBy.add(entityplayer.connection)) {
                        this.serverEntity.b(entityplayer);
                    }
                } else if (this.seenBy.remove(entityplayer.connection)) {
                    this.serverEntity.a(entityplayer);
                }

            }
        }

        private int a(int i) {
            return PlayerChunkMap.this.level.getMinecraftServer().b(i);
        }

        private int b() {
            int i = this.range;
            Iterator iterator = this.entity.getAllPassengers().iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();
                int j = entity.getEntityType().getChunkRange() * 16;

                if (j > i) {
                    i = j;
                }
            }

            return this.a(i);
        }

        public void track(List<EntityPlayer> list) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                this.updatePlayer(entityplayer);
            }

        }
    }
}
