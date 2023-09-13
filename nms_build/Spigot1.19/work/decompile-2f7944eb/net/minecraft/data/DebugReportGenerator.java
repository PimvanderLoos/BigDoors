package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.WorldVersion;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.DispenserRegistry;
import org.slf4j.Logger;

public class DebugReportGenerator {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Collection<Path> inputFolders;
    private final Path outputFolder;
    private final List<DebugReportProvider> allProviders = Lists.newArrayList();
    private final List<DebugReportProvider> providersToRun = Lists.newArrayList();
    private final WorldVersion version;
    private final boolean alwaysGenerate;

    public DebugReportGenerator(Path path, Collection<Path> collection, WorldVersion worldversion, boolean flag) {
        this.outputFolder = path;
        this.inputFolders = collection;
        this.version = worldversion;
        this.alwaysGenerate = flag;
    }

    public Collection<Path> getInputFolders() {
        return this.inputFolders;
    }

    public Path getOutputFolder() {
        return this.outputFolder;
    }

    public Path getOutputFolder(DebugReportGenerator.b debugreportgenerator_b) {
        return this.getOutputFolder().resolve(debugreportgenerator_b.directory);
    }

    public void run() throws IOException {
        HashCache hashcache = new HashCache(this.outputFolder, this.allProviders, this.version);
        Stopwatch stopwatch = Stopwatch.createStarted();
        Stopwatch stopwatch1 = Stopwatch.createUnstarted();
        Iterator iterator = this.providersToRun.iterator();

        while (iterator.hasNext()) {
            DebugReportProvider debugreportprovider = (DebugReportProvider) iterator.next();

            if (!this.alwaysGenerate && !hashcache.shouldRunInThisVersion(debugreportprovider)) {
                DebugReportGenerator.LOGGER.debug("Generator {} already run for version {}", debugreportprovider.getName(), this.version.getName());
            } else {
                DebugReportGenerator.LOGGER.info("Starting provider: {}", debugreportprovider.getName());
                stopwatch1.start();
                debugreportprovider.run(hashcache.getUpdater(debugreportprovider));
                stopwatch1.stop();
                DebugReportGenerator.LOGGER.info("{} finished after {} ms", debugreportprovider.getName(), stopwatch1.elapsed(TimeUnit.MILLISECONDS));
                stopwatch1.reset();
            }
        }

        DebugReportGenerator.LOGGER.info("All providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        hashcache.purgeStaleAndWrite();
    }

    public void addProvider(boolean flag, DebugReportProvider debugreportprovider) {
        if (flag) {
            this.providersToRun.add(debugreportprovider);
        }

        this.allProviders.add(debugreportprovider);
    }

    public DebugReportGenerator.a createPathProvider(DebugReportGenerator.b debugreportgenerator_b, String s) {
        return new DebugReportGenerator.a(this, debugreportgenerator_b, s);
    }

    static {
        DispenserRegistry.bootStrap();
    }

    public static enum b {

        DATA_PACK("data"), RESOURCE_PACK("assets"), REPORTS("reports");

        final String directory;

        private b(String s) {
            this.directory = s;
        }
    }

    public static class a {

        private final Path root;
        private final String kind;

        a(DebugReportGenerator debugreportgenerator, DebugReportGenerator.b debugreportgenerator_b, String s) {
            this.root = debugreportgenerator.getOutputFolder(debugreportgenerator_b);
            this.kind = s;
        }

        public Path file(MinecraftKey minecraftkey, String s) {
            Path path = this.root.resolve(minecraftkey.getNamespace()).resolve(this.kind);
            String s1 = minecraftkey.getPath();

            return path.resolve(s1 + "." + s);
        }

        public Path json(MinecraftKey minecraftkey) {
            return this.root.resolve(minecraftkey.getNamespace()).resolve(this.kind).resolve(minecraftkey.getPath() + ".json");
        }
    }
}
