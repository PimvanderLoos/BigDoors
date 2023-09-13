package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.ThreadingDetector;

public class LegacyRandomSource implements BitRandomSource {

    private static final int MODULUS_BITS = 48;
    private static final long MODULUS_MASK = 281474976710655L;
    private static final long MULTIPLIER = 25214903917L;
    private static final long INCREMENT = 11L;
    private final AtomicLong seed = new AtomicLong();
    private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

    public LegacyRandomSource(long i) {
        this.setSeed(i);
    }

    @Override
    public RandomSource fork() {
        return new LegacyRandomSource(this.nextLong());
    }

    @Override
    public PositionalRandomFactory forkPositional() {
        return new LegacyRandomSource.a(this.nextLong());
    }

    @Override
    public void setSeed(long i) {
        if (!this.seed.compareAndSet(this.seed.get(), (i ^ 25214903917L) & 281474976710655L)) {
            throw ThreadingDetector.makeThreadingException("LegacyRandomSource", (Thread) null);
        } else {
            this.gaussianSource.reset();
        }
    }

    @Override
    public int next(int i) {
        long j = this.seed.get();
        long k = j * 25214903917L + 11L & 281474976710655L;

        if (!this.seed.compareAndSet(j, k)) {
            throw ThreadingDetector.makeThreadingException("LegacyRandomSource", (Thread) null);
        } else {
            return (int) (k >> 48 - i);
        }
    }

    @Override
    public double nextGaussian() {
        return this.gaussianSource.nextGaussian();
    }

    public static class a implements PositionalRandomFactory {

        private final long seed;

        public a(long i) {
            this.seed = i;
        }

        @Override
        public RandomSource at(int i, int j, int k) {
            long l = MathHelper.getSeed(i, j, k);
            long i1 = l ^ this.seed;

            return new LegacyRandomSource(i1);
        }

        @Override
        public RandomSource fromHashOf(String s) {
            int i = s.hashCode();

            return new LegacyRandomSource((long) i ^ this.seed);
        }

        @VisibleForTesting
        @Override
        public void parityConfigString(StringBuilder stringbuilder) {
            stringbuilder.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
        }
    }
}
