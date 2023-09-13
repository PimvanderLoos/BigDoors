package net.minecraft.server.level;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutLightUpdate;
import net.minecraft.network.protocol.game.PacketPlayOutMultiBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.util.DebugBuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunkExtension;
import net.minecraft.world.level.lighting.LightEngine;

public class PlayerChunk {

    public static final Either<IChunkAccess, PlayerChunk.Failure> UNLOADED_CHUNK = Either.right(PlayerChunk.Failure.UNLOADED);
    public static final CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(PlayerChunk.UNLOADED_CHUNK);
    public static final Either<Chunk, PlayerChunk.Failure> UNLOADED_LEVEL_CHUNK = Either.right(PlayerChunk.Failure.UNLOADED);
    private static final CompletableFuture<Either<Chunk, PlayerChunk.Failure>> UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(PlayerChunk.UNLOADED_LEVEL_CHUNK);
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.a();
    private static final PlayerChunk.State[] FULL_CHUNK_STATUSES = PlayerChunk.State.values();
    private static final int BLOCKS_BEFORE_RESEND_FUDGE = 64;
    private final AtomicReferenceArray<CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> futures;
    private final LevelHeightAccessor levelHeightAccessor;
    private volatile CompletableFuture<Either<Chunk, PlayerChunk.Failure>> fullChunkFuture;
    private volatile CompletableFuture<Either<Chunk, PlayerChunk.Failure>> tickingChunkFuture;
    private volatile CompletableFuture<Either<Chunk, PlayerChunk.Failure>> entityTickingChunkFuture;
    private CompletableFuture<IChunkAccess> chunkToSave;
    @Nullable
    private final DebugBuffer<PlayerChunk.b> chunkToSaveHistory;
    public int oldTicketLevel;
    private int ticketLevel;
    private int queueLevel;
    final ChunkCoordIntPair pos;
    private boolean hasChangedSections;
    private final ShortSet[] changedBlocksPerSection;
    private final BitSet blockChangedLightSectionFilter;
    private final BitSet skyChangedLightSectionFilter;
    private final LightEngine lightEngine;
    private final PlayerChunk.d onLevelChange;
    public final PlayerChunk.e playerProvider;
    private boolean wasAccessibleSinceLastSave;
    private boolean resendLight;
    private CompletableFuture<Void> pendingFullStateConfirmation;

