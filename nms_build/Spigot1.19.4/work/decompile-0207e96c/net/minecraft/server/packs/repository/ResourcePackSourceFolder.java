package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.ResourcePackFile;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import org.slf4j.Logger;

public class ResourcePackSourceFolder implements ResourcePackSource {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path folder;
    private final EnumResourcePackType packType;
    private final PackSource packSource;

    public ResourcePackSourceFolder(Path path, EnumResourcePackType enumresourcepacktype, PackSource packsource) {
        this.folder = path;
        this.packType = enumresourcepacktype;
        this.packSource = packsource;
    }

    private static String nameFromPath(Path path) {
        return path.getFileName().toString();
    }

    @Override
    public void loadPacks(Consumer<ResourcePackLoader> consumer) {
        try {
            FileUtils.createDirectoriesSafe(this.folder);
            discoverPacks(this.folder, false, (path, resourcepackloader_c) -> {
                String s = nameFromPath(path);
                ResourcePackLoader resourcepackloader = ResourcePackLoader.readMetaAndCreate("file/" + s, IChatBaseComponent.literal(s), false, resourcepackloader_c, this.packType, ResourcePackLoader.Position.TOP, this.packSource);

                if (resourcepackloader != null) {
                    consumer.accept(resourcepackloader);
                }

            });
        } catch (IOException ioexception) {
            ResourcePackSourceFolder.LOGGER.warn("Failed to list packs in {}", this.folder, ioexception);
        }

    }

    public static void discoverPacks(Path path, boolean flag, BiConsumer<Path, ResourcePackLoader.c> biconsumer) throws IOException {
        DirectoryStream directorystream = Files.newDirectoryStream(path);

        try {
            Iterator iterator = directorystream.iterator();

            while (iterator.hasNext()) {
                Path path1 = (Path) iterator.next();
                ResourcePackLoader.c resourcepackloader_c = detectPackResources(path1, flag);

                if (resourcepackloader_c != null) {
                    biconsumer.accept(path1, resourcepackloader_c);
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

    }

    @Nullable
    public static ResourcePackLoader.c detectPackResources(Path path, boolean flag) {
        BasicFileAttributes basicfileattributes;

        try {
            basicfileattributes = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (NoSuchFileException nosuchfileexception) {
            return null;
        } catch (IOException ioexception) {
            ResourcePackSourceFolder.LOGGER.warn("Failed to read properties of '{}', ignoring", path, ioexception);
            return null;
        }

        if (basicfileattributes.isDirectory() && Files.isRegularFile(path.resolve("pack.mcmeta"), new LinkOption[0])) {
            return (s) -> {
                return new PathPackResources(s, path, flag);
            };
        } else {
            if (basicfileattributes.isRegularFile() && path.getFileName().toString().endsWith(".zip")) {
                FileSystem filesystem = path.getFileSystem();

                if (filesystem == FileSystems.getDefault() || filesystem instanceof LinkFileSystem) {
                    File file = path.toFile();

                    return (s) -> {
                        return new ResourcePackFile(s, file, flag);
                    };
                }
            }

            ResourcePackSourceFolder.LOGGER.info("Found non-pack entry '{}', ignoring", path);
            return null;
        }
    }
}
