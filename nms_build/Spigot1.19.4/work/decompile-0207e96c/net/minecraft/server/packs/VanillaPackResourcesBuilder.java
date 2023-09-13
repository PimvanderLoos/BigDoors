package net.minecraft.server.packs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.SystemUtils;
import org.slf4j.Logger;

public class VanillaPackResourcesBuilder {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static Consumer<VanillaPackResourcesBuilder> developmentConfig = (vanillapackresourcesbuilder) -> {
    };
    private static final Map<EnumResourcePackType, Path> ROOT_DIR_BY_TYPE = (Map) SystemUtils.make(() -> {
        Class oclass = ResourcePackVanilla.class;

        synchronized (ResourcePackVanilla.class) {
            Builder<EnumResourcePackType, Path> builder = ImmutableMap.builder();
            EnumResourcePackType[] aenumresourcepacktype = EnumResourcePackType.values();
            int i = aenumresourcepacktype.length;

            for (int j = 0; j < i; ++j) {
                EnumResourcePackType enumresourcepacktype = aenumresourcepacktype[j];
                String s = "/" + enumresourcepacktype.getDirectory() + "/.mcassetsroot";
                URL url = ResourcePackVanilla.class.getResource(s);

                if (url == null) {
                    VanillaPackResourcesBuilder.LOGGER.error("File {} does not exist in classpath", s);
                } else {
                    try {
                        URI uri = url.toURI();
                        String s1 = uri.getScheme();

                        if (!"jar".equals(s1) && !"file".equals(s1)) {
                            VanillaPackResourcesBuilder.LOGGER.warn("Assets URL '{}' uses unexpected schema", uri);
                        }

                        Path path = safeGetPath(uri);

                        builder.put(enumresourcepacktype, path.getParent());
                    } catch (Exception exception) {
                        VanillaPackResourcesBuilder.LOGGER.error("Couldn't resolve path to vanilla assets", exception);
                    }
                }
            }

            return builder.build();
        }
    });
    private final Set<Path> rootPaths = new LinkedHashSet();
    private final Map<EnumResourcePackType, Set<Path>> pathsForType = new EnumMap(EnumResourcePackType.class);
    private BuiltInMetadata metadata = BuiltInMetadata.of();
    private final Set<String> namespaces = new HashSet();

    public VanillaPackResourcesBuilder() {}

    private static Path safeGetPath(URI uri) throws IOException {
        try {
            return Paths.get(uri);
        } catch (FileSystemNotFoundException filesystemnotfoundexception) {
            ;
        } catch (Throwable throwable) {
            VanillaPackResourcesBuilder.LOGGER.warn("Unable to get path for: {}", uri, throwable);
        }

        try {
            FileSystems.newFileSystem(uri, Collections.emptyMap());
        } catch (FileSystemAlreadyExistsException filesystemalreadyexistsexception) {
            ;
        }

        return Paths.get(uri);
    }

    private boolean validateDirPath(Path path) {
        if (!Files.exists(path, new LinkOption[0])) {
            return false;
        } else if (!Files.isDirectory(path, new LinkOption[0])) {
            throw new IllegalArgumentException("Path " + path.toAbsolutePath() + " is not directory");
        } else {
            return true;
        }
    }

    private void pushRootPath(Path path) {
        if (this.validateDirPath(path)) {
            this.rootPaths.add(path);
        }

    }

    private void pushPathForType(EnumResourcePackType enumresourcepacktype, Path path) {
        if (this.validateDirPath(path)) {
            ((Set) this.pathsForType.computeIfAbsent(enumresourcepacktype, (enumresourcepacktype1) -> {
                return new LinkedHashSet();
            })).add(path);
        }

    }

    public VanillaPackResourcesBuilder pushJarResources() {
        VanillaPackResourcesBuilder.ROOT_DIR_BY_TYPE.forEach((enumresourcepacktype, path) -> {
            this.pushRootPath(path.getParent());
            this.pushPathForType(enumresourcepacktype, path);
        });
        return this;
    }

    public VanillaPackResourcesBuilder pushClasspathResources(EnumResourcePackType enumresourcepacktype, Class<?> oclass) {
        Enumeration enumeration = null;

        try {
            enumeration = oclass.getClassLoader().getResources(enumresourcepacktype.getDirectory() + "/");
        } catch (IOException ioexception) {
            ;
        }

        while (enumeration != null && enumeration.hasMoreElements()) {
            URL url = (URL) enumeration.nextElement();

            try {
                URI uri = url.toURI();

                if ("file".equals(uri.getScheme())) {
                    Path path = Paths.get(uri);

                    this.pushRootPath(path.getParent());
                    this.pushPathForType(enumresourcepacktype, path);
                }
            } catch (Exception exception) {
                VanillaPackResourcesBuilder.LOGGER.error("Failed to extract path from {}", url, exception);
            }
        }

        return this;
    }

    public VanillaPackResourcesBuilder applyDevelopmentConfig() {
        VanillaPackResourcesBuilder.developmentConfig.accept(this);
        return this;
    }

    public VanillaPackResourcesBuilder pushUniversalPath(Path path) {
        this.pushRootPath(path);
        EnumResourcePackType[] aenumresourcepacktype = EnumResourcePackType.values();
        int i = aenumresourcepacktype.length;

        for (int j = 0; j < i; ++j) {
            EnumResourcePackType enumresourcepacktype = aenumresourcepacktype[j];

            this.pushPathForType(enumresourcepacktype, path.resolve(enumresourcepacktype.getDirectory()));
        }

        return this;
    }

    public VanillaPackResourcesBuilder pushAssetPath(EnumResourcePackType enumresourcepacktype, Path path) {
        this.pushRootPath(path);
        this.pushPathForType(enumresourcepacktype, path);
        return this;
    }

    public VanillaPackResourcesBuilder setMetadata(BuiltInMetadata builtinmetadata) {
        this.metadata = builtinmetadata;
        return this;
    }

    public VanillaPackResourcesBuilder exposeNamespace(String... astring) {
        this.namespaces.addAll(Arrays.asList(astring));
        return this;
    }

    public ResourcePackVanilla build() {
        Map<EnumResourcePackType, List<Path>> map = new EnumMap(EnumResourcePackType.class);
        EnumResourcePackType[] aenumresourcepacktype = EnumResourcePackType.values();
        int i = aenumresourcepacktype.length;

        for (int j = 0; j < i; ++j) {
            EnumResourcePackType enumresourcepacktype = aenumresourcepacktype[j];
            List<Path> list = copyAndReverse((Collection) this.pathsForType.getOrDefault(enumresourcepacktype, Set.of()));

            map.put(enumresourcepacktype, list);
        }

        return new ResourcePackVanilla(this.metadata, Set.copyOf(this.namespaces), copyAndReverse(this.rootPaths), map);
    }

    private static List<Path> copyAndReverse(Collection<Path> collection) {
        List<Path> list = new ArrayList(collection);

        Collections.reverse(list);
        return List.copyOf(list);
    }
}
