package net.minecraft.server;

public class ChunkTaskNull extends ChunkTask {

    public ChunkTaskNull() {}

    protected ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, ProtoChunk[] aprotochunk, int i, int j) {
        return null;
    }
}
