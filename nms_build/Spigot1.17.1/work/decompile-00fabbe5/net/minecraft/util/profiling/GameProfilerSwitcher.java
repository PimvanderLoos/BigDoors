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

    public boolean a() {
        return this.profiler != GameProfilerDisabled.INSTANCE;
    }

    public void b() {
        this.profiler = GameProfilerDisabled.INSTANCE;
    }

    public void c() {
        this.profiler = new MethodProfiler(this.realTime, this.tickCount, true);
    }

    public GameProfilerFiller d() {
        return this.profiler;
    }

    public MethodProfilerResults e() {
        return this.profiler.d();
    }
}
