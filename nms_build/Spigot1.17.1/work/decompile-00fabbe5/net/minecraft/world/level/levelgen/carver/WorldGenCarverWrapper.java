package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public class WorldGenCarverWrapper<WC extends WorldGenCarverConfiguration> {

    public static final Codec<WorldGenCarverWrapper<?>> DIRECT_CODEC = IRegistry.CARVER.dispatch((worldgencarverwrapper) -> {
        return worldgencarverwrapper.worldCarver;
    }, WorldGenCarverAbstract::c);
    public static final Codec<Supplier<WorldGenCarverWrapper<?>>> CODEC = RegistryFileCodec.a(IRegistry.CONFIGURED_CARVER_REGISTRY, WorldGenCarverWrapper.DIRECT_CODEC);
    public static final Codec<List<Supplier<WorldGenCarverWrapper<?>>>> LIST_CODEC = RegistryFileCodec.b(IRegistry.CONFIGURED_CARVER_REGISTRY, WorldGenCarverWrapper.DIRECT_CODEC);
    private final WorldGenCarverAbstract<WC> worldCarver;
    private final WC config;

    public WorldGenCarverWrapper(WorldGenCarverAbstract<WC> worldgencarverabstract, WC wc) {
        this.worldCarver = worldgencarverabstract;
        this.config = wc;
    }

    public WC a() {
        return this.config;
    }

    public boolean a(Random random) {
        return this.worldCarver.a(this.config, random);
    }

    public boolean a(CarvingContext carvingcontext, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, Random random, Aquifer aquifer, ChunkCoordIntPair chunkcoordintpair, BitSet bitset) {
        return this.worldCarver.a(carvingcontext, this.config, ichunkaccess, function, random, aquifer, chunkcoordintpair, bitset);
    }
}
