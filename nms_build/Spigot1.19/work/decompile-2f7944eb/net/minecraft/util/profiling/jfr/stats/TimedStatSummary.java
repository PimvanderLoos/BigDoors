package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.Percentiles;

public record TimedStatSummary<T extends TimedStat> (T fastest, T slowest, @Nullable T secondSlowest, int count, Map<Integer, Double> percentilesNanos, Duration totalDuration) {

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
}
