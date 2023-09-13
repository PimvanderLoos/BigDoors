package net.minecraft.util.profiling.metrics.profiling;

import net.minecraft.util.profiling.GameProfilerDisabled;
import net.minecraft.util.profiling.GameProfilerFiller;

public class InactiveMetricsRecorder implements MetricsRecorder {

    public static final MetricsRecorder INSTANCE = new InactiveMetricsRecorder();

    public InactiveMetricsRecorder() {}

    @Override
    public void end() {}

    @Override
    public void cancel() {}

    @Override
    public void startTick() {}

    @Override
    public boolean isRecording() {
        return false;
    }

    @Override
    public GameProfilerFiller getProfiler() {
        return GameProfilerDisabled.INSTANCE;
    }

    @Override
    public void endTick() {}
}
