package net.minecraft.world.level.biome;

import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.MathHelper;

public class BiomeManager {

    public static final int CHUNK_CENTER_QUART = QuartPos.fromBlock(8);
    private static final int ZOOM_BITS = 2;
    private static final int ZOOM = 4;
    private static final int ZOOM_MASK = 3;
    private final BiomeManager.Provider noiseBiomeSource;
    private final long biomeZoomSeed;

    public BiomeManager(BiomeManager.Provider biomemanager_provider, long i) {
        this.noiseBiomeSource = biomemanager_provider;
        this.biomeZoomSeed = i;
    }

    public static long obfuscateSeed(long i) {
        return Hashing.sha256().hashLong(i).asLong();
    }

    public BiomeManager withDifferentSource(BiomeManager.Provider biomemanager_provider) {
        return new BiomeManager(biomemanager_provider, this.biomeZoomSeed);
    }

    public Holder<BiomeBase> getBiome(BlockPosition blockposition) {
        int i = blockposition.getX() - 2;
        int j = blockposition.getY() - 2;
        int k = blockposition.getZ() - 2;
        int l = i >> 2;
        int i1 = j >> 2;
        int j1 = k >> 2;
        double d0 = (double) (i & 3) / 4.0D;
        double d1 = (double) (j & 3) / 4.0D;
        double d2 = (double) (k & 3) / 4.0D;
        int k1 = 0;
        double d3 = Double.POSITIVE_INFINITY;

        int l1;

        for (l1 = 0; l1 < 8; ++l1) {
            boolean flag = (l1 & 4) == 0;
            boolean flag1 = (l1 & 2) == 0;
            boolean flag2 = (l1 & 1) == 0;
            int i2 = flag ? l : l + 1;
            int j2 = flag1 ? i1 : i1 + 1;
            int k2 = flag2 ? j1 : j1 + 1;
            double d4 = flag ? d0 : d0 - 1.0D;
            double d5 = flag1 ? d1 : d1 - 1.0D;
            double d6 = flag2 ? d2 : d2 - 1.0D;
            double d7 = getFiddledDistance(this.biomeZoomSeed, i2, j2, k2, d4, d5, d6);

            if (d3 > d7) {
                k1 = l1;
                d3 = d7;
            }
        }

        l1 = (k1 & 4) == 0 ? l : l + 1;
        int l2 = (k1 & 2) == 0 ? i1 : i1 + 1;
        int i3 = (k1 & 1) == 0 ? j1 : j1 + 1;

        return this.noiseBiomeSource.getNoiseBiome(l1, l2, i3);
    }

    public Holder<BiomeBase> getNoiseBiomeAtPosition(double d0, double d1, double d2) {
        int i = QuartPos.fromBlock(MathHelper.floor(d0));
        int j = QuartPos.fromBlock(MathHelper.floor(d1));
        int k = QuartPos.fromBlock(MathHelper.floor(d2));

        return this.getNoiseBiomeAtQuart(i, j, k);
    }

    public Holder<BiomeBase> getNoiseBiomeAtPosition(BlockPosition blockposition) {
        int i = QuartPos.fromBlock(blockposition.getX());
        int j = QuartPos.fromBlock(blockposition.getY());
        int k = QuartPos.fromBlock(blockposition.getZ());

        return this.getNoiseBiomeAtQuart(i, j, k);
    }

    public Holder<BiomeBase> getNoiseBiomeAtQuart(int i, int j, int k) {
        return this.noiseBiomeSource.getNoiseBiome(i, j, k);
    }

    private static double getFiddledDistance(long i, int j, int k, int l, double d0, double d1, double d2) {
        long i1 = LinearCongruentialGenerator.next(i, (long) j);

        i1 = LinearCongruentialGenerator.next(i1, (long) k);
        i1 = LinearCongruentialGenerator.next(i1, (long) l);
        i1 = LinearCongruentialGenerator.next(i1, (long) j);
        i1 = LinearCongruentialGenerator.next(i1, (long) k);
        i1 = LinearCongruentialGenerator.next(i1, (long) l);
        double d3 = getFiddle(i1);

        i1 = LinearCongruentialGenerator.next(i1, i);
        double d4 = getFiddle(i1);

        i1 = LinearCongruentialGenerator.next(i1, i);
        double d5 = getFiddle(i1);

        return MathHelper.square(d2 + d5) + MathHelper.square(d1 + d4) + MathHelper.square(d0 + d3);
    }

    private static double getFiddle(long i) {
        double d0 = (double) Math.floorMod(i >> 24, 1024) / 1024.0D;

        return (d0 - 0.5D) * 0.9D;
    }

    public interface Provider {

        Holder<BiomeBase> getNoiseBiome(int i, int j, int k);
    }
}
