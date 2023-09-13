package net.minecraft.world.level.levelgen.carver;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class CarvingContext extends WorldGenerationContext {

    private final ChunkGeneratorAbstract generator;
    private final IRegistryCustom registryAccess;
    private final NoiseChunk noiseChunk;

    public CarvingContext(ChunkGeneratorAbstract chunkgeneratorabstract, IRegistryCustom iregistrycustom, LevelHeightAccessor levelheightaccessor, NoiseChunk noisechunk) {
        super(chunkgeneratorabstract, levelheightaccessor);
        this.generator = chunkgeneratorabstract;
        this.registryAccess = iregistrycustom;
        this.noiseChunk = noisechunk;
    }

    /** @deprecated */
    @Deprecated
    public Optional<IBlockData> topMaterial(Function<BlockPosition, BiomeBase> function, IChunkAccess ichunkaccess, BlockPosition blockposition, boolean flag) {
        return this.generator.topMaterial(this, function, ichunkaccess, this.noiseChunk, blockposition, flag);
    }

    /** @deprecated */
    @Deprecated
    public IRegistryCustom registryAccess() {
        return this.registryAccess;
    }
}
