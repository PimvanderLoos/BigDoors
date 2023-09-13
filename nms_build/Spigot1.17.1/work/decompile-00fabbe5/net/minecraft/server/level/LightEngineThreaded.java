package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.thread.Mailbox;
import net.minecraft.util.thread.ThreadedMailbox;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.ILightAccess;
import net.minecraft.world.level.chunk.NibbleArray;
import net.minecraft.world.level.lighting.LightEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LightEngineThreaded extends LightEngine implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ThreadedMailbox<Runnable> taskMailbox;
    private final ObjectList<Pair<LightEngineThreaded.Update, Runnable>> lightTasks = new ObjectArrayList();
    private final PlayerChunkMap chunkMap;
    private final Mailbox<ChunkTaskQueueSorter.a<Runnable>> sorterMailbox;
    private volatile int taskPerBatch = 5;
    private final AtomicBoolean scheduled = new AtomicBoolean();

    public LightEngineThreaded(ILightAccess ilightaccess, PlayerChunkMap playerchunkmap, boolean flag, ThreadedMailbox<Runnable> threadedmailbox, Mailbox<ChunkTaskQueueSorter.a<Runnable>> mailbox) {
        super(ilightaccess, true, flag);
        this.chunkMap = playerchunkmap;
        this.sorterMailbox = mailbox;
        this.taskMailbox = threadedmailbox;
    }

    public void close() {}

    @Override
    public int a(int i, boolean flag, boolean flag1) {
        throw (UnsupportedOperationException) SystemUtils.c((Throwable) (new UnsupportedOperationException("Ran automatically on a different thread!")));
    }

    @Override
    public void a(BlockPosition blockposition, int i) {
        throw (UnsupportedOperationException) SystemUtils.c((Throwable) (new UnsupportedOperationException("Ran automatically on a different thread!")));
    }

    @Override
    public void a(BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.immutableCopy();

        this.a(SectionPosition.a(blockposition.getX()), SectionPosition.a(blockposition.getZ()), LightEngineThreaded.Update.POST_UPDATE, SystemUtils.a(() -> {
            super.a(blockposition1);
        }, () -> {
            return "checkBlock " + blockposition1;
        }));
    }

    protected void a(ChunkCoordIntPair chunkcoordintpair) {
        this.a(chunkcoordintpair.x, chunkcoordintpair.z, () -> {
            return 0;
        }, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.a(() -> {
            super.b(chunkcoordintpair, false);
            super.a(chunkcoordintpair, false);

            int i;

            for (i = this.c(); i < this.d(); ++i) {
                super.a(EnumSkyBlock.BLOCK, SectionPosition.a(chunkcoordintpair, i), (NibbleArray) null, true);
                super.a(EnumSkyBlock.SKY, SectionPosition.a(chunkcoordintpair, i), (NibbleArray) null, true);
            }

            for (i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
                super.a(SectionPosition.a(chunkcoordintpair, i), true);
            }

        }, () -> {
            return "updateChunkStatus " + chunkcoordintpair + " true";
        }));
    }

    @Override
    public void a(SectionPosition sectionposition, boolean flag) {
        this.a(sectionposition.a(), sectionposition.c(), () -> {
            return 0;
        }, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.a(() -> {
            super.a(sectionposition, flag);
        }, () -> {
            return "updateSectionStatus " + sectionposition + " " + flag;
        }));
    }

    @Override
    public void a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        this.a(chunkcoordintpair.x, chunkcoordintpair.z, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.a(() -> {
            super.a(chunkcoordintpair, flag);
        }, () -> {
            return "enableLight " + chunkcoordintpair + " " + flag;
        }));
    }

    @Override
    public void a(EnumSkyBlock enumskyblock, SectionPosition sectionposition, @Nullable NibbleArray nibblearray, boolean flag) {
        this.a(sectionposition.a(), sectionposition.c(), () -> {
            return 0;
        }, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.a(() -> {
            super.a(enumskyblock, sectionposition, nibblearray, flag);
        }, () -> {
            return "queueData " + sectionposition;
        }));
    }

    private void a(int i, int j, LightEngineThreaded.Update lightenginethreaded_update, Runnable runnable) {
        this.a(i, j, this.chunkMap.c(ChunkCoordIntPair.pair(i, j)), lightenginethreaded_update, runnable);
    }

    private void a(int i, int j, IntSupplier intsupplier, LightEngineThreaded.Update lightenginethreaded_update, Runnable runnable) {
        this.sorterMailbox.a(ChunkTaskQueueSorter.a(() -> {
            this.lightTasks.add(Pair.of(lightenginethreaded_update, runnable));
            if (this.lightTasks.size() >= this.taskPerBatch) {
                this.e();
            }

        }, ChunkCoordIntPair.pair(i, j), intsupplier));
    }

    @Override
    public void b(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        this.a(chunkcoordintpair.x, chunkcoordintpair.z, () -> {
            return 0;
        }, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.a(() -> {
            super.b(chunkcoordintpair, flag);
        }, () -> {
            return "retainData " + chunkcoordintpair;
        }));
    }

    public CompletableFuture<IChunkAccess> a(IChunkAccess ichunkaccess, boolean flag) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

        ichunkaccess.b(false);
        this.a(chunkcoordintpair.x, chunkcoordintpair.z, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.a(() -> {
            ChunkSection[] achunksection = ichunkaccess.getSections();

            for (int i = 0; i < ichunkaccess.getSectionsCount(); ++i) {
                ChunkSection chunksection = achunksection[i];

                if (!ChunkSection.a(chunksection)) {
                    int j = this.levelHeightAccessor.getSectionYFromSectionIndex(i);

                    super.a(SectionPosition.a(chunkcoordintpair, j), false);
                }
            }

            super.a(chunkcoordintpair, true);
            if (!flag) {
                ichunkaccess.n().forEach((blockposition) -> {
                    super.a(blockposition, ichunkaccess.h(blockposition));
                });
            }

        }, () -> {
            return "lightChunk " + chunkcoordintpair + " " + flag;
        }));
        return CompletableFuture.supplyAsync(() -> {
            ichunkaccess.b(true);
            super.b(chunkcoordintpair, false);
            this.chunkMap.c(chunkcoordintpair);
            return ichunkaccess;
        }, (runnable) -> {
            this.a(chunkcoordintpair.x, chunkcoordintpair.z, LightEngineThreaded.Update.POST_UPDATE, runnable);
        });
    }

    public void queueUpdate() {
        if ((!this.lightTasks.isEmpty() || super.z_()) && this.scheduled.compareAndSet(false, true)) {
            this.taskMailbox.a((Object) (() -> {
                this.e();
                this.scheduled.set(false);
            }));
        }

    }

    private void e() {
        int i = Math.min(this.lightTasks.size(), this.taskPerBatch);
        ObjectListIterator<Pair<LightEngineThreaded.Update, Runnable>> objectlistiterator = this.lightTasks.iterator();

        Pair pair;
        int j;

        for (j = 0; objectlistiterator.hasNext() && j < i; ++j) {
            pair = (Pair) objectlistiterator.next();
            if (pair.getFirst() == LightEngineThreaded.Update.PRE_UPDATE) {
                ((Runnable) pair.getSecond()).run();
            }
        }

        objectlistiterator.back(j);
        super.a(Integer.MAX_VALUE, true, true);

        for (j = 0; objectlistiterator.hasNext() && j < i; ++j) {
            pair = (Pair) objectlistiterator.next();
            if (pair.getFirst() == LightEngineThreaded.Update.POST_UPDATE) {
                ((Runnable) pair.getSecond()).run();
            }

            objectlistiterator.remove();
        }

    }

    public void a(int i) {
        this.taskPerBatch = i;
    }

    private static enum Update {

        PRE_UPDATE, POST_UPDATE;

        private Update() {}
    }
}
