package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public class WorldGenCarverWrapper<WC extends WorldGenCarverConfiguration> {

    public static final Codec<WorldGenCarverWrapper<?>> DIRECT_CODEC = IRegistry.CARVER.byNameCodec().dispatch((worldgencarverwrapper) -> {
        return worldgencarverwrapper.worldCarver;
    }, WorldGenCarverAbstract::configuredCodec);
    public static final Codec<Supplier<WorldGenCarverWrapper<?>>> CODEC = RegistryFileCodec.create(IRegistry.CONFIGURED_CARVER_REGISTRY, WorldGenCarverWrapper.DIRECT_CODEC);
    public static final Codec<List<Supplier<WorldGenCarverWrapper<?>>>> LIST_CODEC = RegistryFileCodec.homogeneousList(IRegistry.CONFIGURED_CARVER_REGISTRY, WorldGenCarverWrapper.DIRECT_CODEC);
    private final WorldGenCarverAbstract<WC> worldCarver;
    private final WC config;

    public WorldGenCarverWrapper(WorldGenCarverAbstract<WC> worldgencarverabstract, WC wc) {
        this.worldCarver = worldgencarverabstract;
        this.config = wc;
    }

    public WC config() {
        return this.config;
    }

    public boolean isStartChunk(Random random) {
        return this.worldCarver.isStartChunk(this.config, random);
    }

    public boolean carve(CarvingContext carvingcontext, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, Random random, Aquifer aquifer, ChunkCoordIntPair chunkcoordintpair, CarvingMask carvingmask) {
        return SharedConstants.debugVoidTerrain(ichunkaccess.getPos()) ? false : this.worldCarver.carve(carvingcontext, this.config, ichunkaccess, function, random, aquifer, chunkcoordintpair, carvingmask);
    }
}
