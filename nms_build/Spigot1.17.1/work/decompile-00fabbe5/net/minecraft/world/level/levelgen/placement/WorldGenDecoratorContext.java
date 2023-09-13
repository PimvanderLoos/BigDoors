package net.minecraft.world.level.levelgen.placement;

import java.util.BitSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class WorldGenDecoratorContext extends WorldGenerationContext {

    private final GeneratorAccessSeed level;

    public WorldGenDecoratorContext(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator) {
        super(chunkgenerator, generatoraccessseed);
        this.level = generatoraccessseed;
    }

    public int a(HeightMap.Type heightmap_type, int i, int j) {
        return this.level.a(heightmap_type, i, j);
    }

    public BitSet a(ChunkCoordIntPair chunkcoordintpair, WorldGenStage.Features worldgenstage_features) {
        return ((ProtoChunk) this.level.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z)).b(worldgenstage_features);
    }

    public IBlockData a(BlockPosition blockposition) {
        return this.level.getType(blockposition);
    }

    public int c() {
        return this.level.getMinBuildHeight();
    }

    public GeneratorAccessSeed d() {
        return this.level;
    }
}
