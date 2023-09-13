package net.minecraft.server.level;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutLightUpdate;
import net.minecraft.network.protocol.game.PacketPlayOutMultiBlockChange;
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
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
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
        this.setTicketLevel(i);
        this.changedBlocksPerSection = new ShortSet[levelheightaccessor.getSectionsCount()];
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> getFutureIfPresentUnchecked(ChunkStatus chunkstatus) {
        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = (CompletableFuture) this.futures.get(chunkstatus.getIndex());

        return completablefuture == null ? PlayerChunk.UNLOADED_CHUNK_FUTURE : completablefuture;
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> getFutureIfPresent(ChunkStatus chunkstatus) {
        return getStatus(this.ticketLevel).isOrAfter(chunkstatus) ? this.getFutureIfPresentUnchecked(chunkstatus) : PlayerChunk.UNLOADED_CHUNK_FUTURE;
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> getTickingChunkFuture() {
        return this.tickingChunkFuture;
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> getEntityTickingChunkFuture() {
        return this.entityTickingChunkFuture;
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> getFullChunkFuture() {
        return this.fullChunkFuture;
    }

    @Nullable
    public Chunk getTickingChunk() {
        CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture = this.getTickingChunkFuture();
        Either<Chunk, PlayerChunk.Failure> either = (Either) completablefuture.getNow((Object) null);

        return either == null ? null : (Chunk) either.left().orElse((Object) null);
    }

    @Nullable
    public ChunkStatus getLastAvailableStatus() {
        for (int i = PlayerChunk.CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            ChunkStatus chunkstatus = (ChunkStatus) PlayerChunk.CHUNK_STATUSES.get(i);
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = this.getFutureIfPresentUnchecked(chunkstatus);

            if (((Either) completablefuture.getNow(PlayerChunk.UNLOADED_CHUNK)).left().isPresent()) {
                return chunkstatus;
            }
        }

        return null;
    }

    @Nullable
    public IChunkAccess getLastAvailable() {
        for (int i = PlayerChunk.CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            ChunkStatus chunkstatus = (ChunkStatus) PlayerChunk.CHUNK_STATUSES.get(i);
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = this.getFutureIfPresentUnchecked(chunkstatus);

            if (!completablefuture.isCompletedExceptionally()) {
                Optional<IChunkAccess> optional = ((Either) completablefuture.getNow(PlayerChunk.UNLOADED_CHUNK)).left();

                if (optional.isPresent()) {
                    return (IChunkAccess) optional.get();
                }
            }
        }

        return null;
    }

    public CompletableFuture<IChunkAccess> getChunkToSave() {
        return this.chunkToSave;
    }

    public void blockChanged(BlockPosition blockposition) {
        Chunk chunk = this.getTickingChunk();

        if (chunk != null) {
            int i = this.levelHeightAccessor.getSectionIndex(blockposition.getY());

            if (this.changedBlocksPerSection[i] == null) {
                this.hasChangedSections = true;
                this.changedBlocksPerSection[i] = new ShortOpenHashSet();
            }

            this.changedBlocksPerSection[i].add(SectionPosition.sectionRelativePos(blockposition));
        }
    }

    public void sectionLightChanged(EnumSkyBlock enumskyblock, int i) {
        Chunk chunk = this.getTickingChunk();

        if (chunk != null) {
            chunk.setUnsaved(true);
            int j = this.lightEngine.getMinLightSection();
            int k = this.lightEngine.getMaxLightSection();

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

    public void broadcastChanges(Chunk chunk) {
        if (this.hasChangedSections || !this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
            World world = chunk.getLevel();
            int i = 0;

            int j;

            for (j = 0; j < this.changedBlocksPerSection.length; ++j) {
                i += this.changedBlocksPerSection[j] != null ? this.changedBlocksPerSection[j].size() : 0;
            }

            this.resendLight |= i >= 64;
            if (!this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
                this.broadcast(new PacketPlayOutLightUpdate(chunk.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter, true), !this.resendLight);
                this.skyChangedLightSectionFilter.clear();
                this.blockChangedLightSectionFilter.clear();
            }

            for (j = 0; j < this.changedBlocksPerSection.length; ++j) {
                ShortSet shortset = this.changedBlocksPerSection[j];

                if (shortset != null) {
                    int k = this.levelHeightAccessor.getSectionYFromSectionIndex(j);
                    SectionPosition sectionposition = SectionPosition.of(chunk.getPos(), k);

                    if (shortset.size() == 1) {
                        BlockPosition blockposition = sectionposition.relativeToBlockPos(shortset.iterator().nextShort());
                        IBlockData iblockdata = world.getBlockState(blockposition);

                        this.broadcast(new PacketPlayOutBlockChange(blockposition, iblockdata), false);
                        this.broadcastBlockEntityIfNeeded(world, blockposition, iblockdata);
                    } else {
                        ChunkSection chunksection = chunk.getSection(j);
                        PacketPlayOutMultiBlockChange packetplayoutmultiblockchange = new PacketPlayOutMultiBlockChange(sectionposition, shortset, chunksection, this.resendLight);

                        this.broadcast(packetplayoutmultiblockchange, false);
                        packetplayoutmultiblockchange.runUpdates((blockposition1, iblockdata1) -> {
                            this.broadcastBlockEntityIfNeeded(world, blockposition1, iblockdata1);
                        });
                    }

                    this.changedBlocksPerSection[j] = null;
                }
            }

            this.hasChangedSections = false;
        }
    }

    private void broadcastBlockEntityIfNeeded(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.hasBlockEntity()) {
            this.broadcastBlockEntity(world, blockposition);
        }

    }

    private void broadcastBlockEntity(World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity != null) {
            Packet<?> packet = tileentity.getUpdatePacket();

            if (packet != null) {
                this.broadcast(packet, false);
            }
        }

    }

    private void broadcast(Packet<?> packet, boolean flag) {
        this.playerProvider.getPlayers(this.pos, flag).forEach((entityplayer) -> {
            entityplayer.connection.send(packet);
        });
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> getOrScheduleFuture(ChunkStatus chunkstatus, PlayerChunkMap playerchunkmap) {
        int i = chunkstatus.getIndex();
        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = (CompletableFuture) this.futures.get(i);

        if (completablefuture != null) {
            Either<IChunkAccess, PlayerChunk.Failure> either = (Either) completablefuture.getNow((Object) null);
            boolean flag = either != null && either.right().isPresent();

            if (!flag) {
                return completablefuture;
            }
        }

        if (getStatus(this.ticketLevel).isOrAfter(chunkstatus)) {
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture1 = playerchunkmap.schedule(this, chunkstatus);

            this.updateChunkToSave(completablefuture1, "schedule " + chunkstatus);
            this.futures.set(i, completablefuture1);
            return completablefuture1;
        } else {
            return completablefuture == null ? PlayerChunk.UNLOADED_CHUNK_FUTURE : completablefuture;
        }
    }

    protected void addSaveDependency(String s, CompletableFuture<?> completablefuture) {
        if (this.chunkToSaveHistory != null) {
            this.chunkToSaveHistory.push(new PlayerChunk.b(Thread.currentThread(), completablefuture, s));
        }

        this.chunkToSave = this.chunkToSave.thenCombine(completablefuture, (ichunkaccess, object) -> {
            return ichunkaccess;
        });
    }

    private void updateChunkToSave(CompletableFuture<? extends Either<? extends IChunkAccess, PlayerChunk.Failure>> completablefuture, String s) {
        if (this.chunkToSaveHistory != null) {
            this.chunkToSaveHistory.push(new PlayerChunk.b(Thread.currentThread(), completablefuture, s));
        }

        this.chunkToSave = this.chunkToSave.thenCombine(completablefuture, (ichunkaccess, either) -> {
            return (IChunkAccess) either.map((ichunkaccess1) -> {
                return ichunkaccess1;
            }, (playerchunk_failure) -> {
                return ichunkaccess;
            });
        });
    }

    public PlayerChunk.State getFullStatus() {
        return getFullChunkStatus(this.ticketLevel);
    }

    public ChunkCoordIntPair getPos() {
        return this.pos;
    }

    public int getTicketLevel() {
        return this.ticketLevel;
    }

    public int getQueueLevel() {
        return this.queueLevel;
    }

    private void setQueueLevel(int i) {
        this.queueLevel = i;
    }

    public void setTicketLevel(int i) {
        this.ticketLevel = i;
    }

    private void scheduleFullChunkPromotion(PlayerChunkMap playerchunkmap, CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture, Executor executor, PlayerChunk.State playerchunk_state) {
        this.pendingFullStateConfirmation.cancel(false);
        CompletableFuture<Void> completablefuture1 = new CompletableFuture();

        completablefuture1.thenRunAsync(() -> {
            playerchunkmap.onFullChunkStatusChange(this.pos, playerchunk_state);
        }, executor);
        this.pendingFullStateConfirmation = completablefuture1;
        completablefuture.thenAccept((either) -> {
            either.ifLeft((chunk) -> {
                completablefuture1.complete((Object) null);
            });
        });
    }

    private void demoteFullChunk(PlayerChunkMap playerchunkmap, PlayerChunk.State playerchunk_state) {
        this.pendingFullStateConfirmation.cancel(false);
        playerchunkmap.onFullChunkStatusChange(this.pos, playerchunk_state);
    }

    protected void updateFutures(PlayerChunkMap playerchunkmap, Executor executor) {
        ChunkStatus chunkstatus = getStatus(this.oldTicketLevel);
        ChunkStatus chunkstatus1 = getStatus(this.ticketLevel);
        boolean flag = this.oldTicketLevel <= PlayerChunkMap.MAX_CHUNK_DISTANCE;
        boolean flag1 = this.ticketLevel <= PlayerChunkMap.MAX_CHUNK_DISTANCE;
        PlayerChunk.State playerchunk_state = getFullChunkStatus(this.oldTicketLevel);
        PlayerChunk.State playerchunk_state1 = getFullChunkStatus(this.ticketLevel);

        if (flag) {
            Either<IChunkAccess, PlayerChunk.Failure> either = Either.right(new PlayerChunk.Failure() {
                public String toString() {
                    return "Unloaded ticket level " + PlayerChunk.this.pos;
                }
            });

            for (int i = flag1 ? chunkstatus1.getIndex() + 1 : 0; i <= chunkstatus.getIndex(); ++i) {
                CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = (CompletableFuture) this.futures.get(i);

                if (completablefuture == null) {
                    this.futures.set(i, CompletableFuture.completedFuture(either));
                }
            }
        }

        boolean flag2 = playerchunk_state.isOrAfter(PlayerChunk.State.BORDER);
        boolean flag3 = playerchunk_state1.isOrAfter(PlayerChunk.State.BORDER);

        this.wasAccessibleSinceLastSave |= flag3;
        if (!flag2 && flag3) {
            this.fullChunkFuture = playerchunkmap.prepareAccessibleChunk(this);
            this.scheduleFullChunkPromotion(playerchunkmap, this.fullChunkFuture, executor, PlayerChunk.State.BORDER);
            this.updateChunkToSave(this.fullChunkFuture, "full");
        }

        if (flag2 && !flag3) {
            this.fullChunkFuture.complete(PlayerChunk.UNLOADED_LEVEL_CHUNK);
            this.fullChunkFuture = PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE;
        }

        boolean flag4 = playerchunk_state.isOrAfter(PlayerChunk.State.TICKING);
        boolean flag5 = playerchunk_state1.isOrAfter(PlayerChunk.State.TICKING);

        if (!flag4 && flag5) {
            this.tickingChunkFuture = playerchunkmap.prepareTickingChunk(this);
            this.scheduleFullChunkPromotion(playerchunkmap, this.tickingChunkFuture, executor, PlayerChunk.State.TICKING);
            this.updateChunkToSave(this.tickingChunkFuture, "ticking");
        }

        if (flag4 && !flag5) {
            this.tickingChunkFuture.complete(PlayerChunk.UNLOADED_LEVEL_CHUNK);
            this.tickingChunkFuture = PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE;
        }

        boolean flag6 = playerchunk_state.isOrAfter(PlayerChunk.State.ENTITY_TICKING);
        boolean flag7 = playerchunk_state1.isOrAfter(PlayerChunk.State.ENTITY_TICKING);

        if (!flag6 && flag7) {
            if (this.entityTickingChunkFuture != PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE) {
                throw (IllegalStateException) SystemUtils.pauseInIde(new IllegalStateException());
            }

            this.entityTickingChunkFuture = playerchunkmap.prepareEntityTickingChunk(this.pos);
            this.scheduleFullChunkPromotion(playerchunkmap, this.entityTickingChunkFuture, executor, PlayerChunk.State.ENTITY_TICKING);
            this.updateChunkToSave(this.entityTickingChunkFuture, "entity ticking");
        }

        if (flag6 && !flag7) {
            this.entityTickingChunkFuture.complete(PlayerChunk.UNLOADED_LEVEL_CHUNK);
            this.entityTickingChunkFuture = PlayerChunk.UNLOADED_LEVEL_CHUNK_FUTURE;
        }

        if (!playerchunk_state1.isOrAfter(playerchunk_state)) {
            this.demoteFullChunk(playerchunkmap, playerchunk_state1);
        }

        this.onLevelChange.onLevelChange(this.pos, this::getQueueLevel, this.ticketLevel, this::setQueueLevel);
        this.oldTicketLevel = this.ticketLevel;
    }

    public static ChunkStatus getStatus(int i) {
        return i < 33 ? ChunkStatus.FULL : ChunkStatus.getStatusAroundFullChunk(i - 33);
    }

    public static PlayerChunk.State getFullChunkStatus(int i) {
        return PlayerChunk.FULL_CHUNK_STATUSES[MathHelper.clamp(33 - i + 1, (int) 0, PlayerChunk.FULL_CHUNK_STATUSES.length - 1)];
    }

    public boolean wasAccessibleSinceLastSave() {
        return this.wasAccessibleSinceLastSave;
    }

    public void refreshAccessibility() {
        this.wasAccessibleSinceLastSave = getFullChunkStatus(this.ticketLevel).isOrAfter(PlayerChunk.State.BORDER);
    }

    public void replaceProtoChunk(ProtoChunkExtension protochunkextension) {
        for (int i = 0; i < this.futures.length(); ++i) {
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = (CompletableFuture) this.futures.get(i);

            if (completablefuture != null) {
                Optional<IChunkAccess> optional = ((Either) completablefuture.getNow(PlayerChunk.UNLOADED_CHUNK)).left();

                if (optional.isPresent() && optional.get() instanceof ProtoChunk) {
                    this.futures.set(i, CompletableFuture.completedFuture(Either.left(protochunkextension)));
                }
            }
        }

        this.updateChunkToSave(CompletableFuture.completedFuture(Either.left(protochunkextension.getWrapped())), "replaceProto");
    }

    @FunctionalInterface
    public interface d {

        void onLevelChange(ChunkCoordIntPair chunkcoordintpair, IntSupplier intsupplier, int i, IntConsumer intconsumer);
    }

    public interface e {

        List<EntityPlayer> getPlayers(ChunkCoordIntPair chunkcoordintpair, boolean flag);
    }

    private static final class b {

        private final Thread thread;
        private final CompletableFuture<?> future;
        private final String source;

        b(Thread thread, CompletableFuture<?> completablefuture, String s) {
            this.thread = thread;
            this.future = completablefuture;
            this.source = s;
        }
    }

    public static enum State {

        INACCESSIBLE, BORDER, TICKING, ENTITY_TICKING;

        private State() {}

        public boolean isOrAfter(PlayerChunk.State playerchunk_state) {
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
