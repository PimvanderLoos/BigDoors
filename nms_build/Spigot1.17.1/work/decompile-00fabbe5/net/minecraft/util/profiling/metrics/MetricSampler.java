package net.minecraft.util.profiling.metrics;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

public class MetricSampler {

    private final String name;
    private final MetricCategory category;
    private final DoubleSupplier sampler;
    private final ByteBuf ticks;
    private final ByteBuf values;
    private volatile boolean isRunning;
    @Nullable
    private final Runnable beforeTick;
    @Nullable
    final MetricSampler.c thresholdTest;
    private double currentValue;

    protected MetricSampler(String s, MetricCategory metriccategory, DoubleSupplier doublesupplier, @Nullable Runnable runnable, @Nullable MetricSampler.c metricsampler_c) {
        this.name = s;
        this.category = metriccategory;
        this.beforeTick = runnable;
        this.sampler = doublesupplier;
        this.thresholdTest = metricsampler_c;
        this.values = ByteBufAllocator.DEFAULT.buffer();
        this.ticks = ByteBufAllocator.DEFAULT.buffer();
        this.isRunning = true;
    }

    public static MetricSampler a(String s, MetricCategory metriccategory, DoubleSupplier doublesupplier) {
        return new MetricSampler(s, metriccategory, doublesupplier, (Runnable) null, (MetricSampler.c) null);
    }

    public static <T> MetricSampler a(String s, MetricCategory metriccategory, T t0, ToDoubleFunction<T> todoublefunction) {
        return a(s, metriccategory, todoublefunction, t0).a();
    }

    public static <T> MetricSampler.a<T> a(String s, MetricCategory metriccategory, ToDoubleFunction<T> todoublefunction, T t0) {
        return new MetricSampler.a<>(s, metriccategory, todoublefunction, t0);
    }

    public void a() {
        if (!this.isRunning) {
            throw new IllegalStateException("Not running");
        } else {
            if (this.beforeTick != null) {
                this.beforeTick.run();
            }

        }
    }

    public void a(int i) {
        this.h();
        this.currentValue = this.sampler.getAsDouble();
        this.values.writeDouble(this.currentValue);
        this.ticks.writeInt(i);
    }

    public void b() {
        this.h();
        this.values.release();
        this.ticks.release();
        this.isRunning = false;
    }

    private void h() {
        if (!this.isRunning) {
            throw new IllegalStateException(String.format("Sampler for metric %s not started!", this.name));
        }
    }

    DoubleSupplier c() {
        return this.sampler;
    }

    public String d() {
        return this.name;
    }

    public MetricCategory e() {
        return this.category;
    }

    public MetricSampler.b f() {
        Int2DoubleOpenHashMap int2doubleopenhashmap = new Int2DoubleOpenHashMap();
        int i = Integer.MIN_VALUE;

        int j;
        int k;

        for (k = Integer.MIN_VALUE; this.values.isReadable(8); k = j) {
            j = this.ticks.readInt();
            if (i == Integer.MIN_VALUE) {
                i = j;
            }

            int2doubleopenhashmap.put(j, this.values.readDouble());
        }

        return new MetricSampler.b(i, k, int2doubleopenhashmap);
    }

    public boolean g() {
        return this.thresholdTest != null && this.thresholdTest.test(this.currentValue);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            MetricSampler metricsampler = (MetricSampler) object;

            return this.name.equals(metricsampler.name) && this.category.equals(metricsampler.category);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public interface c {

        boolean test(double d0);
    }

    public static class a<T> {

        private final String name;
        private final MetricCategory category;
        private final DoubleSupplier sampler;
        private final T context;
        @Nullable
        private Runnable beforeTick;
        @Nullable
        private MetricSampler.c thresholdTest;

        public a(String s, MetricCategory metriccategory, ToDoubleFunction<T> todoublefunction, T t0) {
            this.name = s;
            this.category = metriccategory;
            this.sampler = () -> {
                return todoublefunction.applyAsDouble(t0);
            };
            this.context = t0;
        }

        public MetricSampler.a<T> a(Consumer<T> consumer) {
            this.beforeTick = () -> {
                consumer.accept(this.context);
            };
            return this;
        }

        public MetricSampler.a<T> a(MetricSampler.c metricsampler_c) {
            this.thresholdTest = metricsampler_c;
            return this;
        }

        public MetricSampler a() {
            return new MetricSampler(this.name, this.category, this.sampler, this.beforeTick, this.thresholdTest);
        }
    }

    public static class b {

        private final Int2DoubleMap recording;
        private final int firstTick;
        private final int lastTick;

        public b(int i, int j, Int2DoubleMap int2doublemap) {
            this.firstTick = i;
            this.lastTick = j;
            this.recording = int2doublemap;
        }

        public double a(int i) {
            return this.recording.get(i);
        }

        public int a() {
            return this.firstTick;
        }

        public int b() {
            return this.lastTick;
        }
    }

    public static class d implements MetricSampler.c {

        private final float percentageIncreaseThreshold;
        private double previousValue = Double.MIN_VALUE;

        public d(float f) {
            this.percentageIncreaseThreshold = f;
        }

        @Override
        public boolean test(double d0) {
            boolean flag;

            if (this.previousValue != Double.MIN_VALUE && d0 > this.previousValue) {
                flag = (d0 - this.previousValue) / this.previousValue >= (double) this.percentageIncreaseThreshold;
            } else {
                flag = false;
            }

            this.previousValue = d0;
            return flag;
        }
    }
}
