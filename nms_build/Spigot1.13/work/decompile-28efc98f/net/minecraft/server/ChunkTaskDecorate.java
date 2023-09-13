package net.minecraft.server;

public class ChunkTaskDecorate extends ChunkTask {

    public ChunkTaskDecorate() {}

    protected ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, ProtoChunk[] aprotochunk, int i, int j) {
        chunkgenerator.addDecorations(new RegionLimitedWorldAccess(aprotochunk, chunkstatus.c() * 2 + 1, chunkstatus.c() * 2 + 1, i, j, world));
        ProtoChunk protochunk = aprotochunk[aprotochunk.length / 2];

        protochunk.a(ChunkStatus.DECORATED);
        return protochunk;
    }
}
