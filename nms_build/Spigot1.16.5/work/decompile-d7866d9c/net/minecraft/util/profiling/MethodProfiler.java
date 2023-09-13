package net.minecraft.util.profiling;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class MethodProfiler implements GameProfilerFillerActive {

    private static final long a = Duration.ofMillis(100L).toNanos();
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<String> c = Lists.newArrayList();
    private final LongList d = new LongArrayList();
    private final Map<String, MethodProfiler.a> e = Maps.newHashMap();
    private final IntSupplier f;
    private final LongSupplier g;
    private final long h;
    private final int i;
    private String j = "";
    private boolean k;
    @Nullable
    private MethodProfiler.a l;
    private final boolean m;

    public MethodProfiler(LongSupplier longsupplier, IntSupplier intsupplier, boolean flag) {
        this.h = longsupplier.getAsLong();
        this.g = longsupplier;
        this.i = intsupplier.getAsInt();
        this.f = intsupplier;
        this.m = flag;
    }

    @Override
    public void a() {
        if (this.k) {
            MethodProfiler.LOGGER.error("Profiler tick already started - missing endTick()?");
        } else {
            this.k = true;
            this.j = "";
            this.c.clear();
            this.enter("root");
        }
    }

    @Override
    public void b() {
        if (!this.k) {
            MethodProfiler.LOGGER.error("Profiler tick already ended - missing startTick()?");
        } else {
            this.exit();
            this.k = false;
            if (!this.j.isEmpty()) {
                MethodProfiler.LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", new Supplier[]{() -> {
                            return MethodProfilerResults.b(this.j);
                        }});
            }

        }
    }

    @Override
    public void enter(String s) {
        if (!this.k) {
            MethodProfiler.LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", s);
        } else {
            if (!this.j.isEmpty()) {
                this.j = this.j + '\u001e';
            }

            this.j = this.j + s;
            this.c.add(this.j);
            this.d.add(SystemUtils.getMonotonicNanos());
            this.l = null;
        }
    }

    @Override
    public void a(java.util.function.Supplier<String> java_util_function_supplier) {
        this.enter((String) java_util_function_supplier.get());
    }

    @Override
    public void exit() {
        if (!this.k) {
            MethodProfiler.LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
        } else if (this.d.isEmpty()) {
            MethodProfiler.LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
        } else {
            long i = SystemUtils.getMonotonicNanos();
            long j = this.d.removeLong(this.d.size() - 1);

            this.c.remove(this.c.size() - 1);
            long k = i - j;
            MethodProfiler.a methodprofiler_a = this.e();

            methodprofiler_a.a = methodprofiler_a.a + k;
            methodprofiler_a.b = methodprofiler_a.b + 1L;
            if (this.m && k > MethodProfiler.a) {
                MethodProfiler.LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", new Supplier[]{() -> {
                            return MethodProfilerResults.b(this.j);
                        }, () -> {
                            return (double) k / 1000000.0D;
                        }});
            }

            this.j = this.c.isEmpty() ? "" : (String) this.c.get(this.c.size() - 1);
            this.l = null;
        }
    }

    @Override
    public void exitEnter(String s) {
        this.exit();
        this.enter(s);
    }

    private MethodProfiler.a e() {
        if (this.l == null) {
            this.l = (MethodProfiler.a) this.e.computeIfAbsent(this.j, (s) -> {
                return new MethodProfiler.a();
            });
        }

        return this.l;
    }

    @Override
    public void c(String s) {
        this.e().c.addTo(s, 1L);
    }

    @Override
    public void c(java.util.function.Supplier<String> java_util_function_supplier) {
        this.e().c.addTo(java_util_function_supplier.get(), 1L);
    }

    @Override
    public MethodProfilerResults d() {
        return new MethodProfilerResultsFilled(this.e, this.h, this.i, this.g.getAsLong(), this.f.getAsInt());
    }

    static class a implements MethodProfilerResult {

        private long a;
        private long b;
        private Object2LongOpenHashMap<String> c;

        private a() {
            this.c = new Object2LongOpenHashMap();
        }

        @Override
        public long a() {
            return this.a;
        }

        @Override
        public long b() {
            return this.b;
        }

        @Override
        public Object2LongMap<String> c() {
            return Object2LongMaps.unmodifiable(this.c);
        }
    }
}
