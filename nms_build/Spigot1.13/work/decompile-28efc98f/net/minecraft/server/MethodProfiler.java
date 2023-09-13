package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MethodProfiler {

    private static final Logger a = LogManager.getLogger();
    private final List<String> b = Lists.newArrayList();
    private final List<Long> c = Lists.newArrayList();
    private boolean d;
    private String e = "";
    private final Map<String, Long> f = Maps.newHashMap();
    private long g;
    private int h;

    public MethodProfiler() {}

    public boolean a() {
        return this.d;
    }

    public void b() {
        this.d = false;
    }

    public long c() {
        return this.g;
    }

    public int d() {
        return this.h;
    }

    public void a(int i) {
        if (!this.d) {
            this.d = true;
            this.f.clear();
            this.e = "";
            this.b.clear();
            this.h = i;
            this.g = SystemUtils.c();
        }
    }

    public void a(String s) {
        if (this.d) {
            if (!this.e.isEmpty()) {
                this.e = this.e + ".";
            }

            this.e = this.e + s;
            this.b.add(this.e);
            this.c.add(Long.valueOf(SystemUtils.c()));
        }
    }

    public void a(Supplier<String> supplier) {
        if (this.d) {
            this.a((String) supplier.get());
        }
    }

    public void e() {
        if (this.d && !this.c.isEmpty()) {
            long i = SystemUtils.c();
            long j = ((Long) this.c.remove(this.c.size() - 1)).longValue();

            this.b.remove(this.b.size() - 1);
            long k = i - j;

            if (this.f.containsKey(this.e)) {
                this.f.put(this.e, Long.valueOf(((Long) this.f.get(this.e)).longValue() + k));
            } else {
                this.f.put(this.e, Long.valueOf(k));
            }

            if (k > 100000000L) {
                MethodProfiler.a.warn("Something\'s taking too long! \'{}\' took aprox {} ms", this.e, Double.valueOf((double) k / 1000000.0D));
            }

            this.e = this.b.isEmpty() ? "" : (String) this.b.get(this.b.size() - 1);
        }
    }

    public List<MethodProfiler.ProfilerInfo> b(String s) {
        long i = this.f.containsKey("root") ? ((Long) this.f.get("root")).longValue() : 0L;
        long j = this.f.containsKey(s) ? ((Long) this.f.get(s)).longValue() : -1L;
        ArrayList arraylist = Lists.newArrayList();

        if (!s.isEmpty()) {
            s = s + ".";
        }

        long k = 0L;
        Iterator iterator = this.f.keySet().iterator();

        while (iterator.hasNext()) {
            String s1 = (String) iterator.next();

            if (s1.length() > s.length() && s1.startsWith(s) && s1.indexOf(".", s.length() + 1) < 0) {
                k += ((Long) this.f.get(s1)).longValue();
            }
        }

        float f = (float) k;

        if (k < j) {
            k = j;
        }

        if (i < k) {
            i = k;
        }

        Iterator iterator1 = this.f.keySet().iterator();

        String s2;

        while (iterator1.hasNext()) {
            s2 = (String) iterator1.next();
            if (s2.length() > s.length() && s2.startsWith(s) && s2.indexOf(".", s.length() + 1) < 0) {
                long l = ((Long) this.f.get(s2)).longValue();
                double d0 = (double) l * 100.0D / (double) k;
                double d1 = (double) l * 100.0D / (double) i;
                String s3 = s2.substring(s.length());

                arraylist.add(new MethodProfiler.ProfilerInfo(s3, d0, d1));
            }
        }

        iterator1 = this.f.keySet().iterator();

        while (iterator1.hasNext()) {
            s2 = (String) iterator1.next();
            this.f.put(s2, Long.valueOf(((Long) this.f.get(s2)).longValue() * 999L / 1000L));
        }

        if ((float) k > f) {
            arraylist.add(new MethodProfiler.ProfilerInfo("unspecified", (double) ((float) k - f) * 100.0D / (double) k, (double) ((float) k - f) * 100.0D / (double) i));
        }

        Collections.sort(arraylist);
        arraylist.add(0, new MethodProfiler.ProfilerInfo(s, 100.0D, (double) k * 100.0D / (double) i));
        return arraylist;
    }

    public void c(String s) {
        this.e();
        this.a(s);
    }

    public String f() {
        return this.b.isEmpty() ? "[UNKNOWN]" : (String) this.b.get(this.b.size() - 1);
    }

    public static final class ProfilerInfo implements Comparable<MethodProfiler.ProfilerInfo> {

        public double a;
        public double b;
        public String c;

        public ProfilerInfo(String s, double d0, double d1) {
            this.c = s;
            this.a = d0;
            this.b = d1;
        }

        public int a(MethodProfiler.ProfilerInfo methodprofiler_profilerinfo) {
            return methodprofiler_profilerinfo.a < this.a ? -1 : (methodprofiler_profilerinfo.a > this.a ? 1 : methodprofiler_profilerinfo.c.compareTo(this.c));
        }

        public int compareTo(Object object) {
            return this.a((MethodProfiler.ProfilerInfo) object);
        }
    }
}
