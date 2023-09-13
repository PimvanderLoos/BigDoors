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
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.IChunkAccess;

public class WorldGenCarverWrapper<WC extends WorldGenCarverConfiguration> {

    public static final Codec<WorldGenCarverWrapper<?>> a = IRegistry.CARVER.dispatch((worldgencarverwrapper) -> {
        return worldgencarverwrapper.d;
    }, WorldGenCarverAbstract::c);
    public static final Codec<Supplier<WorldGenCarverWrapper<?>>> b = RegistryFileCodec.a(IRegistry.at, WorldGenCarverWrapper.a);
    public static final Codec<List<Supplier<WorldGenCarverWrapper<?>>>> c = RegistryFileCodec.b(IRegistry.at, WorldGenCarverWrapper.a);
    private final WorldGenCarverAbstract<WC> d;
    private final WC e;

    public WorldGenCarverWrapper(WorldGenCarverAbstract<WC> worldgencarverabstract, WC wc) {
        this.d = worldgencarverabstract;
        this.e = wc;
    }

    public WC a() {
        return this.e;
    }

    public boolean a(Random random, int i, int j) {
        return this.d.a(random, i, j, this.e);
    }

    public boolean a(IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, Random random, int i, int j, int k, int l, int i1, BitSet bitset) {
        return this.d.a(ichunkaccess, function, random, i, j, k, l, i1, bitset, this.e);
    }
}
