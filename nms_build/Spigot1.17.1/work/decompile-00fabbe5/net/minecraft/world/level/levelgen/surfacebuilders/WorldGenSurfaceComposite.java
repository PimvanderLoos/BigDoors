package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;

public class WorldGenSurfaceComposite<SC extends WorldGenSurfaceConfiguration> {

    public static final Codec<WorldGenSurfaceComposite<?>> DIRECT_CODEC = IRegistry.SURFACE_BUILDER.dispatch((worldgensurfacecomposite) -> {
        return worldgensurfacecomposite.surfaceBuilder;
    }, WorldGenSurface::d);
    public static final Codec<Supplier<WorldGenSurfaceComposite<?>>> CODEC = RegistryFileCodec.a(IRegistry.CONFIGURED_SURFACE_BUILDER_REGISTRY, WorldGenSurfaceComposite.DIRECT_CODEC);
    public final WorldGenSurface<SC> surfaceBuilder;
    public final SC config;

    public WorldGenSurfaceComposite(WorldGenSurface<SC> worldgensurface, SC sc) {
        this.surfaceBuilder = worldgensurface;
        this.config = sc;
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1) {
        this.surfaceBuilder.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, j1, this.config);
    }

    public void a(long i) {
        this.surfaceBuilder.a(i);
    }

    public SC a() {
        return this.config;
    }
}
