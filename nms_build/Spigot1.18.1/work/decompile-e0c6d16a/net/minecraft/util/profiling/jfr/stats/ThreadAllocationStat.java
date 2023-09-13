package net.minecraft.util.profiling.jfr.stats;

import com.google.common.base.MoreObjects;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

public record ThreadAllocationStat(Instant a, String b, long c) {

    private final Instant timestamp;
    private final String threadName;
    private final long totalBytes;
    private static final String UNKNOWN_THREAD = "unknown";

    public ThreadAllocationStat(Instant instant, String s, long i) {
        this.timestamp = instant;
        this.threadName = s;
        this.totalBytes = i;
    }

    public static ThreadAllocationStat from(RecordedEvent recordedevent) {
        RecordedThread recordedthread = recordedevent.getThread("thread");
        String s = recordedthread == null ? "unknown" : (String) MoreObjects.firstNonNull(recordedthread.getJavaName(), "unknown");

        return new ThreadAllocationStat(recordedevent.getStartTime(), s, recordedevent.getLong("allocated"));
    }

    public static ThreadAllocationStat.a summary(List<ThreadAllocationStat> list) {
        Map<String, Double> map = new TreeMap();
        Map<String, List<ThreadAllocationStat>> map1 = (Map) list.stream().collect(Collectors.groupingBy((threadallocationstat) -> {
            return threadallocationstat.threadName;
        }));

        map1.forEach((s, list1) -> {
            if (list1.size() >= 2) {
                ThreadAllocationStat threadallocationstat = (ThreadAllocationStat) list1.get(0);
                ThreadAllocationStat threadallocationstat1 = (ThreadAllocationStat) list1.get(list1.size() - 1);
                long i = Duration.between(threadallocationstat.timestamp, threadallocationstat1.timestamp).getSeconds();
                long j = threadallocationstat1.totalBytes - threadallocationstat.totalBytes;

                map.put(s, (double) j / (double) i);
            }
        });
        return new ThreadAllocationStat.a(map);
    }

    public Instant timestamp() {
        return this.timestamp;
    }

    public String threadName() {
        return this.threadName;
    }

    public long totalBytes() {
        return this.totalBytes;
    }

    public static record a(Map<String, Double> a) {

        private final Map<String, Double> allocationsPerSecondByThread;

        public a(Map<String, Double> map) {
            this.allocationsPerSecondByThread = map;
        }

        public Map<String, Double> allocationsPerSecondByThread() {
            return this.allocationsPerSecondByThread;
        }
    }
}
