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
    public int runUpdates(int i, boolean flag, boolean flag1) {
        throw (UnsupportedOperationException) SystemUtils.pauseInIde(new UnsupportedOperationException("Ran automatically on a different thread!"));
    }

    @Override
    public void onBlockEmissionIncrease(BlockPosition blockposition, int i) {
        throw (UnsupportedOperationException) SystemUtils.pauseInIde(new UnsupportedOperationException("Ran automatically on a different thread!"));
    }

    @Override
    public void checkBlock(BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.immutable();

        this.addTask(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()), LightEngineThreaded.Update.POST_UPDATE, SystemUtils.name(() -> {
            super.checkBlock(blockposition1);
        }, () -> {
            return "checkBlock " + blockposition1;
        }));
    }

    protected void updateChunkStatus(ChunkCoordIntPair chunkcoordintpair) {
        this.addTask(chunkcoordintpair.x, chunkcoordintpair.z, () -> {
            return 0;
        }, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.name(() -> {
            super.retainData(chunkcoordintpair, false);
            super.enableLightSources(chunkcoordintpair, false);

            int i;

            for (i = this.getMinLightSection(); i < this.getMaxLightSection(); ++i) {
                super.queueSectionData(EnumSkyBlock.BLOCK, SectionPosition.of(chunkcoordintpair, i), (NibbleArray) null, true);
                super.queueSectionData(EnumSkyBlock.SKY, SectionPosition.of(chunkcoordintpair, i), (NibbleArray) null, true);
            }

            for (i = this.levelHeightAccessor.getMinSection(); i < this.levelHeightAccessor.getMaxSection(); ++i) {
                super.updateSectionStatus(SectionPosition.of(chunkcoordintpair, i), true);
            }

        }, () -> {
            return "updateChunkStatus " + chunkcoordintpair + " true";
        }));
    }

    @Override
    public void updateSectionStatus(SectionPosition sectionposition, boolean flag) {
        this.addTask(sectionposition.x(), sectionposition.z(), () -> {
            return 0;
        }, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.name(() -> {
            super.updateSectionStatus(sectionposition, flag);
        }, () -> {
            return "updateSectionStatus " + sectionposition + " " + flag;
        }));
    }

    @Override
    public void enableLightSources(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        this.addTask(chunkcoordintpair.x, chunkcoordintpair.z, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.name(() -> {
            super.enableLightSources(chunkcoordintpair, flag);
        }, () -> {
            return "enableLight " + chunkcoordintpair + " " + flag;
        }));
    }

    @Override
    public void queueSectionData(EnumSkyBlock enumskyblock, SectionPosition sectionposition, @Nullable NibbleArray nibblearray, boolean flag) {
        this.addTask(sectionposition.x(), sectionposition.z(), () -> {
            return 0;
        }, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.name(() -> {
            super.queueSectionData(enumskyblock, sectionposition, nibblearray, flag);
        }, () -> {
            return "queueData " + sectionposition;
        }));
    }

    private void addTask(int i, int j, LightEngineThreaded.Update lightenginethreaded_update, Runnable runnable) {
        this.addTask(i, j, this.chunkMap.getChunkQueueLevel(ChunkCoordIntPair.asLong(i, j)), lightenginethreaded_update, runnable);
    }

    private void addTask(int i, int j, IntSupplier intsupplier, LightEngineThreaded.Update lightenginethreaded_update, Runnable runnable) {
        this.sorterMailbox.tell(ChunkTaskQueueSorter.message(() -> {
            this.lightTasks.add(Pair.of(lightenginethreaded_update, runnable));
            if (this.lightTasks.size() >= this.taskPerBatch) {
                this.runUpdate();
            }

        }, ChunkCoordIntPair.asLong(i, j), intsupplier));
    }

    @Override
    public void retainData(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        this.addTask(chunkcoordintpair.x, chunkcoordintpair.z, () -> {
            return 0;
        }, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.name(() -> {
            super.retainData(chunkcoordintpair, flag);
        }, () -> {
            return "retainData " + chunkcoordintpair;
        }));
    }

    public CompletableFuture<IChunkAccess> lightChunk(IChunkAccess ichunkaccess, boolean flag) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

        ichunkaccess.setLightCorrect(false);
        this.addTask(chunkcoordintpair.x, chunkcoordintpair.z, LightEngineThreaded.Update.PRE_UPDATE, SystemUtils.name(() -> {
            ChunkSection[] achunksection = ichunkaccess.getSections();

            for (int i = 0; i < ichunkaccess.getSectionsCount(); ++i) {
                ChunkSection chunksection = achunksection[i];

                if (!chunksection.hasOnlyAir()) {
                    int j = this.levelHeightAccessor.getSectionYFromSectionIndex(i);

                    super.updateSectionStatus(SectionPosition.of(chunkcoordintpair, j), false);
                }
            }

            super.enableLightSources(chunkcoordintpair, true);
            if (!flag) {
                ichunkaccess.getLights().forEach((blockposition) -> {
                    super.onBlockEmissionIncrease(blockposition, ichunkaccess.getLightEmission(blockposition));
                });
            }

        }, () -> {
            return "lightChunk " + chunkcoordintpair + " " + flag;
        }));
        return CompletableFuture.supplyAsync(() -> {
            ichunkaccess.setLightCorrect(true);
            super.retainData(chunkcoordintpair, false);
            this.chunkMap.releaseLightTicket(chunkcoordintpair);
            return ichunkaccess;
        }, (runnable) -> {
            this.addTask(chunkcoordintpair.x, chunkcoordintpair.z, LightEngineThreaded.Update.POST_UPDATE, runnable);
        });
    }

    public void tryScheduleUpdate() {
        if ((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
            this.taskMailbox.tell(() -> {
                this.runUpdate();
                this.scheduled.set(false);
            });
        }

    }

    private void runUpdate() {
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
        super.runUpdates(Integer.MAX_VALUE, true, true);

        for (j = 0; objectlistiterator.hasNext() && j < i; ++j) {
            pair = (Pair) objectlistiterator.next();
            if (pair.getFirst() == LightEngineThreaded.Update.POST_UPDATE) {
                ((Runnable) pair.getSecond()).run();
            }

            objectlistiterator.remove();
        }

    }

    public void setTaskPerBatch(int i) {
        this.taskPerBatch = i;
    }

    private static enum Update {

        PRE_UPDATE, POST_UPDATE;

        private Update() {}
    }
}
