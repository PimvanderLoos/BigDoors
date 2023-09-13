package net.minecraft.server;

public class ChunkTaskSpawnMobs extends ChunkTask {

    public ChunkTaskSpawnMobs() {}

    protected ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, ProtoChunk[] aprotochunk, int i, int j) {
        RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(aprotochunk, chunkstatus.c() * 2 + 1, chunkstatus.c() * 2 + 1, i, j, world);
        ProtoChunk protochunk = aprotochunk[aprotochunk.length / 2];

        chunkgenerator.addMobs(regionlimitedworldaccess);
        protochunk.a(ChunkStatus.MOBS_SPAWNED);
        return protochunk;
    }
}
