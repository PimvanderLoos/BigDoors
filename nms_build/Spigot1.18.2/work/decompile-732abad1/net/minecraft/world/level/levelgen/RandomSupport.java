package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSupport {

    public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
    public static final long SILVER_RATIO_64 = 7640891576956012809L;
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    public RandomSupport() {}

    @VisibleForTesting
    public static long mixStafford13(long i) {
        i = (i ^ i >>> 30) * -4658895280553007687L;
        i = (i ^ i >>> 27) * -7723592293110705685L;
        return i ^ i >>> 31;
    }

    public static RandomSupport.a upgradeSeedTo128bit(long i) {
        long j = i ^ 7640891576956012809L;
        long k = j + -7046029254386353131L;

        return new RandomSupport.a(mixStafford13(j), mixStafford13(k));
    }

    public static long seedUniquifier() {
        return RandomSupport.SEED_UNIQUIFIER.updateAndGet((i) -> {
            return i * 1181783497276652981L;
        }) ^ System.nanoTime();
    }

    public static record a(long a, long b) {

        private final long seedLo;
        private final long seedHi;

        public a(long i, long j) {
            this.seedLo = i;
            this.seedHi = j;
        }

        public long seedLo() {
            return this.seedLo;
        }

        public long seedHi() {
            return this.seedHi;
        }
    }
}
