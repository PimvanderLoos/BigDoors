package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record TickTimeStat(Instant a, Duration b) {

    private final Instant timestamp;
    private final Duration currentAverage;

    public TickTimeStat(Instant instant, Duration duration) {
        this.timestamp = instant;
        this.currentAverage = duration;
    }

    public static TickTimeStat from(RecordedEvent recordedevent) {
        return new TickTimeStat(recordedevent.getStartTime(), recordedevent.getDuration("averageTickDuration"));
    }

    public Instant timestamp() {
        return this.timestamp;
    }

    public Duration currentAverage() {
        return this.currentAverage;
    }
}
