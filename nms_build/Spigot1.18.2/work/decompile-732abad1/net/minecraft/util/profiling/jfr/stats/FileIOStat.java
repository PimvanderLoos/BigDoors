package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public record FileIOStat(Duration a, @Nullable String b, long c) {

    private final Duration duration;
    @Nullable
    private final String path;
    private final long bytes;

    public FileIOStat(Duration duration, @Nullable String s, long i) {
        this.duration = duration;
        this.path = s;
        this.bytes = i;
    }

    public static FileIOStat.a summary(Duration duration, List<FileIOStat> list) {
        long i = list.stream().mapToLong((fileiostat) -> {
            return fileiostat.bytes;
        }).sum();

        return new FileIOStat.a(i, (double) i / (double) duration.getSeconds(), (long) list.size(), (double) list.size() / (double) duration.getSeconds(), (Duration) list.stream().map(FileIOStat::duration).reduce(Duration.ZERO, Duration::plus), ((Map) list.stream().filter((fileiostat) -> {
            return fileiostat.path != null;
        }).collect(Collectors.groupingBy((fileiostat) -> {
            return fileiostat.path;
        }, Collectors.summingLong((fileiostat) -> {
            return fileiostat.bytes;
        })))).entrySet().stream().sorted(Entry.comparingByValue().reversed()).map((entry) -> {
            return Pair.of((String) entry.getKey(), (Long) entry.getValue());
        }).limit(10L).toList());
    }

    public Duration duration() {
        return this.duration;
    }

    @Nullable
    public String path() {
        return this.path;
    }

    public long bytes() {
        return this.bytes;
    }

    public static record a(long a, double b, long c, double d, Duration e, List<Pair<String, Long>> f) {

        private final long totalBytes;
        private final double bytesPerSecond;
        private final long counts;
        private final double countsPerSecond;
        private final Duration timeSpentInIO;
        private final List<Pair<String, Long>> topTenContributorsByTotalBytes;

        public a(long i, double d0, long j, double d1, Duration duration, List<Pair<String, Long>> list) {
            this.totalBytes = i;
            this.bytesPerSecond = d0;
            this.counts = j;
            this.countsPerSecond = d1;
            this.timeSpentInIO = duration;
            this.topTenContributorsByTotalBytes = list;
        }

        public long totalBytes() {
            return this.totalBytes;
        }

        public double bytesPerSecond() {
            return this.bytesPerSecond;
        }

        public long counts() {
            return this.counts;
        }

        public double countsPerSecond() {
            return this.countsPerSecond;
        }

        public Duration timeSpentInIO() {
            return this.timeSpentInIO;
        }

        public List<Pair<String, Long>> topTenContributorsByTotalBytes() {
            return this.topTenContributorsByTotalBytes;
        }
    }
}
