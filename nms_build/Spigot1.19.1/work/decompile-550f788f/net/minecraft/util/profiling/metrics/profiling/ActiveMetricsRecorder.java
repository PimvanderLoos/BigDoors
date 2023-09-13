package net.minecraft.util.profiling.metrics.profiling;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.GameProfilerDisabled;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.util.profiling.GameProfilerFillerActive;
import net.minecraft.util.profiling.GameProfilerSwitcher;
import net.minecraft.util.profiling.MethodProfiler;
import net.minecraft.util.profiling.MethodProfilerResults;
import net.minecraft.util.profiling.MethodProfilerResultsEmpty;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.profiling.metrics.storage.RecordedDeviation;

public class ActiveMetricsRecorder implements MetricsRecorder {

    public static final int PROFILING_MAX_DURATION_SECONDS = 10;
    @Nullable
    private static Consumer<Path> globalOnReportFinished = null;
    private final Map<MetricSampler, List<RecordedDeviation>> deviationsBySampler = new Object2ObjectOpenHashMap();
    private final GameProfilerSwitcher taskProfiler;
    private final Executor ioExecutor;
    private final MetricsPersister metricsPersister;
    private final Consumer<MethodProfilerResults> onProfilingEnd;
    private final Consumer<Path> onReportFinished;
    private final MetricsSamplerProvider metricsSamplerProvider;
    private final LongSupplier wallTimeSource;
    private final long deadlineNano;
    private int currentTick;
    private GameProfilerFillerActive singleTickProfiler;
    private volatile boolean killSwitch;
    private Set<MetricSampler> thisTickSamplers = ImmutableSet.of();

    private ActiveMetricsRecorder(MetricsSamplerProvider metricssamplerprovider, LongSupplier longsupplier, Executor executor, MetricsPersister metricspersister, Consumer<MethodProfilerResults> consumer, Consumer<Path> consumer1) {
        this.metricsSamplerProvider = metricssamplerprovider;
        this.wallTimeSource = longsupplier;
        this.taskProfiler = new GameProfilerSwitcher(longsupplier, () -> {
            return this.currentTick;
        });
        this.ioExecutor = executor;
        this.metricsPersister = metricspersister;
        this.onProfilingEnd = consumer;
        this.onReportFinished = ActiveMetricsRecorder.globalOnReportFinished == null ? consumer1 : consumer1.andThen(ActiveMetricsRecorder.globalOnReportFinished);
        this.deadlineNano = longsupplier.getAsLong() + TimeUnit.NANOSECONDS.convert(10L, TimeUnit.SECONDS);
        this.singleTickProfiler = new MethodProfiler(this.wallTimeSource, () -> {
            return this.currentTick;
        }, false);
        this.taskProfiler.enable();
    }

    public static ActiveMetricsRecorder createStarted(MetricsSamplerProvider metricssamplerprovider, LongSupplier longsupplier, Executor executor, MetricsPersister metricspersister, Consumer<MethodProfilerResults> consumer, Consumer<Path> consumer1) {
        return new ActiveMetricsRecorder(metricssamplerprovider, longsupplier, executor, metricspersister, consumer, consumer1);
    }

    @Override
    public synchronized void end() {
        if (this.isRecording()) {
            this.killSwitch = true;
        }
    }

    @Override
    public synchronized void cancel() {
        if (this.isRecording()) {
            this.singleTickProfiler = GameProfilerDisabled.INSTANCE;
            this.onProfilingEnd.accept(MethodProfilerResultsEmpty.EMPTY);
            this.cleanup(this.thisTickSamplers);
        }
    }

    @Override
    public void startTick() {
        this.verifyStarted();
        this.thisTickSamplers = this.metricsSamplerProvider.samplers(() -> {
            return this.singleTickProfiler;
        });
        Iterator iterator = this.thisTickSamplers.iterator();

        while (iterator.hasNext()) {
            MetricSampler metricsampler = (MetricSampler) iterator.next();

            metricsampler.onStartTick();
        }

        ++this.currentTick;
    }

    @Override
    public void endTick() {
        this.verifyStarted();
        if (this.currentTick != 0) {
            Iterator iterator = this.thisTickSamplers.iterator();

            while (iterator.hasNext()) {
                MetricSampler metricsampler = (MetricSampler) iterator.next();

                metricsampler.onEndTick(this.currentTick);
                if (metricsampler.triggersThreshold()) {
                    RecordedDeviation recordeddeviation = new RecordedDeviation(Instant.now(), this.currentTick, this.singleTickProfiler.getResults());

                    ((List) this.deviationsBySampler.computeIfAbsent(metricsampler, (metricsampler1) -> {
                        return Lists.newArrayList();
                    })).add(recordeddeviation);
                }
            }

            if (!this.killSwitch && this.wallTimeSource.getAsLong() <= this.deadlineNano) {
                this.singleTickProfiler = new MethodProfiler(this.wallTimeSource, () -> {
                    return this.currentTick;
                }, false);
            } else {
                this.killSwitch = false;
                MethodProfilerResults methodprofilerresults = this.taskProfiler.getResults();

                this.singleTickProfiler = GameProfilerDisabled.INSTANCE;
                this.onProfilingEnd.accept(methodprofilerresults);
                this.scheduleSaveResults(methodprofilerresults);
            }
        }
    }

    @Override
    public boolean isRecording() {
        return this.taskProfiler.isEnabled();
    }

    @Override
    public GameProfilerFiller getProfiler() {
        return GameProfilerFiller.tee(this.taskProfiler.getFiller(), this.singleTickProfiler);
    }

    private void verifyStarted() {
        if (!this.isRecording()) {
            throw new IllegalStateException("Not started!");
        }
    }

    private void scheduleSaveResults(MethodProfilerResults methodprofilerresults) {
        HashSet<MetricSampler> hashset = new HashSet(this.thisTickSamplers);

        this.ioExecutor.execute(() -> {
            Path path = this.metricsPersister.saveReports(hashset, this.deviationsBySampler, methodprofilerresults);

            this.cleanup(hashset);
            this.onReportFinished.accept(path);
        });
    }

    private void cleanup(Collection<MetricSampler> collection) {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            MetricSampler metricsampler = (MetricSampler) iterator.next();

            metricsampler.onFinished();
        }

        this.deviationsBySampler.clear();
        this.taskProfiler.disable();
    }

    public static void registerGlobalCompletionCallback(Consumer<Path> consumer) {
        ActiveMetricsRecorder.globalOnReportFinished = consumer;
    }
}
