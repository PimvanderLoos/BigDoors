package net.minecraft.world.level.levelgen.carver;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class CarvingContext extends WorldGenerationContext {

    private final IRegistryCustom registryAccess;
    private final NoiseChunk noiseChunk;
    private final RandomState randomState;
    private final SurfaceRules.o surfaceRule;

    public CarvingContext(ChunkGeneratorAbstract chunkgeneratorabstract, IRegistryCustom iregistrycustom, LevelHeightAccessor levelheightaccessor, NoiseChunk noisechunk, RandomState randomstate, SurfaceRules.o surfacerules_o) {
        super(chunkgeneratorabstract, levelheightaccessor);
        this.registryAccess = iregistrycustom;
        this.noiseChunk = noisechunk;
        this.randomState = randomstate;
        this.surfaceRule = surfacerules_o;
    }

    /** @deprecated */
    @Deprecated
    public Optional<IBlockData> topMaterial(Function<BlockPosition, Holder<BiomeBase>> function, IChunkAccess ichunkaccess, BlockPosition blockposition, boolean flag) {
        return this.randomState.surfaceSystem().topMaterial(this.surfaceRule, this, function, ichunkaccess, this.noiseChunk, blockposition, flag);
    }

    /** @deprecated */
    @Deprecated
    public IRegistryCustom registryAccess() {
        return this.registryAccess;
    }

    public RandomState randomState() {
        return this.randomState;
    }
}
