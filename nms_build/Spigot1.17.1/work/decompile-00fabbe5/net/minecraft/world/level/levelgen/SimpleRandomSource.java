package net.minecraft.world.level.levelgen;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.DebugBuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ThreadingDetector;

public class SimpleRandomSource implements RandomSource {

    private static final int MODULUS_BITS = 48;
    private static final long MODULUS_MASK = 281474976710655L;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private static final float FLOAT_MULTIPLIER = 5.9604645E-8F;
    private static final double DOUBLE_MULTIPLIER = 1.1102230246251565E-16D;
    private final AtomicLong seed = new AtomicLong();
    private double nextNextGaussian;
    private boolean haveNextNextGaussian;

    public SimpleRandomSource(long i) {
        this.setSeed(i);
    }

    @Override
    public void setSeed(long i) {
        if (!this.seed.compareAndSet(this.seed.get(), (i ^ 25214903917L) & 281474976710655L)) {
            throw ThreadingDetector.a("SimpleRandomSource", (DebugBuffer) null);
        }
    }

    private int b(int i) {
        long j = this.seed.get();
        long k = j * 25214903917L + 11L & 281474976710655L;

        if (!this.seed.compareAndSet(j, k)) {
            throw ThreadingDetector.a("SimpleRandomSource", (DebugBuffer) null);
        } else {
            return (int) (k >> 48 - i);
        }
    }

    @Override
    public int nextInt() {
        return this.b(32);
    }

    @Override
    public int nextInt(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        } else if ((i & i - 1) == 0) {
            return (int) ((long) i * (long) this.b(31) >> 31);
        } else {
            int j;
            int k;

            do {
                j = this.b(31);
                k = j % i;
            } while (j - k + (i - 1) < 0);

            return k;
        }
    }

    @Override
    public long nextLong() {
        int i = this.b(32);
        int j = this.b(32);
        long k = (long) i << 32;

        return k + (long) j;
    }

    @Override
    public boolean nextBoolean() {
        return this.b(1) != 0;
    }

    @Override
    public float nextFloat() {
        return (float) this.b(24) * 5.9604645E-8F;
    }

    @Override
    public double nextDouble() {
        int i = this.b(26);
        int j = this.b(27);
        long k = ((long) i << 27) + (long) j;

        return (double) k * 1.1102230246251565E-16D;
    }

    @Override
    public double nextGaussian() {
        if (this.haveNextNextGaussian) {
            this.haveNextNextGaussian = false;
            return this.nextNextGaussian;
        } else {
            double d0;
            double d1;
            double d2;

            do {
                do {
                    d0 = 2.0D * this.nextDouble() - 1.0D;
                    d1 = 2.0D * this.nextDouble() - 1.0D;
                    d2 = MathHelper.m(d0) + MathHelper.m(d1);
                } while (d2 >= 1.0D);
            } while (d2 == 0.0D);

            double d3 = Math.sqrt(-2.0D * Math.log(d2) / d2);

            this.nextNextGaussian = d1 * d3;
            this.haveNextNextGaussian = true;
            return d0 * d3;
        }
    }
}
