package net.minecraft.util.profiling.metrics.profiling;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.profiling.GameProfilerFillerActive;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class ServerMetricsSamplersProvider implements MetricsSamplerProvider {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Set<MetricSampler> samplers = new ObjectOpenHashSet();
    private final ProfilerSamplerAdapter samplerFactory = new ProfilerSamplerAdapter();

    public ServerMetricsSamplersProvider(LongSupplier longsupplier, boolean flag) {
        this.samplers.add(tickTimeSampler(longsupplier));
        if (flag) {
            this.samplers.addAll(runtimeIndependentSamplers());
        }

    }

    public static Set<MetricSampler> runtimeIndependentSamplers() {
        Builder builder = ImmutableSet.builder();

        try {
            ServerMetricsSamplersProvider.a servermetricssamplersprovider_a = new ServerMetricsSamplersProvider.a();
            Stream stream = IntStream.range(0, servermetricssamplersprovider_a.nrOfCpus).mapToObj((i) -> {
                return MetricSampler.create("cpu#" + i, MetricCategory.CPU, () -> {
                    return servermetricssamplersprovider_a.loadForCpu(i);
                });
            });

            Objects.requireNonNull(builder);
            stream.forEach(builder::add);
        } catch (Throwable throwable) {
            ServerMetricsSamplersProvider.LOGGER.warn("Failed to query cpu, no cpu stats will be recorded", throwable);
        }

        builder.add(MetricSampler.create("heap MiB", MetricCategory.JVM, () -> {
            return (double) ((float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576.0F);
        }));
        builder.addAll(MetricsRegistry.INSTANCE.getRegisteredSamplers());
        return builder.build();
    }

    @Override
    public Set<MetricSampler> samplers(Supplier<GameProfilerFillerActive> supplier) {
        this.samplers.addAll(this.samplerFactory.newSamplersFoundInProfiler(supplier));
        return this.samplers;
    }

    public static MetricSampler tickTimeSampler(final LongSupplier longsupplier) {
        Stopwatch stopwatch = Stopwatch.createUnstarted(new Ticker() {
            public long read() {
                return longsupplier.getAsLong();
            }
        });
        ToDoubleFunction<Stopwatch> todoublefunction = (stopwatch1) -> {
            if (stopwatch1.isRunning()) {
                stopwatch1.stop();
            }

            long i = stopwatch1.elapsed(TimeUnit.NANOSECONDS);

            stopwatch1.reset();
            return (double) i;
        };
        MetricSampler.d metricsampler_d = new MetricSampler.d(2.0F);

        return MetricSampler.builder("ticktime", MetricCategory.TICK_LOOP, todoublefunction, stopwatch).withBeforeTick(Stopwatch::start).withThresholdAlert(metricsampler_d).build();
    }

    static class a {

        private final SystemInfo systemInfo = new SystemInfo();
        private final CentralProcessor processor;
        public final int nrOfCpus;
        private long[][] previousCpuLoadTick;
        private double[] currentLoad;
        private long lastPollMs;

        a() {
            this.processor = this.systemInfo.getHardware().getProcessor();
            this.nrOfCpus = this.processor.getLogicalProcessorCount();
            this.previousCpuLoadTick = this.processor.getProcessorCpuLoadTicks();
            this.currentLoad = this.processor.getProcessorCpuLoadBetweenTicks(this.previousCpuLoadTick);
        }

        public double loadForCpu(int i) {
            long j = System.currentTimeMillis();

            if (this.lastPollMs == 0L || this.lastPollMs + 501L < j) {
                this.currentLoad = this.processor.getProcessorCpuLoadBetweenTicks(this.previousCpuLoadTick);
                this.previousCpuLoadTick = this.processor.getProcessorCpuLoadTicks();
                this.lastPollMs = j;
            }

            return this.currentLoad[i] * 100.0D;
        }
    }
}
