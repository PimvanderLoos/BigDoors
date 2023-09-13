package net.minecraft.util.profiling.jfr.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;

public class JfrStatsParser {

    private Instant recordingStarted;
    private Instant recordingEnded;
    private final List<ChunkGenStat> chunkGenStats;
    private final List<CpuLoadStat> cpuLoadStat;
    private final Map<NetworkPacketSummary.b, JfrStatsParser.a> receivedPackets;
    private final Map<NetworkPacketSummary.b, JfrStatsParser.a> sentPackets;
    private final List<FileIOStat> fileWrites;
    private final List<FileIOStat> fileReads;
    private int garbageCollections;
    private Duration gcTotalDuration;
    private final List<GcHeapStat> gcHeapStats;
    private final List<ThreadAllocationStat> threadAllocationStats;
    private final List<TickTimeStat> tickTimes;
    @Nullable
    private Duration worldCreationDuration;

    private JfrStatsParser(Stream<RecordedEvent> stream) {
        this.recordingStarted = Instant.EPOCH;
        this.recordingEnded = Instant.EPOCH;
        this.chunkGenStats = Lists.newArrayList();
        this.cpuLoadStat = Lists.newArrayList();
        this.receivedPackets = Maps.newHashMap();
        this.sentPackets = Maps.newHashMap();
        this.fileWrites = Lists.newArrayList();
        this.fileReads = Lists.newArrayList();
        this.gcTotalDuration = Duration.ZERO;
        this.gcHeapStats = Lists.newArrayList();
        this.threadAllocationStats = Lists.newArrayList();
        this.tickTimes = Lists.newArrayList();
        this.worldCreationDuration = null;
        this.capture(stream);
    }

