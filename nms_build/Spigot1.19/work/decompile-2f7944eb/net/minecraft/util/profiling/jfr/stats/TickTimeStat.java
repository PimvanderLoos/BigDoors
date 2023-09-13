package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record TickTimeStat(Instant timestamp, Duration currentAverage) {

    public static TickTimeStat from(RecordedEvent recordedevent) {
        return new TickTimeStat(recordedevent.getStartTime(), recordedevent.getDuration("averageTickDuration"));
    }
}
