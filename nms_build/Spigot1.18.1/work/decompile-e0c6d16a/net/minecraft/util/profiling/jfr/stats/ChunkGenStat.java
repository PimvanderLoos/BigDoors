package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.server.level.BlockPosition2D;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkStatus;

public record ChunkGenStat(Duration a, ChunkCoordIntPair b, BlockPosition2D c, ChunkStatus d, String e) implements TimedStat {

    private final Duration duration;
    private final ChunkCoordIntPair chunkPos;
    private final BlockPosition2D worldPos;
    private final ChunkStatus status;
    private final String level;

    public ChunkGenStat(Duration duration, ChunkCoordIntPair chunkcoordintpair, BlockPosition2D blockposition2d, ChunkStatus chunkstatus, String s) {
        this.duration = duration;
        this.chunkPos = chunkcoordintpair;
        this.worldPos = blockposition2d;
        this.status = chunkstatus;
        this.level = s;
    }

    public static ChunkGenStat from(RecordedEvent recordedevent) {
        return new ChunkGenStat(recordedevent.getDuration(), new ChunkCoordIntPair(recordedevent.getInt("chunkPosX"), recordedevent.getInt("chunkPosX")), new BlockPosition2D(recordedevent.getInt("worldPosX"), recordedevent.getInt("worldPosZ")), ChunkStatus.byName(recordedevent.getString("status")), recordedevent.getString("level"));
    }

    @Override
    public Duration duration() {
        return this.duration;
    }

    public ChunkCoordIntPair chunkPos() {
        return this.chunkPos;
    }

    public BlockPosition2D worldPos() {
        return this.worldPos;
    }

    public ChunkStatus status() {
        return this.status;
    }

    public String level() {
        return this.level;
    }
}
