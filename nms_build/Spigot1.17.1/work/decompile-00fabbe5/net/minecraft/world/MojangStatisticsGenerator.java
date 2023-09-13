package net.minecraft.world;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;

public class MojangStatisticsGenerator {

    private static final String POLL_HOST = "http://snoop.minecraft.net/";
    private static final long DATA_SEND_FREQUENCY = 900000L;
    private static final int SNOOPER_VERSION = 2;
    final Map<String, Object> fixedData = Maps.newHashMap();
    final Map<String, Object> dynamicData = Maps.newHashMap();
    final String token = UUID.randomUUID().toString();
    final URL url;
    final IMojangStatistics populator;
    private final Timer timer = new Timer("Snooper Timer", true);
    final Object lock = new Object();
    private final long startupTime;
    private boolean started;
    int count;

    public MojangStatisticsGenerator(String s, IMojangStatistics imojangstatistics, long i) {
        try {
            this.url = new URL("http://snoop.minecraft.net/" + s + "?version=2");
        } catch (MalformedURLException malformedurlexception) {
            throw new IllegalArgumentException();
        }

        this.populator = imojangstatistics;
        this.startupTime = i;
    }

    public void a() {
        if (!this.started) {
            ;
        }

    }

    private void h() {
        this.i();
        this.a("snooper_token", (Object) this.token);
        this.b("snooper_token", this.token);
        this.b("os_name", System.getProperty("os.name"));
        this.b("os_version", System.getProperty("os.version"));
        this.b("os_architecture", System.getProperty("os.arch"));
        this.b("java_version", System.getProperty("java.version"));
        this.a("version", (Object) SharedConstants.getGameVersion().getId());
        this.populator.b(this);
    }

    private void i() {
        int[] aint = new int[]{0};

        SystemUtils.j().forEach((s) -> {
            int i = aint[0];
            int j = aint[0];

            aint[0] = i + 1;
            this.a("jvm_arg[" + j + "]", (Object) s);
        });
        this.a("jvm_args", (Object) aint[0]);
    }

    public void b() {
        this.b("memory_total", Runtime.getRuntime().totalMemory());
        this.b("memory_max", Runtime.getRuntime().maxMemory());
        this.b("memory_free", Runtime.getRuntime().freeMemory());
        this.b("cpu_cores", Runtime.getRuntime().availableProcessors());
        this.populator.a(this);
    }

    public void a(String s, Object object) {
        Object object1 = this.lock;

        synchronized (this.lock) {
            this.dynamicData.put(s, object);
        }
    }

    public void b(String s, Object object) {
        Object object1 = this.lock;

        synchronized (this.lock) {
            this.fixedData.put(s, object);
        }
    }

    public Map<String, String> c() {
        Map<String, String> map = Maps.newLinkedHashMap();
        Object object = this.lock;

        synchronized (this.lock) {
            this.b();
            Iterator iterator = this.fixedData.entrySet().iterator();

            Entry entry;

            while (iterator.hasNext()) {
                entry = (Entry) iterator.next();
                map.put((String) entry.getKey(), entry.getValue().toString());
            }

            iterator = this.dynamicData.entrySet().iterator();

            while (iterator.hasNext()) {
                entry = (Entry) iterator.next();
                map.put((String) entry.getKey(), entry.getValue().toString());
            }

            return map;
        }
    }

    public boolean d() {
        return this.started;
    }

    public void e() {
        this.timer.cancel();
    }

    public String f() {
        return this.token;
    }

    public long g() {
        return this.startupTime;
    }
}
