package net.minecraft.server;

import java.util.BitSet;

public class WorldGenDecoratorContext {

    private final GeneratorAccessSeed a;
    private final ChunkGenerator b;

    public WorldGenDecoratorContext(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator) {
        this.a = generatoraccessseed;
        this.b = chunkgenerator;
    }

    public int a(HeightMap.Type heightmap_type, int i, int j) {
        return this.a.a(heightmap_type, i, j);
    }

    public int a() {
        return this.b.getGenerationDepth();
    }

    public int b() {
        return this.b.getSeaLevel();
    }

    public BitSet a(ChunkCoordIntPair chunkcoordintpair, WorldGenStage.Features worldgenstage_features) {
        return ((ProtoChunk) this.a.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z)).b(worldgenstage_features);
    }

    public IBlockData a(BlockPosition blockposition) {
        return this.a.getType(blockposition);
    }
}
