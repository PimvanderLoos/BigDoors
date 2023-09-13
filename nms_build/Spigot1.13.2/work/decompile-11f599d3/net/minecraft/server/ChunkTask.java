package net.minecraft.server;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTask {

    private static final Logger a = LogManager.getLogger();

    public ChunkTask() {}

    protected ProtoChunk[] a(ChunkStatus chunkstatus, int i, int j, Map<ChunkCoordIntPair, ProtoChunk> map) {
        int k = chunkstatus.c();
        ProtoChunk[] aprotochunk = new ProtoChunk[(1 + 2 * k) * (1 + 2 * k)];
        int l = 0;

        for (int i1 = -k; i1 <= k; ++i1) {
            for (int j1 = -k; j1 <= k; ++j1) {
                ProtoChunk protochunk = (ProtoChunk) map.get(new ChunkCoordIntPair(i + j1, j + i1));

                protochunk.b(chunkstatus.f());
                aprotochunk[l++] = protochunk;
            }
        }

        return aprotochunk;
    }

    public ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, Map<ChunkCoordIntPair, ProtoChunk> map, int i, int j) {
        ProtoChunk[] aprotochunk = this.a(chunkstatus, i, j, map);

        return this.a(chunkstatus, world, chunkgenerator, aprotochunk, i, j);
    }

    protected abstract ProtoChunk a(ChunkStatus chunkstatus, World world, ChunkGenerator<?> chunkgenerator, ProtoChunk[] aprotochunk, int i, int j);
}
