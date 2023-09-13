package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HashCache {

    private static final Logger LOGGER = LogManager.getLogger();
    private final java.nio.file.Path b;
    private final java.nio.file.Path c;
    private int d;
    private final Map<java.nio.file.Path, String> e = Maps.newHashMap();
    private final Map<java.nio.file.Path, String> f = Maps.newHashMap();
    private final Set<java.nio.file.Path> g = Sets.newHashSet();

    public HashCache(java.nio.file.Path java_nio_file_path, String s) throws IOException {
        this.b = java_nio_file_path;
        java.nio.file.Path java_nio_file_path1 = java_nio_file_path.resolve(".cache");

        Files.createDirectories(java_nio_file_path1);
        this.c = java_nio_file_path1.resolve(s);
        this.c().forEach((java_nio_file_path2) -> {
            String s1 = (String) this.e.put(java_nio_file_path2, "");
        });
        if (Files.isReadable(this.c)) {
            IOUtils.readLines(Files.newInputStream(this.c), Charsets.UTF_8).forEach((s1) -> {
                int i = s1.indexOf(32);

                this.e.put(java_nio_file_path.resolve(s1.substring(i + 1)), s1.substring(0, i));
            });
        }

    }

    public void a() throws IOException {
        this.b();

        BufferedWriter bufferedwriter;

        try {
            bufferedwriter = Files.newBufferedWriter(this.c);
        } catch (IOException ioexception) {
            HashCache.LOGGER.warn("Unable write cachefile {}: {}", this.c, ioexception.toString());
            return;
        }

        IOUtils.writeLines((Collection) this.f.entrySet().stream().map((entry) -> {
            return (String) entry.getValue() + ' ' + this.b.relativize((java.nio.file.Path) entry.getKey());
        }).collect(Collectors.toList()), System.lineSeparator(), bufferedwriter);
        bufferedwriter.close();
        HashCache.LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.d, this.f.size() - this.d, this.e.size());
    }

    @Nullable
    public String a(java.nio.file.Path java_nio_file_path) {
        return (String) this.e.get(java_nio_file_path);
    }

    public void a(java.nio.file.Path java_nio_file_path, String s) {
        this.f.put(java_nio_file_path, s);
        if (Objects.equals(this.e.remove(java_nio_file_path), s)) {
            ++this.d;
        }

    }

    public boolean b(java.nio.file.Path java_nio_file_path) {
        return this.e.containsKey(java_nio_file_path);
    }

    public void c(java.nio.file.Path java_nio_file_path) {
        this.g.add(java_nio_file_path);
    }

    private void b() throws IOException {
        this.c().forEach((java_nio_file_path) -> {
            if (this.b(java_nio_file_path) && !this.g.contains(java_nio_file_path)) {
                try {
                    Files.delete(java_nio_file_path);
                } catch (IOException ioexception) {
                    HashCache.LOGGER.debug("Unable to delete: {} ({})", java_nio_file_path, ioexception.toString());
                }
            }

        });
    }

    private Stream<java.nio.file.Path> c() throws IOException {
        return Files.walk(this.b).filter((java_nio_file_path) -> {
            return !Objects.equals(this.c, java_nio_file_path) && !Files.isDirectory(java_nio_file_path, new LinkOption[0]);
        });
    }
}
