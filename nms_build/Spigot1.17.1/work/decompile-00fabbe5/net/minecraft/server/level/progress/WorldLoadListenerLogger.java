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
    public void a(ChunkCoordIntPair chunkcoordintpair) {
        this.nextTickTime = SystemUtils.getMonotonicMillis();
        this.startTime = this.nextTickTime;
    }

    @Override
    public void a(ChunkCoordIntPair chunkcoordintpair, @Nullable ChunkStatus chunkstatus) {
        if (chunkstatus == ChunkStatus.FULL) {
            ++this.count;
        }

        int i = this.c();

        if (SystemUtils.getMonotonicMillis() > this.nextTickTime) {
            this.nextTickTime += 500L;
            WorldLoadListenerLogger.LOGGER.info((new ChatMessage("menu.preparingSpawn", new Object[]{MathHelper.clamp(i, 0, 100)})).getString());
        }

    }

    @Override
    public void a() {}

    @Override
    public void b() {
        WorldLoadListenerLogger.LOGGER.info("Time elapsed: {} ms", SystemUtils.getMonotonicMillis() - this.startTime);
        this.nextTickTime = Long.MAX_VALUE;
    }

    public int c() {
        return MathHelper.d((float) this.count * 100.0F / (float) this.maxCount);
    }
}
