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

public record ThreadAllocationStat(Instant timestamp, String threadName, long totalBytes) {

    private static final String UNKNOWN_THREAD = "unknown";

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

    public static record a(Map<String, Double> allocationsPerSecondByThread) {

    }
}
