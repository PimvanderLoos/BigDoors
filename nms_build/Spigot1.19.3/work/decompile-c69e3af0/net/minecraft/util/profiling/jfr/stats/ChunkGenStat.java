package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.server.level.BlockPosition2D;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkStatus;

public record ChunkGenStat(Duration duration, ChunkCoordIntPair chunkPos, BlockPosition2D worldPos, ChunkStatus status, String level) implements TimedStat {

    public static ChunkGenStat from(RecordedEvent recordedevent) {
        return new ChunkGenStat(recordedevent.getDuration(), new ChunkCoordIntPair(recordedevent.getInt("chunkPosX"), recordedevent.getInt("chunkPosX")), new BlockPosition2D(recordedevent.getInt("worldPosX"), recordedevent.getInt("worldPosZ")), ChunkStatus.byName(recordedevent.getString("status")), recordedevent.getString("level"));
    }
}
