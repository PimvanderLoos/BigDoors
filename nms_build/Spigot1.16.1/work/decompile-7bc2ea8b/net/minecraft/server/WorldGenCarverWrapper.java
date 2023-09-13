package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

public class WorldGenCarverWrapper<WC extends WorldGenCarverConfiguration> {

    public static final Codec<WorldGenCarverWrapper<?>> a = IRegistry.CARVER.dispatch("name", (worldgencarverwrapper) -> {
        return worldgencarverwrapper.b;
    }, WorldGenCarverAbstract::c);
    public final WorldGenCarverAbstract<WC> b;
    public final WC c;

    public WorldGenCarverWrapper(WorldGenCarverAbstract<WC> worldgencarverabstract, WC wc) {
        this.b = worldgencarverabstract;
        this.c = wc;
    }

    public boolean a(Random random, int i, int j) {
        return this.b.a(random, i, j, this.c);
    }

    public boolean a(IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, Random random, int i, int j, int k, int l, int i1, BitSet bitset) {
        return this.b.a(ichunkaccess, function, random, i, j, k, l, i1, bitset, this.c);
    }
}
