package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapStat(Instant a, long b, GcHeapStat.b c) {

    private final Instant timestamp;
    private final long heapUsed;
    private final GcHeapStat.b timing;

    public GcHeapStat(Instant instant, long i, GcHeapStat.b gcheapstat_b) {
        this.timestamp = instant;
        this.heapUsed = i;
        this.timing = gcheapstat_b;
    }

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

    public Instant timestamp() {
        return this.timestamp;
    }

    public long heapUsed() {
        return this.heapUsed;
    }

    public GcHeapStat.b timing() {
        return this.timing;
    }

    static enum b {

        BEFORE_GC, AFTER_GC;

        private b() {}
    }

    public static record a(Duration a, Duration b, int c, double d) {

        private final Duration duration;
        private final Duration gcTotalDuration;
        private final int totalGCs;
        private final double allocationRateBytesPerSecond;

        public a(Duration duration, Duration duration1, int i, double d0) {
            this.duration = duration;
            this.gcTotalDuration = duration1;
            this.totalGCs = i;
            this.allocationRateBytesPerSecond = d0;
        }

        public float gcOverHead() {
            return (float) this.gcTotalDuration.toMillis() / (float) this.duration.toMillis();
        }

        public Duration duration() {
            return this.duration;
        }

        public Duration gcTotalDuration() {
            return this.gcTotalDuration;
        }

        public int totalGCs() {
            return this.totalGCs;
        }

        public double allocationRateBytesPerSecond() {
            return this.allocationRateBytesPerSecond;
        }
    }
}
