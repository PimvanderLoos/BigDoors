package net.minecraft.server;

import java.util.BitSet;
import java.util.Random;

public class WorldGenCarverWrapper<C extends WorldGenFeatureConfiguration> implements WorldGenCarver<WorldGenFeatureEmptyConfiguration> {

    private final WorldGenCarver<C> a;
    private final C b;

    public WorldGenCarverWrapper(WorldGenCarver<C> worldgencarver, C c0) {
        this.a = worldgencarver;
        this.b = c0;
    }

    public boolean a(IBlockAccess iblockaccess, Random random, int i, int j, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        return this.a.a(iblockaccess, random, i, j, this.b);
    }

    public boolean a(GeneratorAccess generatoraccess, Random random, int i, int j, int k, int l, BitSet bitset, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        return this.a.a(generatoraccess, random, i, j, k, l, bitset, this.b);
    }
}
