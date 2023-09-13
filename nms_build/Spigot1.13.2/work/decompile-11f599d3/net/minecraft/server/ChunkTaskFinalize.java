package net.minecraft.server;

public class ChunkTaskFinalize extends ChunkTask {

    public ChunkTaskFinalize() {}

    protected ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, ProtoChunk[] aprotochunk, int i, int j) {
        ProtoChunk protochunk = aprotochunk[aprotochunk.length / 2];

        protochunk.a(ChunkStatus.FINALIZED);
        protochunk.a(HeightMap.Type.MOTION_BLOCKING, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, HeightMap.Type.LIGHT_BLOCKING, HeightMap.Type.OCEAN_FLOOR, HeightMap.Type.WORLD_SURFACE);
        return protochunk;
    }
}
