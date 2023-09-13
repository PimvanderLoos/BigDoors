package net.minecraft.util.profiling.metrics.profiling;

import net.minecraft.util.profiling.GameProfilerDisabled;
import net.minecraft.util.profiling.GameProfilerFiller;

public class InactiveMetricsRecorder implements MetricsRecorder {

    public static final MetricsRecorder INSTANCE = new InactiveMetricsRecorder();

    public InactiveMetricsRecorder() {}

    @Override
    public void a() {}

    @Override
    public void b() {}

    @Override
    public boolean d() {
        return false;
    }

    @Override
    public GameProfilerFiller e() {
        return GameProfilerDisabled.INSTANCE;
    }

    @Override
    public void c() {}
}
