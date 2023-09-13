package net.minecraft.data;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.WorldVersion;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class HashCache {

    static final Logger LOGGER = LogUtils.getLogger();
    private static final String HEADER_MARKER = "// ";
    private final Path rootDir;
    private final Path cacheDir;
    private final String versionId;
    private final Map<DebugReportProvider, HashCache.b> existingCaches;
    private final Map<DebugReportProvider, HashCache.a> cachesToWrite = new HashMap();
    private final Set<Path> cachePaths = new HashSet();
    private final int initialCount;

    private Path getProviderCachePath(DebugReportProvider debugreportprovider) {
        return this.cacheDir.resolve(Hashing.sha1().hashString(debugreportprovider.getName(), StandardCharsets.UTF_8).toString());
    }

    public HashCache(Path path, List<DebugReportProvider> list, WorldVersion worldversion) throws IOException {
        this.versionId = worldversion.getName();
        this.rootDir = path;
        this.cacheDir = path.resolve(".cache");
        Files.createDirectories(this.cacheDir);
        Map<DebugReportProvider, HashCache.b> map = new HashMap();
        int i = 0;

        HashCache.b hashcache_b;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); i += hashcache_b.count()) {
            DebugReportProvider debugreportprovider = (DebugReportProvider) iterator.next();
            Path path1 = this.getProviderCachePath(debugreportprovider);

            this.cachePaths.add(path1);
            hashcache_b = readCache(path, path1);
            map.put(debugreportprovider, hashcache_b);
        }

        this.existingCaches = map;
        this.initialCount = i;
    }

    private static HashCache.b readCache(Path path, Path path1) {
        if (Files.isReadable(path1)) {
            try {
                return HashCache.b.load(path, path1);
            } catch (Exception exception) {
                HashCache.LOGGER.warn("Failed to parse cache {}, discarding", path1, exception);
            }
        }

        return new HashCache.b("unknown");
    }

    public boolean shouldRunInThisVersion(DebugReportProvider debugreportprovider) {
        HashCache.b hashcache_b = (HashCache.b) this.existingCaches.get(debugreportprovider);

        return hashcache_b == null || !hashcache_b.version.equals(this.versionId);
    }

    public CachedOutput getUpdater(DebugReportProvider debugreportprovider) {
        return (CachedOutput) this.cachesToWrite.computeIfAbsent(debugreportprovider, (debugreportprovider1) -> {
            HashCache.b hashcache_b = (HashCache.b) this.existingCaches.get(debugreportprovider1);

            if (hashcache_b == null) {
                throw new IllegalStateException("Provider not registered: " + debugreportprovider1.getName());
            } else {
                HashCache.a hashcache_a = new HashCache.a(this.versionId, hashcache_b);

                this.existingCaches.put(debugreportprovider1, hashcache_a.newCache);
                return hashcache_a;
            }
        });
    }

    public void purgeStaleAndWrite() throws IOException {
        MutableInt mutableint = new MutableInt();

        this.cachesToWrite.forEach((debugreportprovider, hashcache_a) -> {
            Path path = this.getProviderCachePath(debugreportprovider);
            HashCache.b hashcache_b = hashcache_a.newCache;
            Path path1 = this.rootDir;
            String s = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());

            hashcache_b.save(path1, path, s + "\t" + debugreportprovider.getName());
            mutableint.add(hashcache_a.writes);
        });
        Set<Path> set = new HashSet();

        this.existingCaches.values().forEach((hashcache_b) -> {
            set.addAll(hashcache_b.data().keySet());
        });
        set.add(this.rootDir.resolve("version.json"));
        MutableInt mutableint1 = new MutableInt();
        MutableInt mutableint2 = new MutableInt();
        Stream stream = Files.walk(this.rootDir);

        try {
            stream.forEach((path) -> {
                if (!Files.isDirectory(path, new LinkOption[0])) {
                    if (!this.cachePaths.contains(path)) {
                        mutableint1.increment();
                        if (!set.contains(path)) {
                            try {
                                Files.delete(path);
                            } catch (IOException ioexception) {
                                HashCache.LOGGER.warn("Failed to delete file {}", path, ioexception);
                            }

                            mutableint2.increment();
                        }
                    }
                }
            });
        } catch (Throwable throwable) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            }

            throw throwable;
        }

        if (stream != null) {
            stream.close();
        }

        HashCache.LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", new Object[]{mutableint1, this.initialCount, set.size(), mutableint2, mutableint});
    }

    private static record b(String version, Map<Path, HashCode> data) {

        b(String s) {
            this(s, new HashMap());
        }

        @Nullable
        public HashCode get(Path path) {
            return (HashCode) this.data.get(path);
        }

        public void put(Path path, HashCode hashcode) {
            this.data.put(path, hashcode);
        }

        public int count() {
            return this.data.size();
        }

        public static HashCache.b load(Path path, Path path1) throws IOException {
            BufferedReader bufferedreader = Files.newBufferedReader(path1, StandardCharsets.UTF_8);

            HashCache.b hashcache_b;

            try {
                String s = bufferedreader.readLine();

                if (!s.startsWith("// ")) {
                    throw new IllegalStateException("Missing cache file header");
                }

                String[] astring = s.substring("// ".length()).split("\t", 2);
                String s1 = astring[0];
                Map<Path, HashCode> map = new HashMap();

                bufferedreader.lines().forEach((s2) -> {
                    int i = s2.indexOf(32);

                    map.put(path.resolve(s2.substring(i + 1)), HashCode.fromString(s2.substring(0, i)));
                });
                hashcache_b = new HashCache.b(s1, Map.copyOf(map));
            } catch (Throwable throwable) {
                if (bufferedreader != null) {
                    try {
                        bufferedreader.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (bufferedreader != null) {
                bufferedreader.close();
            }

            return hashcache_b;
        }

        public void save(Path path, Path path1, String s) {
            try {
                BufferedWriter bufferedwriter = Files.newBufferedWriter(path1, StandardCharsets.UTF_8);

                try {
                    bufferedwriter.write("// ");
                    bufferedwriter.write(this.version);
                    bufferedwriter.write(9);
                    bufferedwriter.write(s);
                    bufferedwriter.newLine();
                    Iterator iterator = this.data.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Entry<Path, HashCode> entry = (Entry) iterator.next();

                        bufferedwriter.write(((HashCode) entry.getValue()).toString());
                        bufferedwriter.write(32);
                        bufferedwriter.write(path.relativize((Path) entry.getKey()).toString());
                        bufferedwriter.newLine();
                    }
                } catch (Throwable throwable) {
                    if (bufferedwriter != null) {
                        try {
                            bufferedwriter.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    }

                    throw throwable;
                }

                if (bufferedwriter != null) {
                    bufferedwriter.close();
                }
            } catch (IOException ioexception) {
                HashCache.LOGGER.warn("Unable write cachefile {}: {}", path1, ioexception);
            }

        }
    }

    private static class a implements CachedOutput {

        private final HashCache.b oldCache;
        final HashCache.b newCache;
        int writes;

        a(String s, HashCache.b hashcache_b) {
            this.oldCache = hashcache_b;
            this.newCache = new HashCache.b(s);
        }

        private boolean shouldWrite(Path path, HashCode hashcode) {
            return !Objects.equals(this.oldCache.get(path), hashcode) || !Files.exists(path, new LinkOption[0]);
        }

        @Override
        public void writeIfNeeded(Path path, byte[] abyte, HashCode hashcode) throws IOException {
            if (this.shouldWrite(path, hashcode)) {
                ++this.writes;
                Files.createDirectories(path.getParent());
                Files.write(path, abyte, new OpenOption[0]);
            }

            this.newCache.put(path, hashcode);
        }
    }
}
