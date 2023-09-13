package net.minecraft.server;

import java.util.Random;

public abstract class WorldGenContextLayer<R extends Area> implements AreaContextTransformed<R> {

    private long c;
    private long d;
    protected long a;
    protected NoiseGeneratorPerlin b;

    public WorldGenContextLayer(long i) {
        this.a = i;
        this.a *= this.a * 6364136223846793005L + 1442695040888963407L;
        this.a += i;
        this.a *= this.a * 6364136223846793005L + 1442695040888963407L;
        this.a += i;
        this.a *= this.a * 6364136223846793005L + 1442695040888963407L;
        this.a += i;
    }

    public void a(long i) {
        this.c = i;
        this.c *= this.c * 6364136223846793005L + 1442695040888963407L;
        this.c += this.a;
        this.c *= this.c * 6364136223846793005L + 1442695040888963407L;
        this.c += this.a;
        this.c *= this.c * 6364136223846793005L + 1442695040888963407L;
        this.c += this.a;
        this.b = new NoiseGeneratorPerlin(new Random(i));
    }

    public void a(long i, long j) {
        this.d = this.c;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += i;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += j;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += i;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += j;
    }

    public int a(int i) {
        int j = (int) ((this.d >> 24) % (long) i);

        if (j < 0) {
            j += i;
        }

        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += this.c;
        return j;
    }

    public int a(int... aint) {
        return aint[this.a(aint.length)];
    }

    public NoiseGeneratorPerlin a() {
        return this.b;
    }
}
