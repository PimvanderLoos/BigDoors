package net.minecraft.server;

public class ChunkTaskBase extends ChunkTask {

    public ChunkTaskBase() {}

    protected ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, ProtoChunk[] aprotochunk, int i, int j) {
        ProtoChunk protochunk = aprotochunk[aprotochunk.length / 2];

        chunkgenerator.createChunk(protochunk);
        return protochunk;
    }
}
