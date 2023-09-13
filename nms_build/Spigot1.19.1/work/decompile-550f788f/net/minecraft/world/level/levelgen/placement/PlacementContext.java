package net.minecraft.world.level.levelgen.placement;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class PlacementContext extends WorldGenerationContext {

    private final GeneratorAccessSeed level;
    private final ChunkGenerator generator;
    private final Optional<PlacedFeature> topFeature;

    public PlacementContext(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Optional<PlacedFeature> optional) {
        super(chunkgenerator, generatoraccessseed);
        this.level = generatoraccessseed;
        this.generator = chunkgenerator;
        this.topFeature = optional;
    }

    public int getHeight(HeightMap.Type heightmap_type, int i, int j) {
        return this.level.getHeight(heightmap_type, i, j);
    }

    public CarvingMask getCarvingMask(ChunkCoordIntPair chunkcoordintpair, WorldGenStage.Features worldgenstage_features) {
        return ((ProtoChunk) this.level.getChunk(chunkcoordintpair.x, chunkcoordintpair.z)).getOrCreateCarvingMask(worldgenstage_features);
    }

    public IBlockData getBlockState(BlockPosition blockposition) {
        return this.level.getBlockState(blockposition);
    }

    public int getMinBuildHeight() {
        return this.level.getMinBuildHeight();
    }

    public GeneratorAccessSeed getLevel() {
        return this.level;
    }

    public Optional<PlacedFeature> topFeature() {
        return this.topFeature;
    }

    public ChunkGenerator generator() {
        return this.generator;
    }
}
