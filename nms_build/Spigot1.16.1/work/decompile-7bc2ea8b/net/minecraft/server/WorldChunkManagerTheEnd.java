package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;

public class WorldChunkManagerTheEnd extends WorldChunkManager {

    public static final Codec<WorldChunkManagerTheEnd> e = Codec.LONG.fieldOf("seed").xmap(WorldChunkManagerTheEnd::new, (worldchunkmanagertheend) -> {
        return worldchunkmanagertheend.h;
    }).stable().codec();
    private final NoiseGenerator3Handler f;
    private static final List<BiomeBase> g = ImmutableList.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);
    private final long h;

    public WorldChunkManagerTheEnd(long i) {
        super(WorldChunkManagerTheEnd.g);
        this.h = i;
        SeededRandom seededrandom = new SeededRandom(i);

        seededrandom.a(17292);
        this.f = new NoiseGenerator3Handler(seededrandom);
    }

    @Override
    protected Codec<? extends WorldChunkManager> a() {
        return WorldChunkManagerTheEnd.e;
    }

    @Override
    public BiomeBase getBiome(int i, int j, int k) {
        int l = i >> 2;
        int i1 = k >> 2;

        if ((long) l * (long) l + (long) i1 * (long) i1 <= 4096L) {
            return Biomes.THE_END;
        } else {
            float f = a(this.f, l * 2 + 1, i1 * 2 + 1);

            return f > 40.0F ? Biomes.END_HIGHLANDS : (f >= 0.0F ? Biomes.END_MIDLANDS : (f < -20.0F ? Biomes.SMALL_END_ISLANDS : Biomes.END_BARRENS));
        }
    }

    public boolean b(long i) {
        return this.h == i;
    }

    public static float a(NoiseGenerator3Handler noisegenerator3handler, int i, int j) {
        int k = i / 2;
        int l = j / 2;
        int i1 = i % 2;
        int j1 = j % 2;
        float f = 100.0F - MathHelper.c((float) (i * i + j * j)) * 8.0F;

        f = MathHelper.a(f, -100.0F, 80.0F);

        for (int k1 = -12; k1 <= 12; ++k1) {
            for (int l1 = -12; l1 <= 12; ++l1) {
                long i2 = (long) (k + k1);
                long j2 = (long) (l + l1);

                if (i2 * i2 + j2 * j2 > 4096L && noisegenerator3handler.a((double) i2, (double) j2) < -0.8999999761581421D) {
                    float f1 = (MathHelper.e((float) i2) * 3439.0F + MathHelper.e((float) j2) * 147.0F) % 13.0F + 9.0F;
                    float f2 = (float) (i1 - k1 * 2);
                    float f3 = (float) (j1 - l1 * 2);
                    float f4 = 100.0F - MathHelper.c(f2 * f2 + f3 * f3) * f1;

                    f4 = MathHelper.a(f4, -100.0F, 80.0F);
                    f = Math.max(f, f4);
                }
            }
        }

        return f;
    }
}
