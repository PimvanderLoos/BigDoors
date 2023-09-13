package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkStatus;

public interface WorldLoadListener {

    void updateSpawnPos(ChunkCoordIntPair chunkcoordintpair);

    void onStatusChange(ChunkCoordIntPair chunkcoordintpair, @Nullable ChunkStatus chunkstatus);

    void start();

    void stop();
}
