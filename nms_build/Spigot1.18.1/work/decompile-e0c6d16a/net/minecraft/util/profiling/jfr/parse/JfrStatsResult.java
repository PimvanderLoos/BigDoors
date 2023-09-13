package net.minecraft.util.profiling.jfr.parse;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.serialize.JfrResultJsonSerializer;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import net.minecraft.util.profiling.jfr.stats.TimedStatSummary;
import net.minecraft.world.level.chunk.ChunkStatus;

public record JfrStatsResult(Instant a, Instant b, Duration c, @Nullable Duration d, List<TickTimeStat> e, List<CpuLoadStat> f, GcHeapStat.a g, ThreadAllocationStat.a h, NetworkPacketSummary i, NetworkPacketSummary j, FileIOStat.a k, FileIOStat.a l, List<ChunkGenStat> m) {

    private final Instant recordingStarted;
    private final Instant recordingEnded;
    private final Duration recordingDuration;
    @Nullable
    private final Duration worldCreationDuration;
    private final List<TickTimeStat> tickTimes;
    private final List<CpuLoadStat> cpuLoadStats;
    private final GcHeapStat.a heapSummary;
    private final ThreadAllocationStat.a threadAllocationSummary;
    private final NetworkPacketSummary receivedPacketsSummary;
    private final NetworkPacketSummary sentPacketsSummary;
    private final FileIOStat.a fileWrites;
    private final FileIOStat.a fileReads;
    private final List<ChunkGenStat> chunkGenStats;

    public JfrStatsResult(Instant instant, Instant instant1, Duration duration, @Nullable Duration duration1, List<TickTimeStat> list, List<CpuLoadStat> list1, GcHeapStat.a gcheapstat_a, ThreadAllocationStat.a threadallocationstat_a, NetworkPacketSummary networkpacketsummary, NetworkPacketSummary networkpacketsummary1, FileIOStat.a fileiostat_a, FileIOStat.a fileiostat_a1, List<ChunkGenStat> list2) {
        this.recordingStarted = instant;
        this.recordingEnded = instant1;
        this.recordingDuration = duration;
        this.worldCreationDuration = duration1;
        this.tickTimes = list;
        this.cpuLoadStats = list1;
        this.heapSummary = gcheapstat_a;
        this.threadAllocationSummary = threadallocationstat_a;
        this.receivedPacketsSummary = networkpacketsummary;
        this.sentPacketsSummary = networkpacketsummary1;
        this.fileWrites = fileiostat_a;
        this.fileReads = fileiostat_a1;
        this.chunkGenStats = list2;
    }

    public List<Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>>> chunkGenSummary() {
        Map<ChunkStatus, List<ChunkGenStat>> map = (Map) this.chunkGenStats.stream().collect(Collectors.groupingBy(ChunkGenStat::status));

        return map.entrySet().stream().map((entry) -> {
            return Pair.of((ChunkStatus) entry.getKey(), TimedStatSummary.summary((List) entry.getValue()));
        }).sorted(Comparator.comparing((pair) -> {
            return ((TimedStatSummary) pair.getSecond()).totalDuration();
        }).reversed()).toList();
    }

    public String asJson() {
        return (new JfrResultJsonSerializer()).format(this);
    }

    public Instant recordingStarted() {
        return this.recordingStarted;
    }

    public Instant recordingEnded() {
        return this.recordingEnded;
    }

    public Duration recordingDuration() {
        return this.recordingDuration;
    }

    @Nullable
    public Duration worldCreationDuration() {
        return this.worldCreationDuration;
    }

    public List<TickTimeStat> tickTimes() {
        return this.tickTimes;
    }

    public List<CpuLoadStat> cpuLoadStats() {
        return this.cpuLoadStats;
    }

    public GcHeapStat.a heapSummary() {
        return this.heapSummary;
    }

    public ThreadAllocationStat.a threadAllocationSummary() {
        return this.threadAllocationSummary;
    }

    public NetworkPacketSummary receivedPacketsSummary() {
        return this.receivedPacketsSummary;
    }

    public NetworkPacketSummary sentPacketsSummary() {
        return this.sentPacketsSummary;
    }

    public FileIOStat.a fileWrites() {
        return this.fileWrites;
    }

    public FileIOStat.a fileReads() {
        return this.fileReads;
    }

    public List<ChunkGenStat> chunkGenStats() {
        return this.chunkGenStats;
    }
}
