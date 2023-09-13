package net.minecraft.server;

public class GenLayerRiverMix extends GenLayer {

    private final GenLayer c;
    private final GenLayer d;

    public GenLayerRiverMix(long i, GenLayer genlayer, GenLayer genlayer1) {
        super(i);
        this.c = genlayer;
        this.d = genlayer1;
    }

    public void a(long i) {
        this.c.a(i);
        this.d.a(i);
        super.a(i);
    }

    public int[] a(int i, int j, int k, int l) {
        int[] aint = this.c.a(i, j, k, l);
        int[] aint1 = this.d.a(i, j, k, l);
        int[] aint2 = IntCache.a(k * l);

        for (int i1 = 0; i1 < k * l; ++i1) {
            if (aint[i1] != BiomeBase.a(Biomes.a) && aint[i1] != BiomeBase.a(Biomes.z)) {
                if (aint1[i1] == BiomeBase.a(Biomes.i)) {
                    if (aint[i1] == BiomeBase.a(Biomes.n)) {
                        aint2[i1] = BiomeBase.a(Biomes.m);
                    } else if (aint[i1] != BiomeBase.a(Biomes.p) && aint[i1] != BiomeBase.a(Biomes.q)) {
                        aint2[i1] = aint1[i1] & 255;
                    } else {
                        aint2[i1] = BiomeBase.a(Biomes.q);
                    }
                } else {
                    aint2[i1] = aint[i1];
                }
            } else {
                aint2[i1] = aint[i1];
            }
        }

        return aint2;
    }
}
