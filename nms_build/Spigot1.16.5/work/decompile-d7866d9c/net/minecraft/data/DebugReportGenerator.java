package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.DispenserRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportGenerator {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Collection<Path> b;
    private final Path c;
    private final List<DebugReportProvider> d = Lists.newArrayList();

    public DebugReportGenerator(Path path, Collection<Path> collection) {
        this.c = path;
        this.b = collection;
    }

    public Collection<Path> a() {
        return this.b;
    }

    public Path b() {
        return this.c;
    }

    public void c() throws IOException {
        HashCache hashcache = new HashCache(this.c, "cache");

        hashcache.c(this.b().resolve("version.json"));
        Stopwatch stopwatch = Stopwatch.createStarted();
        Stopwatch stopwatch1 = Stopwatch.createUnstarted();
        Iterator iterator = this.d.iterator();

        while (iterator.hasNext()) {
            DebugReportProvider debugreportprovider = (DebugReportProvider) iterator.next();

            DebugReportGenerator.LOGGER.info("Starting provider: {}", debugreportprovider.a());
            stopwatch1.start();
            debugreportprovider.a(hashcache);
            stopwatch1.stop();
            DebugReportGenerator.LOGGER.info("{} finished after {} ms", debugreportprovider.a(), stopwatch1.elapsed(TimeUnit.MILLISECONDS));
            stopwatch1.reset();
        }

        DebugReportGenerator.LOGGER.info("All providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        hashcache.a();
    }

    public void a(DebugReportProvider debugreportprovider) {
        this.d.add(debugreportprovider);
    }

    static {
        DispenserRegistry.init();
    }
}