    public static JfrStatsResult parse(Path path) {
        try {
            final RecordingFile recordingfile = new RecordingFile(path);

            JfrStatsResult jfrstatsresult;

            try {
                Iterator<RecordedEvent> iterator = new Iterator<RecordedEvent>() {
                    public boolean hasNext() {
                        return recordingfile.hasMoreEvents();
                    }

                    public RecordedEvent next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        } else {
                            try {
                                return recordingfile.readEvent();
                            } catch (IOException ioexception) {
                                throw new UncheckedIOException(ioexception);
                            }
                        }
                    }
                };
                Stream<RecordedEvent> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 1297), false);

                jfrstatsresult = (new JfrStatsParser(stream)).results();
            } catch (Throwable throwable) {
                try {
                    recordingfile.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }

                throw throwable;
            }

            recordingfile.close();
            return jfrstatsresult;
        } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
        }
    }

    private JfrStatsResult results() {
        Duration duration = Duration.between(this.recordingStarted, this.recordingEnded);

        return new JfrStatsResult(this.recordingStarted, this.recordingEnded, duration, this.worldCreationDuration, this.tickTimes, this.cpuLoadStat, GcHeapStat.summary(duration, this.gcHeapStats, this.gcTotalDuration, this.garbageCollections), ThreadAllocationStat.summary(this.threadAllocationStats), collectPacketStats(duration, this.receivedPackets), collectPacketStats(duration, this.sentPackets), FileIOStat.summary(duration, this.fileWrites), FileIOStat.summary(duration, this.fileReads), this.chunkGenStats);
    }

    private void capture(Stream<RecordedEvent> stream) {
        stream.forEach((recordedevent) -> {
            if (recordedevent.getEndTime().isAfter(this.recordingEnded) || this.recordingEnded.equals(Instant.EPOCH)) {
                this.recordingEnded = recordedevent.getEndTime();
            }

            if (recordedevent.getStartTime().isBefore(this.recordingStarted) || this.recordingStarted.equals(Instant.EPOCH)) {
                this.recordingStarted = recordedevent.getStartTime();
            }

            String s = recordedevent.getEventType().getName();
            byte b0 = -1;

            switch (s.hashCode()) {
                case -1839589802:
                    if (s.equals("jdk.GarbageCollection")) {
                        b0 = 10;
                    }
                    break;
                case -1477302047:
                    if (s.equals("jdk.GCHeapSummary")) {
                        b0 = 6;
                    }
                    break;
                case -1062263542:
                    if (s.equals("jdk.ThreadAllocationStatistics")) {
                        b0 = 5;
                    }
                    break;
                case -996376789:
                    if (s.equals("minecraft.LoadWorld")) {
                        b0 = 1;
                    }
                    break;
                case -561696959:
                    if (s.equals("minecraft.PacketSent")) {
                        b0 = 4;
                    }
                    break;
                case -425698066:
                    if (s.equals("minecraft.ServerTickTime")) {
                        b0 = 2;
                    }
                    break;
                case -270233553:
                    if (s.equals("jdk.FileRead")) {
                        b0 = 9;
                    }
                    break;
                case 217707622:
                    if (s.equals("jdk.FileWrite")) {
                        b0 = 8;
                    }
                    break;
                case 470410257:
                    if (s.equals("jdk.CPULoad")) {
                        b0 = 7;
                    }
                    break;
                case 849431818:
                    if (s.equals("minecraft.PacketReceived")) {
                        b0 = 3;
                    }
                    break;
                case 1320933636:
                    if (s.equals("minecraft.ChunkGeneration")) {
                        b0 = 0;
                    }
            }

            switch (b0) {
                case 0:
                    this.chunkGenStats.add(ChunkGenStat.from(recordedevent));
                    break;
                case 1:
                    this.worldCreationDuration = recordedevent.getDuration();
                    break;
                case 2:
                    this.tickTimes.add(TickTimeStat.from(recordedevent));
                    break;
                case 3:
                    this.incrementPacket(recordedevent, recordedevent.getInt("bytes"), this.receivedPackets);
                    break;
                case 4:
                    this.incrementPacket(recordedevent, recordedevent.getInt("bytes"), this.sentPackets);
                    break;
                case 5:
                    this.threadAllocationStats.add(ThreadAllocationStat.from(recordedevent));
                    break;
                case 6:
                    this.gcHeapStats.add(GcHeapStat.from(recordedevent));
                    break;
                case 7:
                    this.cpuLoadStat.add(CpuLoadStat.from(recordedevent));
                    break;
                case 8:
                    this.appendFileIO(recordedevent, this.fileWrites, "bytesWritten");
                    break;
                case 9:
                    this.appendFileIO(recordedevent, this.fileReads, "bytesRead");
                    break;
                case 10:
                    ++this.garbageCollections;
                    this.gcTotalDuration = this.gcTotalDuration.plus(recordedevent.getDuration());
            }

        });
    }

    private void incrementPacket(RecordedEvent recordedevent, int i, Map<NetworkPacketSummary.b, JfrStatsParser.a> map) {
        ((JfrStatsParser.a) map.computeIfAbsent(NetworkPacketSummary.b.from(recordedevent), (networkpacketsummary_b) -> {
            return new JfrStatsParser.a();
        })).increment(i);
    }

    private void appendFileIO(RecordedEvent recordedevent, List<FileIOStat> list, String s) {
        list.add(new FileIOStat(recordedevent.getDuration(), recordedevent.getString("path"), recordedevent.getLong(s)));
    }

    private static NetworkPacketSummary collectPacketStats(Duration duration, Map<NetworkPacketSummary.b, JfrStatsParser.a> map) {
        List<Pair<NetworkPacketSummary.b, NetworkPacketSummary.a>> list = map.entrySet().stream().map((entry) -> {
            return Pair.of((NetworkPacketSummary.b) entry.getKey(), ((JfrStatsParser.a) entry.getValue()).toCountAndSize());
        }).toList();

        return new NetworkPacketSummary(duration, list);
    }

    public static final class a {

        private long count;
        private long totalSize;

        public a() {}

        public void increment(int i) {
            this.totalSize += (long) i;
            ++this.count;
        }

        public NetworkPacketSummary.a toCountAndSize() {
            return new NetworkPacketSummary.a(this.count, this.totalSize);
        }
    }
}
