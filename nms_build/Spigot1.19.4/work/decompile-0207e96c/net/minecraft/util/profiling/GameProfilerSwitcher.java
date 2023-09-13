package net.minecraft.util.profiling;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public class GameProfilerSwitcher {

    private final LongSupplier realTime;
    private final IntSupplier tickCount;
    private GameProfilerFillerActive profiler;

    public GameProfilerSwitcher(LongSupplier longsupplier, IntSupplier intsupplier) {
        this.profiler = GameProfilerDisabled.INSTANCE;
        this.realTime = longsupplier;
        this.tickCount = intsupplier;
    }

    public boolean isEnabled() {
        return this.profiler != GameProfilerDisabled.INSTANCE;
    }

    public void disable() {
        this.profiler = GameProfilerDisabled.INSTANCE;
    }

    public void enable() {
        this.profiler = new MethodProfiler(this.realTime, this.tickCount, true);
    }

    public GameProfilerFiller getFiller() {
        return this.profiler;
    }

    public MethodProfilerResults getResults() {
        return this.profiler.getResults();
    }
}
