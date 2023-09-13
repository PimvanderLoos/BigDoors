package net.minecraft.server.packs;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackVanilla implements IResourcePack {

    public static Path a;
    private static final Logger LOGGER = LogManager.getLogger();
    public static Class<?> b;
    private static final Map<EnumResourcePackType, FileSystem> e = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        Class oclass = ResourcePackVanilla.class;

        synchronized (ResourcePackVanilla.class) {
            EnumResourcePackType[] aenumresourcepacktype = EnumResourcePackType.values();
            int i = aenumresourcepacktype.length;

            for (int j = 0; j < i; ++j) {
                EnumResourcePackType enumresourcepacktype = aenumresourcepacktype[j];
                URL url = ResourcePackVanilla.class.getResource("/" + enumresourcepacktype.a() + "/.mcassetsroot");

                try {
                    URI uri = url.toURI();

                    if ("jar".equals(uri.getScheme())) {
                        FileSystem filesystem;

                        try {
                            filesystem = FileSystems.getFileSystem(uri);
                        } catch (FileSystemNotFoundException filesystemnotfoundexception) {
                            filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                        }

                        hashmap.put(enumresourcepacktype, filesystem);
                    }
                } catch (IOException | URISyntaxException urisyntaxexception) {
                    ResourcePackVanilla.LOGGER.error("Couldn't get a list of all vanilla resources", urisyntaxexception);
                }
            }

        }
    });
    public final Set<String> c;

    public ResourcePackVanilla(String... astring) {
        this.c = ImmutableSet.copyOf(astring);
    }

    public InputStream b(String s) throws IOException {
        if (!s.contains("/") && !s.contains("\\")) {
            if (ResourcePackVanilla.a != null) {
                Path path = ResourcePackVanilla.a.resolve(s);

                if (Files.exists(path, new LinkOption[0])) {
                    return Files.newInputStream(path);
                }
            }

            return this.a(s);
        } else {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
    }

    @Override
    public InputStream a(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) throws IOException {
        InputStream inputstream = this.c(enumresourcepacktype, minecraftkey);

        if (inputstream != null) {
            return inputstream;
        } else {
            throw new FileNotFoundException(minecraftkey.getKey());
        }
    }

    @Override
    public Collection<MinecraftKey> a(EnumResourcePackType enumresourcepacktype, String s, String s1, int i, Predicate<String> predicate) {
        Set<MinecraftKey> set = Sets.newHashSet();
        URI uri;

        if (ResourcePackVanilla.a != null) {
            try {
                a(set, i, s, ResourcePackVanilla.a.resolve(enumresourcepacktype.a()), s1, predicate);
            } catch (IOException ioexception) {
                ;
            }

            if (enumresourcepacktype == EnumResourcePackType.CLIENT_RESOURCES) {
                Enumeration enumeration = null;

                try {
                    enumeration = ResourcePackVanilla.b.getClassLoader().getResources(enumresourcepacktype.a() + "/");
                } catch (IOException ioexception1) {
                    ;
                }

                while (enumeration != null && enumeration.hasMoreElements()) {
                    try {
                        uri = ((URL) enumeration.nextElement()).toURI();
                        if ("file".equals(uri.getScheme())) {
                            a(set, i, s, Paths.get(uri), s1, predicate);
                        }
                    } catch (IOException | URISyntaxException urisyntaxexception) {
                        ;
                    }
                }
            }
        }

        try {
            URL url = ResourcePackVanilla.class.getResource("/" + enumresourcepacktype.a() + "/.mcassetsroot");

            if (url == null) {
                ResourcePackVanilla.LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
                return set;
            }

            uri = url.toURI();
            if ("file".equals(uri.getScheme())) {
                URL url1 = new URL(url.toString().substring(0, url.toString().length() - ".mcassetsroot".length()));
                Path path = Paths.get(url1.toURI());

                a(set, i, s, path, s1, predicate);
            } else if ("jar".equals(uri.getScheme())) {
                Path path1 = ((FileSystem) ResourcePackVanilla.e.get(enumresourcepacktype)).getPath("/" + enumresourcepacktype.a());

                a(set, i, "minecraft", path1, s1, predicate);
            } else {
                ResourcePackVanilla.LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", uri);
            }
        } catch (NoSuchFileException | FileNotFoundException filenotfoundexception) {
            ;
        } catch (IOException | URISyntaxException urisyntaxexception1) {
            ResourcePackVanilla.LOGGER.error("Couldn't get a list of all vanilla resources", urisyntaxexception1);
        }

        return set;
    }

    private static void a(Collection<MinecraftKey> collection, int i, String s, Path path, String s1, Predicate<String> predicate) throws IOException {
        Path path1 = path.resolve(s);
        Stream<Path> stream = Files.walk(path1.resolve(s1), i, new FileVisitOption[0]);
        Throwable throwable = null;

        try {
            stream.filter((path2) -> {
                return !path2.endsWith(".mcmeta") && Files.isRegularFile(path2, new LinkOption[0]) && predicate.test(path2.getFileName().toString());
            }).map((path2) -> {
                return new MinecraftKey(s, path1.relativize(path2).toString().replaceAll("\\\\", "/"));
            }).forEach(collection::add);
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (stream != null) {
                if (throwable != null) {
                    try {
                        stream.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    stream.close();
                }
            }

        }

    }

    @Nullable
    protected InputStream c(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        String s = d(enumresourcepacktype, minecraftkey);

        if (ResourcePackVanilla.a != null) {
            Path path = ResourcePackVanilla.a.resolve(enumresourcepacktype.a() + "/" + minecraftkey.getNamespace() + "/" + minecraftkey.getKey());

            if (Files.exists(path, new LinkOption[0])) {
                try {
                    return Files.newInputStream(path);
                } catch (IOException ioexception) {
                    ;
                }
            }
        }

        try {
            URL url = ResourcePackVanilla.class.getResource(s);

            return a(s, url) ? url.openStream() : null;
        } catch (IOException ioexception1) {
            return ResourcePackVanilla.class.getResourceAsStream(s);
        }
    }

    private static String d(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        return "/" + enumresourcepacktype.a() + "/" + minecraftkey.getNamespace() + "/" + minecraftkey.getKey();
    }

    private static boolean a(String s, @Nullable URL url) throws IOException {
        return url != null && (url.getProtocol().equals("jar") || ResourcePackFolder.a(new File(url.getFile()), s));
    }

    @Nullable
    protected InputStream a(String s) {
        return ResourcePackVanilla.class.getResourceAsStream("/" + s);
    }

    @Override
    public boolean b(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        String s = d(enumresourcepacktype, minecraftkey);

        if (ResourcePackVanilla.a != null) {
            Path path = ResourcePackVanilla.a.resolve(enumresourcepacktype.a() + "/" + minecraftkey.getNamespace() + "/" + minecraftkey.getKey());

            if (Files.exists(path, new LinkOption[0])) {
                return true;
            }
        }

        try {
            URL url = ResourcePackVanilla.class.getResource(s);

            return a(s, url);
        } catch (IOException ioexception) {
            return false;
        }
    }

    @Override
    public Set<String> a(EnumResourcePackType enumresourcepacktype) {
        return this.c;
    }

    @Nullable
    @Override
    public <T> T a(ResourcePackMetaParser<T> resourcepackmetaparser) throws IOException {
        try {
            InputStream inputstream = this.b("pack.mcmeta");
            Throwable throwable = null;

            Object object;

            try {
                object = ResourcePackAbstract.a(resourcepackmetaparser, inputstream);
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (inputstream != null) {
                    if (throwable != null) {
                        try {
                            inputstream.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        inputstream.close();
                    }
                }

            }

            return object;
        } catch (FileNotFoundException | RuntimeException runtimeexception) {
            return null;
        }
    }

    @Override
    public String a() {
        return "Default";
    }

    @Override
    public void close() {}
}
