package net.minecraft.server;

public class ChunkTaskLiquidCarve extends ChunkTask {

    public ChunkTaskLiquidCarve() {}

    protected ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, ProtoChunk[] aprotochunk, int i, int j) {
        chunkgenerator.addFeatures(new RegionLimitedWorldAccess(aprotochunk, chunkstatus.c() * 2 + 1, chunkstatus.c() * 2 + 1, i, j, world), WorldGenStage.Features.LIQUID);
        ProtoChunk protochunk = aprotochunk[aprotochunk.length / 2];

        protochunk.a(HeightMap.Type.OCEAN_FLOOR_WG, HeightMap.Type.WORLD_SURFACE_WG);
        protochunk.a(ChunkStatus.LIQUID_CARVED);
        return protochunk;
    }
}
