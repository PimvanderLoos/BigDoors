package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkStatus;

public interface WorldLoadListener {

    void a(ChunkCoordIntPair chunkcoordintpair);

    void a(ChunkCoordIntPair chunkcoordintpair, @Nullable ChunkStatus chunkstatus);

    void b();
}
