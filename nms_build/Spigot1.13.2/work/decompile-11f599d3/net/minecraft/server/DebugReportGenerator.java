package net.minecraft.server;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportGenerator {

    private static final Logger a = LogManager.getLogger();
    private final Collection<java.nio.file.Path> b;
    private final java.nio.file.Path c;
    private final List<DebugReportProvider> d = Lists.newArrayList();

    public DebugReportGenerator(java.nio.file.Path java_nio_file_path, Collection<java.nio.file.Path> collection) {
        this.c = java_nio_file_path;
        this.b = collection;
    }

    public Collection<java.nio.file.Path> a() {
        return this.b;
    }

    public java.nio.file.Path b() {
        return this.c;
    }

    public void c() throws IOException {
        HashCache hashcache = new HashCache(this.c, "cache");
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        Iterator iterator = this.d.iterator();

        while (iterator.hasNext()) {
            DebugReportProvider debugreportprovider = (DebugReportProvider) iterator.next();

            DebugReportGenerator.a.info("Starting provider: {}", debugreportprovider.a());
            stopwatch.start();
            debugreportprovider.a(hashcache);
            stopwatch.stop();
            DebugReportGenerator.a.info("{} finished after {} ms", debugreportprovider.a(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
            stopwatch.reset();
        }

        hashcache.a();
    }

    public void a(DebugReportProvider debugreportprovider) {
        this.d.add(debugreportprovider);
    }

    static {
        DispenserRegistry.c();
    }
}
