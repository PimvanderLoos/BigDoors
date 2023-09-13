package net.minecraft.server.packs.linkfs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

class LinkFSProvider extends FileSystemProvider {

    public static final String SCHEME = "x-mc-link";

    LinkFSProvider() {}

    public String getScheme() {
        return "x-mc-link";
    }

    public FileSystem newFileSystem(URI uri, Map<String, ?> map) {
        throw new UnsupportedOperationException();
    }

    public FileSystem getFileSystem(URI uri) {
        throw new UnsupportedOperationException();
    }

    public Path getPath(URI uri) {
        throw new UnsupportedOperationException();
    }

    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> set, FileAttribute<?>... afileattribute) throws IOException {
        if (!set.contains(StandardOpenOption.CREATE_NEW) && !set.contains(StandardOpenOption.CREATE) && !set.contains(StandardOpenOption.APPEND) && !set.contains(StandardOpenOption.WRITE)) {
            Path path1 = toLinkPath(path).toAbsolutePath().getTargetPath();

            if (path1 == null) {
                throw new NoSuchFileException(path.toString());
            } else {
                return Files.newByteChannel(path1, set, afileattribute);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public DirectoryStream<Path> newDirectoryStream(Path path, final Filter<? super Path> filter) throws IOException {
        final PathContents.a pathcontents_a = toLinkPath(path).toAbsolutePath().getDirectoryContents();

        if (pathcontents_a == null) {
            throw new NotDirectoryException(path.toString());
        } else {
            return new DirectoryStream<Path>() {
                public Iterator<Path> iterator() {
                    return pathcontents_a.children().values().stream().filter((linkfspath) -> {
                        try {
                            return filter.accept(linkfspath);
                        } catch (IOException ioexception) {
                            throw new DirectoryIteratorException(ioexception);
                        }
                    }).map((linkfspath) -> {
                        return linkfspath;
                    }).iterator();
                }

                public void close() {}
            };
        }
    }

    public void createDirectory(Path path, FileAttribute<?>... afileattribute) {
        throw new ReadOnlyFileSystemException();
    }

    public void delete(Path path) {
        throw new ReadOnlyFileSystemException();
    }

    public void copy(Path path, Path path1, CopyOption... acopyoption) {
        throw new ReadOnlyFileSystemException();
    }

    public void move(Path path, Path path1, CopyOption... acopyoption) {
        throw new ReadOnlyFileSystemException();
    }

    public boolean isSameFile(Path path, Path path1) {
        return path instanceof LinkFSPath && path1 instanceof LinkFSPath && path.equals(path1);
    }

    public boolean isHidden(Path path) {
        return false;
    }

    public FileStore getFileStore(Path path) {
        return toLinkPath(path).getFileSystem().store();
    }

    public void checkAccess(Path path, AccessMode... aaccessmode) throws IOException {
        if (aaccessmode.length == 0 && !toLinkPath(path).exists()) {
            throw new NoSuchFileException(path.toString());
        } else {
            AccessMode[] aaccessmode1 = aaccessmode;
            int i = aaccessmode.length;
            int j = 0;

            while (j < i) {
                AccessMode accessmode = aaccessmode1[j];

                switch (accessmode) {
                    case READ:
                        if (!toLinkPath(path).exists()) {
                            throw new NoSuchFileException(path.toString());
                        }
                    default:
                        ++j;
                        break;
                    case EXECUTE:
                    case WRITE:
                        throw new AccessDeniedException(accessmode.toString());
                }
            }

        }
    }

    @Nullable
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> oclass, LinkOption... alinkoption) {
        LinkFSPath linkfspath = toLinkPath(path);

        return oclass == BasicFileAttributeView.class ? linkfspath.getBasicAttributeView() : null;
    }

    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> oclass, LinkOption... alinkoption) throws IOException {
        LinkFSPath linkfspath = toLinkPath(path).toAbsolutePath();

        if (oclass == BasicFileAttributes.class) {
            return linkfspath.getBasicAttributes();
        } else {
            throw new UnsupportedOperationException("Attributes of type " + oclass.getName() + " not supported");
        }
    }

    public Map<String, Object> readAttributes(Path path, String s, LinkOption... alinkoption) {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(Path path, String s, Object object, LinkOption... alinkoption) {
        throw new ReadOnlyFileSystemException();
    }

    private static LinkFSPath toLinkPath(@Nullable Path path) {
        if (path == null) {
            throw new NullPointerException();
        } else if (path instanceof LinkFSPath) {
            LinkFSPath linkfspath = (LinkFSPath) path;

            return linkfspath;
        } else {
            throw new ProviderMismatchException();
        }
    }
}
