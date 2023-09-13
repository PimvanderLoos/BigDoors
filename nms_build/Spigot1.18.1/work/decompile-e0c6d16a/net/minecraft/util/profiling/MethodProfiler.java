package net.minecraft.util.profiling;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class MethodProfiler implements GameProfilerFillerActive {

    private static final long WARNING_TIME_NANOS = Duration.ofMillis(100L).toNanos();
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<String> paths = Lists.newArrayList();
    private final LongList startTimes = new LongArrayList();
    private final Map<String, MethodProfiler.a> entries = Maps.newHashMap();
    private final IntSupplier getTickTime;
    private final LongSupplier getRealTime;
    private final long startTimeNano;
    private final int startTimeTicks;
    private String path = "";
    private boolean started;
    @Nullable
    private MethodProfiler.a currentEntry;
    private final boolean warn;
    private final Set<Pair<String, MetricCategory>> chartedPaths = new ObjectArraySet();

    public MethodProfiler(LongSupplier longsupplier, IntSupplier intsupplier, boolean flag) {
        this.startTimeNano = longsupplier.getAsLong();
        this.getRealTime = longsupplier;
        this.startTimeTicks = intsupplier.getAsInt();
        this.getTickTime = intsupplier;
        this.warn = flag;
    }

    @Override
    public void startTick() {
        if (this.started) {
            MethodProfiler.LOGGER.error("Profiler tick already started - missing endTick()?");
        } else {
            this.started = true;
            this.path = "";
            this.paths.clear();
            this.push("root");
        }
    }

    @Override
    public void endTick() {
        if (!this.started) {
            MethodProfiler.LOGGER.error("Profiler tick already ended - missing startTick()?");
        } else {
            this.pop();
            this.started = false;
            if (!this.path.isEmpty()) {
                MethodProfiler.LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", new Supplier[]{() -> {
                            return MethodProfilerResults.demanglePath(this.path);
                        }});
            }

        }
    }

    @Override
    public void push(String s) {
        if (!this.started) {
            MethodProfiler.LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", s);
        } else {
            if (!this.path.isEmpty()) {
                this.path = this.path + "\u001e";
            }

            this.path = this.path + s;
            this.paths.add(this.path);
            this.startTimes.add(SystemUtils.getNanos());
            this.currentEntry = null;
        }
    }

    @Override
    public void push(java.util.function.Supplier<String> java_util_function_supplier) {
        this.push((String) java_util_function_supplier.get());
    }

    @Override
    public void markForCharting(MetricCategory metriccategory) {
        this.chartedPaths.add(Pair.of(this.path, metriccategory));
    }

    @Override
    public void pop() {
        if (!this.started) {
            MethodProfiler.LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
        } else if (this.startTimes.isEmpty()) {
            MethodProfiler.LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
        } else {
            long i = SystemUtils.getNanos();
            long j = this.startTimes.removeLong(this.startTimes.size() - 1);

            this.paths.remove(this.paths.size() - 1);
            long k = i - j;
            MethodProfiler.a methodprofiler_a = this.getCurrentEntry();

            methodprofiler_a.accumulatedDuration += k;
            ++methodprofiler_a.count;
            methodprofiler_a.maxDuration = Math.max(methodprofiler_a.maxDuration, k);
            methodprofiler_a.minDuration = Math.min(methodprofiler_a.minDuration, k);
            if (this.warn && k > MethodProfiler.WARNING_TIME_NANOS) {
                MethodProfiler.LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", new Supplier[]{() -> {
                            return MethodProfilerResults.demanglePath(this.path);
                        }, () -> {
                            return (double) k / 1000000.0D;
                        }});
            }

            this.path = this.paths.isEmpty() ? "" : (String) this.paths.get(this.paths.size() - 1);
            this.currentEntry = null;
        }
    }

    @Override
    public void popPush(String s) {
        this.pop();
        this.push(s);
    }

    @Override
    public void popPush(java.util.function.Supplier<String> java_util_function_supplier) {
        this.pop();
        this.push(java_util_function_supplier);
    }

    private MethodProfiler.a getCurrentEntry() {
        if (this.currentEntry == null) {
            this.currentEntry = (MethodProfiler.a) this.entries.computeIfAbsent(this.path, (s) -> {
                return new MethodProfiler.a();
            });
        }

        return this.currentEntry;
    }

    @Override
    public void incrementCounter(String s, int i) {
        this.getCurrentEntry().counters.addTo(s, (long) i);
    }

    @Override
    public void incrementCounter(java.util.function.Supplier<String> java_util_function_supplier, int i) {
        this.getCurrentEntry().counters.addTo((String) java_util_function_supplier.get(), (long) i);
    }

    @Override
    public MethodProfilerResults getResults() {
        return new MethodProfilerResultsFilled(this.entries, this.startTimeNano, this.startTimeTicks, this.getRealTime.getAsLong(), this.getTickTime.getAsInt());
    }

    @Nullable
    @Override
    public MethodProfiler.a getEntry(String s) {
        return (MethodProfiler.a) this.entries.get(s);
    }

    @Override
    public Set<Pair<String, MetricCategory>> getChartedPaths() {
        return this.chartedPaths;
    }

    public static class a implements MethodProfilerResult {

        long maxDuration = Long.MIN_VALUE;
        long minDuration = Long.MAX_VALUE;
        long accumulatedDuration;
        long count;
        final Object2LongOpenHashMap<String> counters = new Object2LongOpenHashMap();

        public a() {}

        @Override
        public long getDuration() {
            return this.accumulatedDuration;
        }

        @Override
        public long getMaxDuration() {
            return this.maxDuration;
        }

        @Override
        public long getCount() {
            return this.count;
        }

        @Override
        public Object2LongMap<String> getCounters() {
            return Object2LongMaps.unmodifiable(this.counters);
        }
    }
}
