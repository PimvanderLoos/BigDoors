package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.Percentiles;

public record TimedStatSummary<T extends TimedStat> (T a, T b, @Nullable T c, int d, Map<Integer, Double> e, Duration f) {

    private final T fastest;
    private final T slowest;
    @Nullable
    private final T secondSlowest;
    private final int count;
    private final Map<Integer, Double> percentilesNanos;
    private final Duration totalDuration;

    public TimedStatSummary(T t0, T t1, @Nullable T t2, int i, Map<Integer, Double> map, Duration duration) {
        this.fastest = t0;
        this.slowest = t1;
        this.secondSlowest = t2;
        this.count = i;
        this.percentilesNanos = map;
        this.totalDuration = duration;
    }

    public static <T extends TimedStat> TimedStatSummary<T> summary(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No values");
        } else {
            List<T> list1 = list.stream().sorted(Comparator.comparing(TimedStat::duration)).toList();
            Duration duration = (Duration) list1.stream().map(TimedStat::duration).reduce(Duration::plus).orElse(Duration.ZERO);
            T t0 = (TimedStat) list1.get(0);
            T t1 = (TimedStat) list1.get(list1.size() - 1);
            T t2 = list1.size() > 1 ? (TimedStat) list1.get(list1.size() - 2) : null;
            int i = list1.size();
            Map<Integer, Double> map = Percentiles.evaluate(list1.stream().mapToLong((timedstat) -> {
                return timedstat.duration().toNanos();
            }).toArray());

            return new TimedStatSummary<>(t0, t1, t2, i, map, duration);
        }
    }

    public T fastest() {
        return this.fastest;
    }

    public T slowest() {
        return this.slowest;
    }

    @Nullable
    public T secondSlowest() {
        return this.secondSlowest;
    }

    public int count() {
        return this.count;
    }

    public Map<Integer, Double> percentilesNanos() {
        return this.percentilesNanos;
    }

    public Duration totalDuration() {
        return this.totalDuration;
    }
}
