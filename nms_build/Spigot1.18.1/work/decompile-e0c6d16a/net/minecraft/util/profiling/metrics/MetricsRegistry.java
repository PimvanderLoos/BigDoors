package net.minecraft.util.profiling.metrics;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class MetricsRegistry {

    public static final MetricsRegistry INSTANCE = new MetricsRegistry();
    private final WeakHashMap<ProfilerMeasured, Void> measuredInstances = new WeakHashMap();

    private MetricsRegistry() {}

    public void add(ProfilerMeasured profilermeasured) {
        this.measuredInstances.put(profilermeasured, (Object) null);
    }

    public List<MetricSampler> getRegisteredSamplers() {
        Map<String, List<MetricSampler>> map = (Map) this.measuredInstances.keySet().stream().flatMap((profilermeasured) -> {
            return profilermeasured.profiledMetrics().stream();
        }).collect(Collectors.groupingBy(MetricSampler::getName));

        return aggregateDuplicates(map);
    }

    private static List<MetricSampler> aggregateDuplicates(Map<String, List<MetricSampler>> map) {
        return (List) map.entrySet().stream().map((entry) -> {
            String s = (String) entry.getKey();
            List<MetricSampler> list = (List) entry.getValue();

            return (MetricSampler) (list.size() > 1 ? new MetricsRegistry.a(s, list) : (MetricSampler) list.get(0));
        }).collect(Collectors.toList());
    }

    private static class a extends MetricSampler {

        private final List<MetricSampler> delegates;

        a(String s, List<MetricSampler> list) {
            super(s, ((MetricSampler) list.get(0)).getCategory(), () -> {
                return averageValueFromDelegates(list);
            }, () -> {
                beforeTick(list);
            }, thresholdTest(list));
            this.delegates = list;
        }

        private static MetricSampler.c thresholdTest(List<MetricSampler> list) {
            return (d0) -> {
                return list.stream().anyMatch((metricsampler) -> {
                    return metricsampler.thresholdTest != null ? metricsampler.thresholdTest.test(d0) : false;
                });
            };
        }

        private static void beforeTick(List<MetricSampler> list) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                MetricSampler metricsampler = (MetricSampler) iterator.next();

                metricsampler.onStartTick();
            }

        }

        private static double averageValueFromDelegates(List<MetricSampler> list) {
            double d0 = 0.0D;

            MetricSampler metricsampler;

            for (Iterator iterator = list.iterator(); iterator.hasNext(); d0 += metricsampler.getSampler().getAsDouble()) {
                metricsampler = (MetricSampler) iterator.next();
            }

            return d0 / (double) list.size();
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                if (!super.equals(object)) {
                    return false;
                } else {
                    MetricsRegistry.a metricsregistry_a = (MetricsRegistry.a) object;

                    return this.delegates.equals(metricsregistry_a.delegates);
                }
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(new Object[]{super.hashCode(), this.delegates});
        }
    }
}
