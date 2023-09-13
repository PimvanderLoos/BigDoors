package net.minecraft.util.profiling.metrics.profiling;

import net.minecraft.util.profiling.GameProfilerFiller;

public interface MetricsRecorder {

    void end();

    void cancel();

    void startTick();

    boolean isRecording();

    GameProfilerFiller getProfiler();

    void endTick();
}
