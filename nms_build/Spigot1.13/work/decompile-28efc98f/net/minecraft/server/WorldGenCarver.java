package net.minecraft.server;

import java.util.BitSet;
import java.util.Random;

public interface WorldGenCarver<C extends WorldGenFeatureConfiguration> {

    boolean a(IBlockAccess iblockaccess, Random random, int i, int j, C c0);

    boolean a(GeneratorAccess generatoraccess, Random random, int i, int j, int k, int l, BitSet bitset, C c0);
}
