package net.minecraft.util.profiling;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MethodProfilerResultsFilled implements MethodProfilerResults {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final MethodProfilerResult b = new MethodProfilerResult() {
        @Override
        public long a() {
            return 0L;
        }

        @Override
        public long b() {
            return 0L;
        }

        @Override
        public Object2LongMap<String> c() {
            return Object2LongMaps.emptyMap();
        }
    };
    private static final Splitter c = Splitter.on('\u001e');
    private static final Comparator<Entry<String, MethodProfilerResultsFilled.a>> d = Entry.comparingByValue(Comparator.comparingLong((methodprofilerresultsfilled_a) -> {
        return methodprofilerresultsfilled_a.b;
    })).reversed();
    private final Map<String, ? extends MethodProfilerResult> e;
    private final long f;
    private final int g;
    private final long h;
    private final int i;
    private final int j;

    public MethodProfilerResultsFilled(Map<String, ? extends MethodProfilerResult> map, long i, int j, long k, int l) {
        this.e = map;
        this.f = i;
        this.g = j;
        this.h = k;
        this.i = l;
        this.j = l - j;
    }

    private MethodProfilerResult c(String s) {
        MethodProfilerResult methodprofilerresult = (MethodProfilerResult) this.e.get(s);

        return methodprofilerresult != null ? methodprofilerresult : MethodProfilerResultsFilled.b;
    }

    public List<MethodProfilerResultsField> a(String s) {
        MethodProfilerResult methodprofilerresult = this.c("root");
        long i = methodprofilerresult.a();
        MethodProfilerResult methodprofilerresult1 = this.c(s);
        long j = methodprofilerresult1.a();
        long k = methodprofilerresult1.b();
        List<MethodProfilerResultsField> list = Lists.newArrayList();

        if (!s.isEmpty()) {
            s = s + '\u001e';
        }

        long l = 0L;
        Iterator iterator = this.e.keySet().iterator();

        while (iterator.hasNext()) {
            String s1 = (String) iterator.next();

            if (a(s, s1)) {
                l += this.c(s1).a();
            }
        }

        float f = (float) l;

        if (l < j) {
            l = j;
        }

        if (i < l) {
            i = l;
        }

        Iterator iterator1 = this.e.keySet().iterator();

        while (iterator1.hasNext()) {
            String s2 = (String) iterator1.next();

            if (a(s, s2)) {
                MethodProfilerResult methodprofilerresult2 = this.c(s2);
                long i1 = methodprofilerresult2.a();
                double d0 = (double) i1 * 100.0D / (double) l;
                double d1 = (double) i1 * 100.0D / (double) i;
                String s3 = s2.substring(s.length());

                list.add(new MethodProfilerResultsField(s3, d0, d1, methodprofilerresult2.b()));
            }
        }

        if ((float) l > f) {
            list.add(new MethodProfilerResultsField("unspecified", (double) ((float) l - f) * 100.0D / (double) l, (double) ((float) l - f) * 100.0D / (double) i, k));
        }

        Collections.sort(list);
        list.add(0, new MethodProfilerResultsField(s, 100.0D, (double) l * 100.0D / (double) i, k));
        return list;
    }

    private static boolean a(String s, String s1) {
        return s1.length() > s.length() && s1.startsWith(s) && s1.indexOf(30, s.length() + 1) < 0;
    }

    private Map<String, MethodProfilerResultsFilled.a> h() {
        Map<String, MethodProfilerResultsFilled.a> map = Maps.newTreeMap();

        this.e.forEach((s, methodprofilerresult) -> {
            Object2LongMap<String> object2longmap = methodprofilerresult.c();

            if (!object2longmap.isEmpty()) {
                List<String> list = MethodProfilerResultsFilled.c.splitToList(s);

                object2longmap.forEach((s1, olong) -> {
                    ((MethodProfilerResultsFilled.a) map.computeIfAbsent(s1, (s2) -> {
                        return new MethodProfilerResultsFilled.a();
                    })).a(list.iterator(), olong);
                });
            }

        });
        return map;
    }

    @Override
    public long a() {
        return this.f;
    }

    @Override
    public int b() {
        return this.g;
    }

    @Override
    public long c() {
        return this.h;
    }

    @Override
    public int d() {
        return this.i;
    }

    @Override
    public boolean a(File file) {
        file.getParentFile().mkdirs();
        OutputStreamWriter outputstreamwriter = null;

        boolean flag;

        try {
            outputstreamwriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            outputstreamwriter.write(this.a(this.g(), this.f()));
            boolean flag1 = true;

            return flag1;
        } catch (Throwable throwable) {
            MethodProfilerResultsFilled.LOGGER.error("Could not save profiler results to {}", file, throwable);
            flag = false;
        } finally {
            IOUtils.closeQuietly(outputstreamwriter);
        }

        return flag;
    }

    protected String a(long i, int j) {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append("---- Minecraft Profiler Results ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(i());
        stringbuilder.append("\n\n");
        stringbuilder.append("Version: ").append(SharedConstants.getGameVersion().getId()).append('\n');
        stringbuilder.append("Time span: ").append(i / 1000000L).append(" ms\n");
        stringbuilder.append("Tick span: ").append(j).append(" ticks\n");
        stringbuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float) j / ((float) i / 1.0E9F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.a(0, "root", stringbuilder);
        stringbuilder.append("--- END PROFILE DUMP ---\n\n");
        Map<String, MethodProfilerResultsFilled.a> map = this.h();

        if (!map.isEmpty()) {
            stringbuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
            this.a(map, stringbuilder, j);
            stringbuilder.append("--- END COUNTER DUMP ---\n\n");
        }

        return stringbuilder.toString();
    }

    private static StringBuilder a(StringBuilder stringbuilder, int i) {
        stringbuilder.append(String.format("[%02d] ", i));

        for (int j = 0; j < i; ++j) {
            stringbuilder.append("|   ");
        }

        return stringbuilder;
    }

    private void a(int i, String s, StringBuilder stringbuilder) {
        List<MethodProfilerResultsField> list = this.a(s);
        Object2LongMap<String> object2longmap = ((MethodProfilerResult) ObjectUtils.firstNonNull(new MethodProfilerResult[]{(MethodProfilerResult) this.e.get(s), MethodProfilerResultsFilled.b})).c();

        object2longmap.forEach((s1, olong) -> {
            a(stringbuilder, i).append('#').append(s1).append(' ').append(olong).append('/').append(olong / (long) this.j).append('\n');
        });
        if (list.size() >= 3) {
            for (int j = 1; j < list.size(); ++j) {
                MethodProfilerResultsField methodprofilerresultsfield = (MethodProfilerResultsField) list.get(j);

                a(stringbuilder, i).append(methodprofilerresultsfield.d).append('(').append(methodprofilerresultsfield.c).append('/').append(String.format(Locale.ROOT, "%.0f", (float) methodprofilerresultsfield.c / (float) this.j)).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", methodprofilerresultsfield.a)).append("%/").append(String.format(Locale.ROOT, "%.2f", methodprofilerresultsfield.b)).append("%\n");
                if (!"unspecified".equals(methodprofilerresultsfield.d)) {
                    try {
                        this.a(i + 1, s + '\u001e' + methodprofilerresultsfield.d, stringbuilder);
                    } catch (Exception exception) {
                        stringbuilder.append("[[ EXCEPTION ").append(exception).append(" ]]");
                    }
                }
            }

        }
    }

    private void a(int i, String s, MethodProfilerResultsFilled.a methodprofilerresultsfilled_a, int j, StringBuilder stringbuilder) {
        a(stringbuilder, i).append(s).append(" total:").append(methodprofilerresultsfilled_a.a).append('/').append(methodprofilerresultsfilled_a.b).append(" average: ").append(methodprofilerresultsfilled_a.a / (long) j).append('/').append(methodprofilerresultsfilled_a.b / (long) j).append('\n');
        methodprofilerresultsfilled_a.c.entrySet().stream().sorted(MethodProfilerResultsFilled.d).forEach((entry) -> {
            this.a(i + 1, (String) entry.getKey(), (MethodProfilerResultsFilled.a) entry.getValue(), j, stringbuilder);
        });
    }

    private void a(Map<String, MethodProfilerResultsFilled.a> map, StringBuilder stringbuilder, int i) {
        map.forEach((s, methodprofilerresultsfilled_a) -> {
            stringbuilder.append("-- Counter: ").append(s).append(" --\n");
            this.a(0, "root", (MethodProfilerResultsFilled.a) methodprofilerresultsfilled_a.c.get("root"), i, stringbuilder);
            stringbuilder.append("\n\n");
        });
    }

    private static String i() {
        String[] astring = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

        try {
            return astring[(int) (SystemUtils.getMonotonicNanos() % (long) astring.length)];
        } catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    @Override
    public int f() {
        return this.j;
    }

    static class a {

        private long a;
        private long b;
        private final Map<String, MethodProfilerResultsFilled.a> c;

        private a() {
            this.c = Maps.newHashMap();
        }

        public void a(Iterator<String> iterator, long i) {
            this.b += i;
            if (!iterator.hasNext()) {
                this.a += i;
            } else {
                ((MethodProfilerResultsFilled.a) this.c.computeIfAbsent(iterator.next(), (s) -> {
                    return new MethodProfilerResultsFilled.a();
                })).a(iterator, i);
            }

        }
    }
}
