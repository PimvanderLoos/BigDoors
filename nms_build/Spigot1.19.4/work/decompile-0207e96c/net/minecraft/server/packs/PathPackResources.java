package net.minecraft.server.packs;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtils;
import net.minecraft.SystemUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IoSupplier;
import org.slf4j.Logger;

public class PathPackResources extends ResourcePackAbstract {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Joiner PATH_JOINER = Joiner.on("/");
    private final Path root;

    public PathPackResources(String s, Path path, boolean flag) {
        super(s, flag);
        this.root = path;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... astring) {
        FileUtils.validatePath(astring);
        Path path = FileUtils.resolvePath(this.root, List.of(astring));

        return Files.exists(path, new LinkOption[0]) ? IoSupplier.create(path) : null;
    }

    public static boolean validatePath(Path path) {
        return true;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        Path path = this.root.resolve(enumresourcepacktype.getDirectory()).resolve(minecraftkey.getNamespace());

        return getResource(minecraftkey, path);
    }

    public static IoSupplier<InputStream> getResource(MinecraftKey minecraftkey, Path path) {
        return (IoSupplier) FileUtils.decomposePath(minecraftkey.getPath()).get().map((list) -> {
            Path path1 = FileUtils.resolvePath(path, list);

            return returnFileIfExists(path1);
        }, (partialresult) -> {
            PathPackResources.LOGGER.error("Invalid path {}: {}", minecraftkey, partialresult.message());
            return null;
        });
    }

    @Nullable
    private static IoSupplier<InputStream> returnFileIfExists(Path path) {
        return Files.exists(path, new LinkOption[0]) && validatePath(path) ? IoSupplier.create(path) : null;
    }

    @Override
    public void listResources(EnumResourcePackType enumresourcepacktype, String s, String s1, IResourcePack.a iresourcepack_a) {
        FileUtils.decomposePath(s1).get().ifLeft((list) -> {
            Path path = this.root.resolve(enumresourcepacktype.getDirectory()).resolve(s);

            listPath(s, path, list, iresourcepack_a);
        }).ifRight((partialresult) -> {
            PathPackResources.LOGGER.error("Invalid path {}: {}", s1, partialresult.message());
        });
    }

    public static void listPath(String s, Path path, List<String> list, IResourcePack.a iresourcepack_a) {
        Path path1 = FileUtils.resolvePath(path, list);

        try {
            Stream stream = Files.find(path1, Integer.MAX_VALUE, (path2, basicfileattributes) -> {
                return basicfileattributes.isRegularFile();
            }, new FileVisitOption[0]);

            try {
                stream.forEach((path2) -> {
                    String s1 = PathPackResources.PATH_JOINER.join(path.relativize(path2));
                    MinecraftKey minecraftkey = MinecraftKey.tryBuild(s, s1);

                    if (minecraftkey == null) {
                        SystemUtils.logAndPauseIfInIde(String.format(Locale.ROOT, "Invalid path in pack: %s:%s, ignoring", s, s1));
                    } else {
                        iresourcepack_a.accept(minecraftkey, IoSupplier.create(path2));
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
        } catch (NoSuchFileException nosuchfileexception) {
            ;
        } catch (IOException ioexception) {
            PathPackResources.LOGGER.error("Failed to list path {}", path1, ioexception);
        }

    }

    @Override
    public Set<String> getNamespaces(EnumResourcePackType enumresourcepacktype) {
        Set<String> set = Sets.newHashSet();
        Path path = this.root.resolve(enumresourcepacktype.getDirectory());

        try {
            DirectoryStream directorystream = Files.newDirectoryStream(path);

            try {
                Iterator iterator = directorystream.iterator();

                while (iterator.hasNext()) {
                    Path path1 = (Path) iterator.next();
                    String s = path1.getFileName().toString();

                    if (s.equals(s.toLowerCase(Locale.ROOT))) {
                        set.add(s);
                    } else {
                        PathPackResources.LOGGER.warn("Ignored non-lowercase namespace: {} in {}", s, this.root);
                    }
                }
            } catch (Throwable throwable) {
                if (directorystream != null) {
                    try {
                        directorystream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (directorystream != null) {
                directorystream.close();
            }
        } catch (NoSuchFileException nosuchfileexception) {
            ;
        } catch (IOException ioexception) {
            PathPackResources.LOGGER.error("Failed to list path {}", path, ioexception);
        }

        return set;
    }

    @Override
    public void close() {}
}
