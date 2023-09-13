package net.minecraft.server;

import java.util.Random;

public class WorldGenBase {

    protected int e = 8;
    protected Random f = new Random();
    protected World g;

    public WorldGenBase() {}

    public void a(World world, int i, int j, ChunkSnapshot chunksnapshot) {
        int k = this.e;

        this.g = world;
        this.f.setSeed(world.getSeed());
        long l = this.f.nextLong();
        long i1 = this.f.nextLong();

        for (int j1 = i - k; j1 <= i + k; ++j1) {
            for (int k1 = j - k; k1 <= j + k; ++k1) {
                long l1 = (long) j1 * l;
                long i2 = (long) k1 * i1;

                this.f.setSeed(l1 ^ i2 ^ world.getSeed());
                this.a(world, j1, k1, i, j, chunksnapshot);
            }
        }

    }

    public static void a(long i, Random random, int j, int k) {
        random.setSeed(i);
        long l = random.nextLong();
        long i1 = random.nextLong();
        long j1 = (long) j * l;
        long k1 = (long) k * i1;

        random.setSeed(j1 ^ k1 ^ i);
    }

    protected void a(World world, int i, int j, int k, int l, ChunkSnapshot chunksnapshot) {}
}
