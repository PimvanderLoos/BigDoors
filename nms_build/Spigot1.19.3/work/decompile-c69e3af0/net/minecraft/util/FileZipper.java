package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import org.slf4j.Logger;

public class FileZipper implements Closeable {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path outputFile;
    private final Path tempFile;
    private final FileSystem fs;

    public FileZipper(Path path) {
        this.outputFile = path;
        this.tempFile = path.resolveSibling(path.getFileName().toString() + "_tmp");

        try {
            this.fs = SystemUtils.ZIP_FILE_SYSTEM_PROVIDER.newFileSystem(this.tempFile, ImmutableMap.of("create", "true"));
        } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
        }
    }

    public void add(Path path, String s) {
        try {
            Path path1 = this.fs.getPath(File.separator);
            Path path2 = path1.resolve(path.toString());

            Files.createDirectories(path2.getParent());
            Files.write(path2, s.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
        } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
        }
    }

    public void add(Path path, File file) {
        try {
            Path path1 = this.fs.getPath(File.separator);
            Path path2 = path1.resolve(path.toString());

            Files.createDirectories(path2.getParent());
            Files.copy(file.toPath(), path2);
        } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
        }
    }

    public void add(Path path) {
        try {
            Path path1 = this.fs.getPath(File.separator);

            if (Files.isRegularFile(path, new LinkOption[0])) {
                Path path2 = path1.resolve(path.getParent().relativize(path).toString());

                Files.copy(path2, path);
            } else {
                Stream stream = Files.find(path, Integer.MAX_VALUE, (path3, basicfileattributes) -> {
                    return basicfileattributes.isRegularFile();
                }, new FileVisitOption[0]);

                try {
                    Iterator iterator = ((List) stream.collect(Collectors.toList())).iterator();

                    while (iterator.hasNext()) {
                        Path path3 = (Path) iterator.next();
                        Path path4 = path1.resolve(path.relativize(path3).toString());

                        Files.createDirectories(path4.getParent());
                        Files.copy(path3, path4);
                    }
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

            }
        } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
        }
    }

    public void close() {
        try {
            this.fs.close();
            Files.move(this.tempFile, this.outputFile);
            FileZipper.LOGGER.info("Compressed to {}", this.outputFile);
        } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
        }
    }
}
