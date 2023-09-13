package net.minecraft.world.level.entity;

import net.minecraft.server.level.PlayerChunk;
import net.minecraft.world.level.ChunkCoordIntPair;

@FunctionalInterface
public interface ChunkStatusUpdateListener {

    void onChunkStatusChange(ChunkCoordIntPair chunkcoordintpair, PlayerChunk.State playerchunk_state);
}
