package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldLoadListenerLogger implements WorldLoadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private final int maxCount;
    private int count;
    private long startTime;
    private long nextTickTime = Long.MAX_VALUE;

    public WorldLoadListenerLogger(int i) {
        int j = i * 2 + 1;

        this.maxCount = j * j;
    }

    @Override
    public void updateSpawnPos(ChunkCoordIntPair chunkcoordintpair) {
        this.nextTickTime = SystemUtils.getMillis();
        this.startTime = this.nextTickTime;
    }

    @Override
    public void onStatusChange(ChunkCoordIntPair chunkcoordintpair, @Nullable ChunkStatus chunkstatus) {
        if (chunkstatus == ChunkStatus.FULL) {
            ++this.count;
        }

        int i = this.getProgress();

        if (SystemUtils.getMillis() > this.nextTickTime) {
            this.nextTickTime += 500L;
            WorldLoadListenerLogger.LOGGER.info((new ChatMessage("menu.preparingSpawn", new Object[]{MathHelper.clamp(i, (int) 0, (int) 100)})).getString());
        }

    }

    @Override
    public void start() {}

    @Override
    public void stop() {
        WorldLoadListenerLogger.LOGGER.info("Time elapsed: {} ms", SystemUtils.getMillis() - this.startTime);
        this.nextTickTime = Long.MAX_VALUE;
    }

    public int getProgress() {
        return MathHelper.floor((float) this.count * 100.0F / (float) this.maxCount);
    }
}
