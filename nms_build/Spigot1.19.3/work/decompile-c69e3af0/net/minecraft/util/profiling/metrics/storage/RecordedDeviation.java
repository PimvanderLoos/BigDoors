package net.minecraft.util.profiling.metrics.storage;

import java.time.Instant;
import net.minecraft.util.profiling.MethodProfilerResults;

public final class RecordedDeviation {

    public final Instant timestamp;
    public final int tick;
    public final MethodProfilerResults profilerResultAtTick;

    public RecordedDeviation(Instant instant, int i, MethodProfilerResults methodprofilerresults) {
        this.timestamp = instant;
        this.tick = i;
        this.profilerResultAtTick = methodprofilerresults;
    }
}
