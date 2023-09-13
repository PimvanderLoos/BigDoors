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

    public static final Codec<WorldGenSurfaceComposite<?>> a = IRegistry.SURFACE_BUILDER.dispatch((worldgensurfacecomposite) -> {
        return worldgensurfacecomposite.c;
    }, WorldGenSurface::d);
    public static final Codec<Supplier<WorldGenSurfaceComposite<?>>> b = RegistryFileCodec.a(IRegistry.as, WorldGenSurfaceComposite.a);
    public final WorldGenSurface<SC> c;
    public final SC d;

    public WorldGenSurfaceComposite(WorldGenSurface<SC> worldgensurface, SC sc) {
        this.c = worldgensurface;
        this.d = sc;
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1) {
        this.c.a(random, ichunkaccess, biomebase, i, j, k, d0, iblockdata, iblockdata1, l, i1, this.d);
    }

    public void a(long i) {
        this.c.a(i);
    }

    public SC a() {
        return this.d;
    }
}
