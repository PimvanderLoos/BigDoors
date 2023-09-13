package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;

public class WorldChunkManagerOverworld extends WorldChunkManager {

    private final BiomeCache c = new BiomeCache(this);
    private final GenLayer d;
    private final GenLayer e;
    private final BiomeBase[] f;

    public WorldChunkManagerOverworld(BiomeLayoutOverworldConfiguration biomelayoutoverworldconfiguration) {
        this.f = new BiomeBase[] { Biomes.a, Biomes.c, Biomes.d, Biomes.e, Biomes.f, Biomes.g, Biomes.h, Biomes.i, Biomes.l, Biomes.m, Biomes.n, Biomes.o, Biomes.p, Biomes.q, Biomes.r, Biomes.s, Biomes.t, Biomes.u, Biomes.v, Biomes.w, Biomes.x, Biomes.y, Biomes.z, Biomes.A, Biomes.B, Biomes.C, Biomes.D, Biomes.E, Biomes.F, Biomes.G, Biomes.H, Biomes.I, Biomes.J, Biomes.K, Biomes.L, Biomes.M, Biomes.N, Biomes.O, Biomes.T, Biomes.U, Biomes.V, Biomes.W, Biomes.X, Biomes.Y, Biomes.Z, Biomes.ab, Biomes.ac, Biomes.ad, Biomes.ae, Biomes.af, Biomes.ag, Biomes.ah, Biomes.ai, Biomes.aj, Biomes.ak, Biomes.al, Biomes.am, Biomes.an, Biomes.ao, Biomes.ap, Biomes.aq, Biomes.ar, Biomes.as, Biomes.at, Biomes.au, Biomes.av};
        WorldData worlddata = biomelayoutoverworldconfiguration.a();
        GeneratorSettingsOverworld generatorsettingsoverworld = biomelayoutoverworldconfiguration.b();
        GenLayer[] agenlayer = GenLayers.a(worlddata.getSeed(), worlddata.getType(), generatorsettingsoverworld);

        this.d = agenlayer[0];
        this.e = agenlayer[1];
    }

    @Nullable
    public BiomeBase getBiome(BlockPosition blockposition, @Nullable BiomeBase biomebase) {
        return this.c.a(blockposition.getX(), blockposition.getZ(), biomebase);
    }

    public BiomeBase[] getBiomes(int i, int j, int k, int l) {
        return this.d.a(i, j, k, l, Biomes.b);
    }

    public BiomeBase[] a(int i, int j, int k, int l, boolean flag) {
        return flag && k == 16 && l == 16 && (i & 15) == 0 && (j & 15) == 0 ? this.c.b(i, j) : this.e.a(i, j, k, l, Biomes.b);
    }

    public Set<BiomeBase> a(int i, int j, int k) {
        int l = i - k >> 2;
        int i1 = j - k >> 2;
        int j1 = i + k >> 2;
        int k1 = j + k >> 2;
        int l1 = j1 - l + 1;
        int i2 = k1 - i1 + 1;
        HashSet hashset = Sets.newHashSet();

        Collections.addAll(hashset, this.d.a(l, i1, l1, i2, (BiomeBase) null));
        return hashset;
    }

    @Nullable
    public BlockPosition a(int i, int j, int k, List<BiomeBase> list, Random random) {
        int l = i - k >> 2;
        int i1 = j - k >> 2;
        int j1 = i + k >> 2;
        int k1 = j + k >> 2;
        int l1 = j1 - l + 1;
        int i2 = k1 - i1 + 1;
        BiomeBase[] abiomebase = this.d.a(l, i1, l1, i2, (BiomeBase) null);
        BlockPosition blockposition = null;
        int j2 = 0;

        for (int k2 = 0; k2 < l1 * i2; ++k2) {
            int l2 = l + k2 % l1 << 2;
            int i3 = i1 + k2 / l1 << 2;

            if (list.contains(abiomebase[k2])) {
                if (blockposition == null || random.nextInt(j2 + 1) == 0) {
                    blockposition = new BlockPosition(l2, 0, i3);
                }

                ++j2;
            }
        }

        return blockposition;
    }

    public boolean a(StructureGenerator<?> structuregenerator) {
        return ((Boolean) this.a.computeIfAbsent(structuregenerator, (structuregenerator) -> {
            BiomeBase[] abiomebase = this.f;
            int i = abiomebase.length;

            for (int j = 0; j < i; ++j) {
                BiomeBase biomebase = abiomebase[j];

                if (biomebase.a(structuregenerator)) {
                    return Boolean.valueOf(true);
                }
            }

            return Boolean.valueOf(false);
        })).booleanValue();
    }

    public Set<IBlockData> b() {
        if (this.b.isEmpty()) {
            BiomeBase[] abiomebase = this.f;
            int i = abiomebase.length;

            for (int j = 0; j < i; ++j) {
                BiomeBase biomebase = abiomebase[j];

                this.b.add(biomebase.r().a());
            }
        }

        return this.b;
    }

    public void Y_() {
        this.c.a();
    }
}
