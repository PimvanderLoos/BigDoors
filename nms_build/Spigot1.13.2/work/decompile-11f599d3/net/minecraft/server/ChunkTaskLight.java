package net.minecraft.server;

public class ChunkTaskLight extends ChunkTask {

    public ChunkTaskLight() {}

    protected ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, ProtoChunk[] aprotochunk, int i, int j) {
        ProtoChunk protochunk = aprotochunk[aprotochunk.length / 2];
        RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(aprotochunk, chunkstatus.c() * 2 + 1, chunkstatus.c() * 2 + 1, i, j, world);

        protochunk.a(HeightMap.Type.LIGHT_BLOCKING);
        if (regionlimitedworldaccess.o().g()) {
            (new LightEngineSky()).a(regionlimitedworldaccess, (IChunkAccess) protochunk);
        }

        (new LightEngineBlock()).a(regionlimitedworldaccess, (IChunkAccess) protochunk);
        protochunk.a(ChunkStatus.LIGHTED);
        return protochunk;
    }
}
