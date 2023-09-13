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
    public void a() {}

    @Override
    public void b() {}

    @Override
    public void enter(String s) {}

    @Override
    public void a(Supplier<String> supplier) {}

    @Override
    public void a(MetricCategory metriccategory) {}

    @Override
    public void exit() {}

    @Override
    public void exitEnter(String s) {}

    @Override
    public void b(Supplier<String> supplier) {}

    @Override
    public void c(String s) {}

    @Override
    public void c(Supplier<String> supplier) {}

    @Override
    public MethodProfilerResults d() {
        return MethodProfilerResultsEmpty.EMPTY;
    }

    @Nullable
    @Override
    public MethodProfiler.a d(String s) {
        return null;
    }

    @Override
    public Set<Pair<String, MetricCategory>> e() {
        return ImmutableSet.of();
    }
}
