package net.minecraft.world.level.levelgen;

import java.util.function.LongFunction;
import net.minecraft.util.RandomSource;

public class SeededRandom extends LegacyRandomSource {

    private final RandomSource randomSource;
    private int count;

    public SeededRandom(RandomSource randomsource) {
        super(0L);
        this.randomSource = randomsource;
    }

    public int getCount() {
        return this.count;
    }

    @Override
    public RandomSource fork() {
        return this.randomSource.fork();
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return this.randomSource.forkPositional();
    }

    @Override
    public int next(int i) {
        ++this.count;
        RandomSource randomsource = this.randomSource;

        if (randomsource instanceof LegacyRandomSource) {
            LegacyRandomSource legacyrandomsource = (LegacyRandomSource) randomsource;

            return legacyrandomsource.next(i);
        } else {
            return (int) (this.randomSource.nextLong() >>> 64 - i);
        }
    }

    @Override
    public synchronized void setSeed(long i) {
        if (this.randomSource != null) {
            this.randomSource.setSeed(i);
        }
    }

    public long setDecorationSeed(long i, int j, int k) {
        this.setSeed(i);
        long l = this.nextLong() | 1L;
        long i1 = this.nextLong() | 1L;
        long j1 = (long) j * l + (long) k * i1 ^ i;

        this.setSeed(j1);
        return j1;
    }

    public void setFeatureSeed(long i, int j, int k) {
        long l = i + (long) j + (long) (10000 * k);

        this.setSeed(l);
    }

    public void setLargeFeatureSeed(long i, int j, int k) {
        this.setSeed(i);
        long l = this.nextLong();
        long i1 = this.nextLong();
        long j1 = (long) j * l ^ (long) k * i1 ^ i;

        this.setSeed(j1);
    }

    public void setLargeFeatureWithSalt(long i, int j, int k, int l) {
        long i1 = (long) j * 341873128712L + (long) k * 132897987541L + i + (long) l;

        this.setSeed(i1);
    }

    public static RandomSource seedSlimeChunk(int i, int j, long k, long l) {
        return RandomSource.create(k + (long) (i * i * 4987142) + (long) (i * 5947611) + (long) (j * j) * 4392871L + (long) (j * 389711) ^ l);
    }

    public static enum a {

        LEGACY(LegacyRandomSource::new), XOROSHIRO(XoroshiroRandomSource::new);

        private final LongFunction<RandomSource> constructor;

        private a(LongFunction longfunction) {
            this.constructor = longfunction;
        }

        public RandomSource newInstance(long i) {
            return (RandomSource) this.constructor.apply(i);
        }
    }
}
