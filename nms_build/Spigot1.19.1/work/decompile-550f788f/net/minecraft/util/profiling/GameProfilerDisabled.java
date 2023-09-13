package net.minecraft.util.profiling;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.apache.commons.lang3.tuple.Pair;

public class GameProfilerDisabled implements GameProfilerFillerActive {

    public static final GameProfilerDisabled INSTANCE = new GameProfilerDisabled();

    private GameProfilerDisabled() {}

    @Override
    public void startTick() {}

    @Override
    public void endTick() {}

    @Override
    public void push(String s) {}

    @Override
    public void push(Supplier<String> supplier) {}

    @Override
    public void markForCharting(MetricCategory metriccategory) {}

    @Override
    public void pop() {}

    @Override
    public void popPush(String s) {}

    @Override
    public void popPush(Supplier<String> supplier) {}

    @Override
    public void incrementCounter(String s, int i) {}

    @Override
    public void incrementCounter(Supplier<String> supplier, int i) {}

    @Override
    public MethodProfilerResults getResults() {
        return MethodProfilerResultsEmpty.EMPTY;
    }

    @Nullable
    @Override
    public MethodProfiler.a getEntry(String s) {
        return null;
    }

    @Override
    public Set<Pair<String, MetricCategory>> getChartedPaths() {
        return ImmutableSet.of();
    }
}
