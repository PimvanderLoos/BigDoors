package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import net.minecraft.util.MathHelper;

public class XoroshiroRandomSource implements RandomSource {

    private static final float FLOAT_UNIT = 5.9604645E-8F;
    private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
    private Xoroshiro128PlusPlus randomNumberGenerator;
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public XoroshiroRandomSource(long i) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(i));
    }

    public XoroshiroRandomSource(long i, long j) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(i, j);
    }

    @Override
    public RandomSource fork() {
        return new XoroshiroRandomSource(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new XoroshiroRandomSource.a(this.randomNumberGenerator.nextLong(), this.randomNumberGenerator.nextLong());
    }

    @Override
    public void setSeed(long i) {
        this.randomNumberGenerator = new Xoroshiro128PlusPlus(RandomSupport.upgradeSeedTo128bit(i));
        this.gaussianSource.reset();
    }

    @Override
    public int nextInt() {
        return (int) this.randomNumberGenerator.nextLong();
    }

    @Override
    public int nextInt(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        } else {
            long j = Integer.toUnsignedLong(this.nextInt());
            long k = j * (long) i;
            long l = k & 4294967295L;

            if (l < (long) i) {
                for (int i1 = Integer.remainderUnsigned(~i + 1, i); l < (long) i1; l = k & 4294967295L) {
                    j = Integer.toUnsignedLong(this.nextInt());
                    k = j * (long) i;
                }
            }

            long j1 = k >> 32;

            return (int) j1;
        }
    }

    @Override
    public long nextLong() {
        return this.randomNumberGenerator.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return (this.randomNumberGenerator.nextLong() & 1L) != 0L;
    }

    @Override
    public float nextFloat() {
        return (float) this.nextBits(24) * 5.9604645E-8F;
    }

    @Override
    public double nextDouble() {
        return (double) this.nextBits(53) * 1.1102230246251565E-16D;
    }

    @Override
    public double nextGaussian() {
        return this.gaussianSource.nextGaussian();
    }

    @Override
    public void consumeCount(int i) {
        for (int j = 0; j < i; ++j) {
            this.randomNumberGenerator.nextLong();
        }

    }

    private long nextBits(int i) {
        return this.randomNumberGenerator.nextLong() >>> 64 - i;
    }

    public static class a implements PositionalRandomFactory {

        private static final HashFunction MD5_128 = Hashing.md5();
        private final long seedLo;
        private final long seedHi;

        public a(long i, long j) {
            this.seedLo = i;
            this.seedHi = j;
        }

        @Override
        public RandomSource at(int i, int j, int k) {
            long l = MathHelper.getSeed(i, j, k);
            long i1 = l ^ this.seedLo;

            return new XoroshiroRandomSource(i1, this.seedHi);
        }

        @Override
        public RandomSource fromHashOf(String s) {
            byte[] abyte = XoroshiroRandomSource.a.MD5_128.hashString(s, Charsets.UTF_8).asBytes();
            long i = Longs.fromBytes(abyte[0], abyte[1], abyte[2], abyte[3], abyte[4], abyte[5], abyte[6], abyte[7]);
            long j = Longs.fromBytes(abyte[8], abyte[9], abyte[10], abyte[11], abyte[12], abyte[13], abyte[14], abyte[15]);

            return new XoroshiroRandomSource(i ^ this.seedLo, j ^ this.seedHi);
        }

        @VisibleForTesting
        @Override
        public void parityConfigString(StringBuilder stringbuilder) {
            stringbuilder.append("seedLo: ").append(this.seedLo).append(", seedHi: ").append(this.seedHi);
        }
    }
}
