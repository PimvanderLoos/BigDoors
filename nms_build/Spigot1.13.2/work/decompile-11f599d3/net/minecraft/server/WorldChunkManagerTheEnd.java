package net.minecraft.server;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class WorldChunkManagerTheEnd extends WorldChunkManager {

    private final NoiseGenerator3Handler c;
    private final SeededRandom d;
    private final BiomeBase[] e;

    public WorldChunkManagerTheEnd(BiomeLayoutTheEndConfiguration biomelayouttheendconfiguration) {
        this.e = new BiomeBase[] { Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS};
        this.d = new SeededRandom(biomelayouttheendconfiguration.a());
        this.d.a(17292);
        this.c = new NoiseGenerator3Handler(this.d);
    }

    @Nullable
    public BiomeBase getBiome(BlockPosition blockposition, @Nullable BiomeBase biomebase) {
        return this.a(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }

    private BiomeBase a(int i, int j) {
        if ((long) i * (long) i + (long) j * (long) j <= 4096L) {
            return Biomes.THE_END;
        } else {
            float f = this.c(i, j, 1, 1);

            return f > 40.0F ? Biomes.END_HIGHLANDS : (f >= 0.0F ? Biomes.END_MIDLANDS : (f < -20.0F ? Biomes.SMALL_END_ISLANDS : Biomes.END_BARRENS));
        }
    }

    public BiomeBase[] getBiomes(int i, int j, int k, int l) {
        return this.getBiomeBlock(i, j, k, l);
    }

    public BiomeBase[] a(int i, int j, int k, int l, boolean flag) {
        BiomeBase[] abiomebase = new BiomeBase[k * l];
        Long2ObjectMap<BiomeBase> long2objectmap = new Long2ObjectOpenHashMap();

        for (int i1 = 0; i1 < k; ++i1) {
            for (int j1 = 0; j1 < l; ++j1) {
                int k1 = i1 + i >> 4;
                int l1 = j1 + j >> 4;
                long i2 = ChunkCoordIntPair.a(k1, l1);
                BiomeBase biomebase = (BiomeBase) long2objectmap.get(i2);

                if (biomebase == null) {
                    biomebase = this.a(k1, l1);
                    long2objectmap.put(i2, biomebase);
                }

                abiomebase[i1 + j1 * k] = biomebase;
            }
        }

        return abiomebase;
    }

    public Set<BiomeBase> a(int i, int j, int k) {
        int l = i - k >> 2;
        int i1 = j - k >> 2;
        int j1 = i + k >> 2;
        int k1 = j + k >> 2;
        int l1 = j1 - l + 1;
        int i2 = k1 - i1 + 1;

        return Sets.newHashSet(this.getBiomeBlock(l, i1, l1, i2));
    }

    @Nullable
    public BlockPosition a(int i, int j, int k, List<BiomeBase> list, Random random) {
        int l = i - k >> 2;
        int i1 = j - k >> 2;
        int j1 = i + k >> 2;
        int k1 = j + k >> 2;
        int l1 = j1 - l + 1;
        int i2 = k1 - i1 + 1;
        BiomeBase[] abiomebase = this.getBiomeBlock(l, i1, l1, i2);
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

    public float c(int i, int j, int k, int l) {
        float f = (float) (i * 2 + k);
        float f1 = (float) (j * 2 + l);
        float f2 = 100.0F - MathHelper.c(f * f + f1 * f1) * 8.0F;

        f2 = MathHelper.a(f2, -100.0F, 80.0F);

        for (int i1 = -12; i1 <= 12; ++i1) {
            for (int j1 = -12; j1 <= 12; ++j1) {
                long k1 = (long) (i + i1);
                long l1 = (long) (j + j1);

                if (k1 * k1 + l1 * l1 > 4096L && this.c.a((double) k1, (double) l1) < -0.8999999761581421D) {
                    float f3 = (MathHelper.e((float) k1) * 3439.0F + MathHelper.e((float) l1) * 147.0F) % 13.0F + 9.0F;

                    f = (float) (k - i1 * 2);
                    f1 = (float) (l - j1 * 2);
                    float f4 = 100.0F - MathHelper.c(f * f + f1 * f1) * f3;

                    f4 = MathHelper.a(f4, -100.0F, 80.0F);
                    f2 = Math.max(f2, f4);
                }
            }
        }

        return f2;
    }

    public boolean a(StructureGenerator<?> structuregenerator) {
        return (Boolean) this.a.computeIfAbsent(structuregenerator, (structuregenerator1) -> {
            BiomeBase[] abiomebase = this.e;
            int i = abiomebase.length;

            for (int j = 0; j < i; ++j) {
                BiomeBase biomebase = abiomebase[j];

                if (biomebase.a(structuregenerator1)) {
                    return true;
                }
            }

            return false;
        });
    }

    public Set<IBlockData> b() {
        if (this.b.isEmpty()) {
            BiomeBase[] abiomebase = this.e;
            int i = abiomebase.length;

            for (int j = 0; j < i; ++j) {
                BiomeBase biomebase = abiomebase[j];

                this.b.add(biomebase.r().a());
            }
        }

        return this.b;
    }
}
