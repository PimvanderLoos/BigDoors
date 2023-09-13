package net.minecraft.world.level.levelgen;

public interface RandomSource {

    void setSeed(long i);

    int nextInt();

    int nextInt(int i);

    long nextLong();

    boolean nextBoolean();

    float nextFloat();

    double nextDouble();

    double nextGaussian();

    default void a(int i) {
        for (int j = 0; j < i; ++j) {
            this.nextInt();
        }

    }
}