    public PlayerChunk(ChunkCoordIntPair chunkcoordintpair, int i, LevelHeightAccessor levelheightaccessor, LightEngine lightengine, PlayerChunk.d playerchunk_d, PlayerChunk.e playerchunk_e) {
        this.futures = new AtomicReferenceArray(PlayerChunk.CHUNK_STATUSES.size());
        this.fullChunkFuture = PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE;
        this.tickingChunkFuture = PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE;
        this.entityTickingChunkFuture = PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE;
        this.chunkToSave = CompletableFuture.completedFuture((Object) null);
        this.chunkToSaveHistory = null;
        this.blockChangedLightSectionFilter = new BitSet();
        this.skyChangedLightSectionFilter = new BitSet();
        this.pendingFullStateConfirmation = CompletableFuture.completedFuture((Object) null);
        this.pos = chunkcoordintpair;
        this.levelHeightAccessor = levelheightaccessor;
        this.lightEngine = lightengine;
        this.onLevelChange = playerchunk_d;
        this.playerProvider = playerchunk_e;
        this.oldTicketLevel = PlayerChunkMap.MAX_CHUNK_DISTANCE + 1;
        this.ticketLevel = this.oldTicketLevel;
        this.queueLevel = this.oldTicketLevel;
        this.a(i);
        this.changedBlocksPerSection = new ShortSet[levelheightaccessor.getSectionsCount()];
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> getStatusFutureUnchecked(ChunkStatus chunkstatus) {
        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = (CompletableFuture) this.futures.get(chunkstatus.c());

        return completablefuture == null ? PlayerChunk.UNLOADED_CHUNK_FUTURE : completablefuture;
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> b(ChunkStatus chunkstatus) {
        return getChunkStatus(this.ticketLevel).b(chunkstatus) ? this.getStatusFutureUnchecked(chunkstatus) : PlayerChunk.UNLOADED_CHUNK_FUTURE;
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> a() {
        return this.tickingChunkFuture;
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> b() {
        return this.entityTickingChunkFuture;
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> c() {
        return this.fullChunkFuture;
    }

    @Nullable
    public Chunk getChunk() {
        CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture = this.a();
        Either<Chunk, PlayerChunk.Failure> either = (Either) completablefuture.getNow((Object) null);

        return either == null ? null : (Chunk) either.left().orElse((Object) null);
    }

    @Nullable
    public ChunkStatus e() {
        for (int i = PlayerChunk.CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            ChunkStatus chunkstatus = (ChunkStatus) PlayerChunk.CHUNK_STATUSES.get(i);
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = this.getStatusFutureUnchecked(chunkstatus);

            if (((Either) completablefuture.getNow(PlayerChunk.UNLOADED_CHUNK)).left().isPresent()) {
                return chunkstatus;
            }
        }

        return null;
    }

    @Nullable
    public IChunkAccess f() {
        for (int i = PlayerChunk.CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            ChunkStatus chunkstatus = (ChunkStatus) PlayerChunk.CHUNK_STATUSES.get(i);
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = this.getStatusFutureUnchecked(chunkstatus);

            if (!completablefuture.isCompletedExceptionally()) {
                Optional<IChunkAccess> optional = ((Either) completablefuture.getNow(PlayerChunk.UNLOADED_CHUNK)).left();

                if (optional.isPresent()) {
                    return (IChunkAccess) optional.get();
                }
            }
        }

        return null;
    }

    public CompletableFuture<IChunkAccess> getChunkSave() {
        return this.chunkToSave;
    }

    public void a(BlockPosition blockposition) {
        Chunk chunk = this.getChunk();

        if (chunk != null) {
            int i = this.levelHeightAccessor.getSectionIndex(blockposition.getY());

            if (this.changedBlocksPerSection[i] == null) {
                this.hasChangedSections = true;
                this.changedBlocksPerSection[i] = new ShortArraySet();
            }

            this.changedBlocksPerSection[i].add(SectionPosition.b(blockposition));
        }
    }

    public void a(EnumSkyBlock enumskyblock, int i) {
        Chunk chunk = this.getChunk();

        if (chunk != null) {
            chunk.setNeedsSaving(true);
            int j = this.lightEngine.c();
            int k = this.lightEngine.d();

            if (i >= j && i <= k) {
                int l = i - j;

                if (enumskyblock == EnumSkyBlock.SKY) {
                    this.skyChangedLightSectionFilter.set(l);
                } else {
                    this.blockChangedLightSectionFilter.set(l);
                }

            }
        }
    }

    public void a(Chunk chunk) {
        if (this.hasChangedSections || !this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
            World world = chunk.getWorld();
            int i = 0;

            int j;

            for (j = 0; j < this.changedBlocksPerSection.length; ++j) {
                i += this.changedBlocksPerSection[j] != null ? this.changedBlocksPerSection[j].size() : 0;
            }

            this.resendLight |= i >= 64;
            if (!this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
                this.a(new PacketPlayOutLightUpdate(chunk.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter, true), !this.resendLight);
                this.skyChangedLightSectionFilter.clear();
                this.blockChangedLightSectionFilter.clear();
            }

            for (j = 0; j < this.changedBlocksPerSection.length; ++j) {
                ShortSet shortset = this.changedBlocksPerSection[j];

                if (shortset != null) {
                    int k = this.levelHeightAccessor.getSectionYFromSectionIndex(j);
                    SectionPosition sectionposition = SectionPosition.a(chunk.getPos(), k);

                    if (shortset.size() == 1) {
                        BlockPosition blockposition = sectionposition.g(shortset.iterator().nextShort());
                        IBlockData iblockdata = world.getType(blockposition);

                        this.a(new PacketPlayOutBlockChange(blockposition, iblockdata), false);
                        this.a(world, blockposition, iblockdata);
                    } else {
                        ChunkSection chunksection = chunk.getSections()[j];
                        PacketPlayOutMultiBlockChange packetplayoutmultiblockchange = new PacketPlayOutMultiBlockChange(sectionposition, shortset, chunksection, this.resendLight);

                        this.a(packetplayoutmultiblockchange, false);
                        packetplayoutmultiblockchange.a((blockposition1, iblockdata1) -> {
                            this.a(world, blockposition1, iblockdata1);
                        });
                    }

                    this.changedBlocksPerSection[j] = null;
                }
            }

            this.hasChangedSections = false;
        }
    }

    private void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.isTileEntity()) {
            this.a(world, blockposition);
        }

    }

    private void a(World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity != null) {
            PacketPlayOutTileEntityData packetplayouttileentitydata = tileentity.getUpdatePacket();

            if (packetplayouttileentitydata != null) {
                this.a(packetplayouttileentitydata, false);
            }
        }

    }

    private void a(Packet<?> packet, boolean flag) {
        this.playerProvider.a(this.pos, flag).forEach((entityplayer) -> {
            entityplayer.connection.sendPacket(packet);
        });
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> a(ChunkStatus chunkstatus, PlayerChunkMap playerchunkmap) {
        int i = chunkstatus.c();
        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = (CompletableFuture) this.futures.get(i);

        if (completablefuture != null) {
            Either<IChunkAccess, PlayerChunk.Failure> either = (Either) completablefuture.getNow((Object) null);
            boolean flag = either != null && either.right().isPresent();

            if (!flag) {
                return completablefuture;
            }
        }

        if (getChunkStatus(this.ticketLevel).b(chunkstatus)) {
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture1 = playerchunkmap.a(this, chunkstatus);

            this.a(completablefuture1, "schedule " + chunkstatus);
            this.futures.set(i, completablefuture1);
            return completablefuture1;
        } else {
            return completablefuture == null ? PlayerChunk.UNLOADED_CHUNK_FUTURE : completablefuture;
        }
    }

    private void a(CompletableFuture<? extends Either<? extends IChunkAccess, PlayerChunk.Failure>> completablefuture, String s) {
        if (this.chunkToSaveHistory != null) {
            this.chunkToSaveHistory.a(new PlayerChunk.b(Thread.currentThread(), completablefuture, s));
        }

        this.chunkToSave = this.chunkToSave.thenCombine(completablefuture, (ichunkaccess, either) -> {
            return (IChunkAccess) either.map((ichunkaccess1) -> {
                return ichunkaccess1;
            }, (playerchunk_failure) -> {
                return ichunkaccess;
            });
        });
    }

    public PlayerChunk.State h() {
        return getChunkState(this.ticketLevel);
    }

    public ChunkCoordIntPair i() {
        return this.pos;
    }

    public int getTicketLevel() {
        return this.ticketLevel;
    }

    public int k() {
        return this.queueLevel;
    }

    private void d(int i) {
        this.queueLevel = i;
    }

    public void a(int i) {
        this.ticketLevel = i;
    }

    private void a(PlayerChunkMap playerchunkmap, CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture, Executor executor, PlayerChunk.State playerchunk_state) {
        this.pendingFullStateConfirmation.cancel(false);
        CompletableFuture<Void> completablefuture1 = new CompletableFuture();

        completablefuture1.thenRunAsync(() -> {
            playerchunkmap.a(this.pos, playerchunk_state);
        }, executor);
        this.pendingFullStateConfirmation = completablefuture1;
        completablefuture.thenAccept((either) -> {
            either.ifLeft((chunk) -> {
                completablefuture1.complete((Object) null);
            });
        });
    }

    private void a(PlayerChunkMap playerchunkmap, PlayerChunk.State playerchunk_state) {
        this.pendingFullStateConfirmation.cancel(false);
        playerchunkmap.a(this.pos, playerchunk_state);
    }

    protected void a(PlayerChunkMap playerchunkmap, Executor executor) {
        ChunkStatus chunkstatus = getChunkStatus(this.oldTicketLevel);
        ChunkStatus chunkstatus1 = getChunkStatus(this.ticketLevel);
        boolean flag = this.oldTicketLevel <= PlayerChunkMap.MAX_CHUNK_DISTANCE;
        boolean flag1 = this.ticketLevel <= PlayerChunkMap.MAX_CHUNK_DISTANCE;
        PlayerChunk.State playerchunk_state = getChunkState(this.oldTicketLevel);
        PlayerChunk.State playerchunk_state1 = getChunkState(this.ticketLevel);
        CompletableFuture completablefuture;

        if (flag) {
            Either<IChunkAccess, PlayerChunk.Failure> either = Either.right(new PlayerChunk.Failure() {
                public String toString() {
                    return "Unloaded ticket level " + PlayerChunk.this.pos;
                }
            });

            for (int i = flag1 ? chunkstatus1.c() + 1 : 0; i <= chunkstatus.c(); ++i) {
                completablefuture = (CompletableFuture) this.futures.get(i);
                if (completablefuture == null) {
                    this.futures.set(i, CompletableFuture.completedFuture(either));
                }
            }
        }

        boolean flag2 = playerchunk_state.isAtLeast(PlayerChunk.State.BORDER);
        boolean flag3 = playerchunk_state1.isAtLeast(PlayerChunk.State.BORDER);

        this.wasAccessibleSinceLastSave |= flag3;
        if (!flag2 && flag3) {
            this.fullChunkFuture = playerchunkmap.b(this);
            this.a(playerchunkmap, this.fullChunkFuture, executor, PlayerChunk.State.BORDER);
            this.a(this.fullChunkFuture, "full");
        }

        if (flag2 && !flag3) {
            completablefuture = this.fullChunkFuture;
            this.fullChunkFuture = PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE;
            this.a(completablefuture.thenApply((either1) -> {
                Objects.requireNonNull(playerchunkmap);
                return either1.ifLeft(playerchunkmap::a);
            }), "unfull");
        }

        boolean flag4 = playerchunk_state.isAtLeast(PlayerChunk.State.TICKING);
        boolean flag5 = playerchunk_state1.isAtLeast(PlayerChunk.State.TICKING);

        if (!flag4 && flag5) {
            this.tickingChunkFuture = playerchunkmap.a(this);
            this.a(playerchunkmap, this.tickingChunkFuture, executor, PlayerChunk.State.TICKING);
            this.a(this.tickingChunkFuture, "ticking");
        }

        if (flag4 && !flag5) {
            this.tickingChunkFuture.complete(PlayerChunk.UNLOADED_LEVEL_CHUNK);
            this.tickingChunkFuture = PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE;
        }

        boolean flag6 = playerchunk_state.isAtLeast(PlayerChunk.State.ENTITY_TICKING);
        boolean flag7 = playerchunk_state1.isAtLeast(PlayerChunk.State.ENTITY_TICKING);

        if (!flag6 && flag7) {
            if (this.entityTickingChunkFuture != PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE) {
                throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException()));
            }

            this.entityTickingChunkFuture = playerchunkmap.b(this.pos);
            this.a(playerchunkmap, this.entityTickingChunkFuture, executor, PlayerChunk.State.ENTITY_TICKING);
            this.a(this.entityTickingChunkFuture, "entity ticking");
        }

        if (flag6 && !flag7) {
            this.entityTickingChunkFuture.complete(PlayerChunk.UNLOADED_LEVEL_CHUNK);
            this.entityTickingChunkFuture = PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE;
        }

        if (!playerchunk_state1.isAtLeast(playerchunk_state)) {
            this.a(playerchunkmap, playerchunk_state1);
        }

        this.onLevelChange.a(this.pos, this::k, this.ticketLevel, this::d);
        this.oldTicketLevel = this.ticketLevel;
    }

    public static ChunkStatus getChunkStatus(int i) {
        return i < 33 ? ChunkStatus.FULL : ChunkStatus.a(i - 33);
    }

    public static PlayerChunk.State getChunkState(int i) {
        return PlayerChunk.FULL_CHUNK_STATUSES[MathHelper.clamp(33 - i + 1, 0, PlayerChunk.FULL_CHUNK_STATUSES.length - 1)];
    }

    public boolean hasBeenLoaded() {
        return this.wasAccessibleSinceLastSave;
    }

    public void m() {
        this.wasAccessibleSinceLastSave = getChunkState(this.ticketLevel).isAtLeast(PlayerChunk.State.BORDER);
    }

    public void a(ProtoChunkExtension protochunkextension) {
        for (int i = 0; i < this.futures.length(); ++i) {
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = (CompletableFuture) this.futures.get(i);

            if (completablefuture != null) {
                Optional<IChunkAccess> optional = ((Either) completablefuture.getNow(PlayerChunk.UNLOADED_CHUNK)).left();

                if (optional.isPresent() && optional.get() instanceof ProtoChunk) {
                    this.futures.set(i, CompletableFuture.completedFuture(Either.left(protochunkextension)));
                }
            }
        }

        this.a(CompletableFuture.completedFuture(Either.left(protochunkextension.v())), "replaceProto");
    }

    @FunctionalInterface
    public interface d {

        void a(ChunkCoordIntPair chunkcoordintpair, IntSupplier intsupplier, int i, IntConsumer intconsumer);
    }

    public interface e {

        Stream<EntityPlayer> a(ChunkCoordIntPair chunkcoordintpair, boolean flag);
    }

    private static final class b {

        private final Thread thread;
        private final CompletableFuture<? extends Either<? extends IChunkAccess, PlayerChunk.Failure>> future;
        private final String source;

        b(Thread thread, CompletableFuture<? extends Either<? extends IChunkAccess, PlayerChunk.Failure>> completablefuture, String s) {
            this.thread = thread;
            this.future = completablefuture;
            this.source = s;
        }
    }

    public static enum State {

        INACCESSIBLE, BORDER, TICKING, ENTITY_TICKING;

        private State() {}

        public boolean isAtLeast(PlayerChunk.State playerchunk_state) {
            return this.ordinal() >= playerchunk_state.ordinal();
        }
    }

    public interface Failure {

        PlayerChunk.Failure UNLOADED = new PlayerChunk.Failure() {
            public String toString() {
                return "UNLOADED";
            }
        };
    }
}
