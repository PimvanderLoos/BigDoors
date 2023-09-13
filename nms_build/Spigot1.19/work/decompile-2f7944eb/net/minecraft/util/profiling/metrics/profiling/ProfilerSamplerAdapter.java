package net.minecraft.util.profiling.metrics.profiling;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.TimeRange;
import net.minecraft.util.profiling.GameProfilerFillerActive;
import net.minecraft.util.profiling.MethodProfiler;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;

public class ProfilerSamplerAdapter {

    private final Set<String> previouslyFoundSamplerNames = new ObjectOpenHashSet();

    public ProfilerSamplerAdapter() {}

    public Set<MetricSampler> newSamplersFoundInProfiler(Supplier<GameProfilerFillerActive> supplier) {
        Set<MetricSampler> set = (Set) ((GameProfilerFillerActive) supplier.get()).getChartedPaths().stream().filter((pair) -> {
            return !this.previouslyFoundSamplerNames.contains(pair.getLeft());
        }).map((pair) -> {
            return samplerForProfilingPath(supplier, (String) pair.getLeft(), (MetricCategory) pair.getRight());
        }).collect(Collectors.toSet());
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            MetricSampler metricsampler = (MetricSampler) iterator.next();

            this.previouslyFoundSamplerNames.add(metricsampler.getName());
        }

        return set;
    }

    private static MetricSampler samplerForProfilingPath(Supplier<GameProfilerFillerActive> supplier, String s, MetricCategory metriccategory) {
        return MetricSampler.create(s, metriccategory, () -> {
            MethodProfiler.a methodprofiler_a = ((GameProfilerFillerActive) supplier.get()).getEntry(s);

            return methodprofiler_a == null ? 0.0D : (double) methodprofiler_a.getMaxDuration() / (double) TimeRange.NANOSECONDS_PER_MILLISECOND;
        });
    }
}
