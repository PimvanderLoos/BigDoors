package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapStat(Instant timestamp, long heapUsed, GcHeapStat.b timing) {

    public static GcHeapStat from(RecordedEvent recordedevent) {
        return new GcHeapStat(recordedevent.getStartTime(), recordedevent.getLong("heapUsed"), recordedevent.getString("when").equalsIgnoreCase("before gc") ? GcHeapStat.b.BEFORE_GC : GcHeapStat.b.AFTER_GC);
    }

    public static GcHeapStat.a summary(Duration duration, List<GcHeapStat> list, Duration duration1, int i) {
        return new GcHeapStat.a(duration, duration1, i, calculateAllocationRatePerSecond(list));
    }

    private static double calculateAllocationRatePerSecond(List<GcHeapStat> list) {
        long i = 0L;
        Map<GcHeapStat.b, List<GcHeapStat>> map = (Map) list.stream().collect(Collectors.groupingBy((gcheapstat) -> {
            return gcheapstat.timing;
        }));
        List<GcHeapStat> list1 = (List) map.get(GcHeapStat.b.BEFORE_GC);
        List<GcHeapStat> list2 = (List) map.get(GcHeapStat.b.AFTER_GC);

        for (int j = 1; j < list1.size(); ++j) {
            GcHeapStat gcheapstat = (GcHeapStat) list1.get(j);
            GcHeapStat gcheapstat1 = (GcHeapStat) list2.get(j - 1);

            i += gcheapstat.heapUsed - gcheapstat1.heapUsed;
        }

        Duration duration = Duration.between(((GcHeapStat) list.get(1)).timestamp, ((GcHeapStat) list.get(list.size() - 1)).timestamp);

        return (double) i / (double) duration.getSeconds();
    }

    static enum b {

        BEFORE_GC, AFTER_GC;

        private b() {}
    }

    public static record a(Duration duration, Duration gcTotalDuration, int totalGCs, double allocationRateBytesPerSecond) {

        public float gcOverHead() {
            return (float) this.gcTotalDuration.toMillis() / (float) this.duration.toMillis();
        }
    }
}
