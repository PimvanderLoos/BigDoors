package net.minecraft.server;

public class ChunkTaskCarve extends ChunkTask {

    public ChunkTaskCarve() {}

    protected ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, ProtoChunk[] aprotochunk, int i, int j) {
        chunkgenerator.addFeatures(new RegionLimitedWorldAccess(aprotochunk, chunkstatus.c() * 2 + 1, chunkstatus.c() * 2 + 1, i, j, world), WorldGenStage.Features.AIR);
        ProtoChunk protochunk = aprotochunk[aprotochunk.length / 2];

        protochunk.a(ChunkStatus.CARVED);
        return protochunk;
    }
}
