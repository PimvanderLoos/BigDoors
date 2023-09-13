package net.minecraft.server.packs.linkfs;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

class LinkFSPath implements Path {

    private static final BasicFileAttributes DIRECTORY_ATTRIBUTES = new DummyFileAttributes() {
        public boolean isRegularFile() {
            return false;
        }

        public boolean isDirectory() {
            return true;
        }
    };
    private static final BasicFileAttributes FILE_ATTRIBUTES = new DummyFileAttributes() {
        public boolean isRegularFile() {
            return true;
        }

        public boolean isDirectory() {
            return false;
        }
    };
    private static final Comparator<LinkFSPath> PATH_COMPARATOR = Comparator.comparing(LinkFSPath::pathToString);
    private final String name;
    private final LinkFileSystem fileSystem;
    @Nullable
    private final LinkFSPath parent;
    @Nullable
    private List<String> pathToRoot;
    @Nullable
    private String pathString;
    private final PathContents pathContents;

    public LinkFSPath(LinkFileSystem linkfilesystem, String s, @Nullable LinkFSPath linkfspath, PathContents pathcontents) {
        this.fileSystem = linkfilesystem;
        this.name = s;
        this.parent = linkfspath;
        this.pathContents = pathcontents;
    }

    private LinkFSPath createRelativePath(@Nullable LinkFSPath linkfspath, String s) {
        return new LinkFSPath(this.fileSystem, s, linkfspath, PathContents.RELATIVE);
    }

    public LinkFileSystem getFileSystem() {
        return this.fileSystem;
    }

    public boolean isAbsolute() {
        return this.pathContents != PathContents.RELATIVE;
    }

    public File toFile() {
        PathContents pathcontents = this.pathContents;

        if (pathcontents instanceof PathContents.b) {
            PathContents.b pathcontents_b = (PathContents.b) pathcontents;

            return pathcontents_b.contents().toFile();
        } else {
            throw new UnsupportedOperationException("Path " + this.pathToString() + " does not represent file");
        }
    }

    @Nullable
    public LinkFSPath getRoot() {
        return this.isAbsolute() ? this.fileSystem.rootPath() : null;
    }

    public LinkFSPath getFileName() {
        return this.createRelativePath((LinkFSPath) null, this.name);
    }

    @Nullable
    public LinkFSPath getParent() {
        return this.parent;
    }

    public int getNameCount() {
        return this.pathToRoot().size();
    }

    private List<String> pathToRoot() {
        if (this.name.isEmpty()) {
            return List.of();
        } else {
            if (this.pathToRoot == null) {
                Builder<String> builder = ImmutableList.builder();

                if (this.parent != null) {
                    builder.addAll(this.parent.pathToRoot());
                }

                builder.add(this.name);
                this.pathToRoot = builder.build();
            }

            return this.pathToRoot;
        }
    }

    public LinkFSPath getName(int i) {
        List<String> list = this.pathToRoot();

        if (i >= 0 && i < list.size()) {
            return this.createRelativePath((LinkFSPath) null, (String) list.get(i));
        } else {
            throw new IllegalArgumentException("Invalid index: " + i);
        }
    }

    public LinkFSPath subpath(int i, int j) {
        List<String> list = this.pathToRoot();

        if (i >= 0 && j <= list.size() && i < j) {
            LinkFSPath linkfspath = null;

            for (int k = i; k < j; ++k) {
                linkfspath = this.createRelativePath(linkfspath, (String) list.get(k));
            }

            return linkfspath;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean startsWith(Path path) {
        if (path.isAbsolute() != this.isAbsolute()) {
            return false;
        } else if (path instanceof LinkFSPath) {
            LinkFSPath linkfspath = (LinkFSPath) path;

            if (linkfspath.fileSystem != this.fileSystem) {
                return false;
            } else {
                List<String> list = this.pathToRoot();
                List<String> list1 = linkfspath.pathToRoot();
                int i = list1.size();

                if (i > list.size()) {
                    return false;
                } else {
                    for (int j = 0; j < i; ++j) {
                        if (!((String) list1.get(j)).equals(list.get(j))) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public boolean endsWith(Path path) {
        if (path.isAbsolute() && !this.isAbsolute()) {
            return false;
        } else if (path instanceof LinkFSPath) {
            LinkFSPath linkfspath = (LinkFSPath) path;

            if (linkfspath.fileSystem != this.fileSystem) {
                return false;
            } else {
                List<String> list = this.pathToRoot();
                List<String> list1 = linkfspath.pathToRoot();
                int i = list1.size();
                int j = list.size() - i;

                if (j < 0) {
                    return false;
                } else {
                    for (int k = i - 1; k >= 0; --k) {
                        if (!((String) list1.get(k)).equals(list.get(j + k))) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public LinkFSPath normalize() {
        return this;
    }

    public LinkFSPath resolve(Path path) {
        LinkFSPath linkfspath = this.toLinkPath(path);

        return path.isAbsolute() ? linkfspath : this.resolve(linkfspath.pathToRoot());
    }

    private LinkFSPath resolve(List<String> list) {
        LinkFSPath linkfspath = this;

        String s;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); linkfspath = linkfspath.resolveName(s)) {
            s = (String) iterator.next();
        }

        return linkfspath;
    }

    LinkFSPath resolveName(String s) {
        if (isRelativeOrMissing(this.pathContents)) {
            return new LinkFSPath(this.fileSystem, s, this, this.pathContents);
        } else {
            PathContents pathcontents = this.pathContents;

            if (pathcontents instanceof PathContents.a) {
                PathContents.a pathcontents_a = (PathContents.a) pathcontents;
                LinkFSPath linkfspath = (LinkFSPath) pathcontents_a.children().get(s);

                return linkfspath != null ? linkfspath : new LinkFSPath(this.fileSystem, s, this, PathContents.MISSING);
            } else if (this.pathContents instanceof PathContents.b) {
                return new LinkFSPath(this.fileSystem, s, this, PathContents.MISSING);
            } else {
                throw new AssertionError("All content types should be already handled");
            }
        }
    }

    private static boolean isRelativeOrMissing(PathContents pathcontents) {
        return pathcontents == PathContents.MISSING || pathcontents == PathContents.RELATIVE;
    }

    public LinkFSPath relativize(Path path) {
        LinkFSPath linkfspath = this.toLinkPath(path);

        if (this.isAbsolute() != linkfspath.isAbsolute()) {
            throw new IllegalArgumentException("absolute mismatch");
        } else {
            List<String> list = this.pathToRoot();
            List<String> list1 = linkfspath.pathToRoot();

            if (list.size() >= list1.size()) {
                throw new IllegalArgumentException();
            } else {
                for (int i = 0; i < list.size(); ++i) {
                    if (!((String) list.get(i)).equals(list1.get(i))) {
                        throw new IllegalArgumentException();
                    }
                }

                return linkfspath.subpath(list.size(), list1.size());
            }
        }
    }

    public URI toUri() {
        try {
            return new URI("x-mc-link", this.fileSystem.store().name(), this.pathToString(), (String) null);
        } catch (URISyntaxException urisyntaxexception) {
            throw new AssertionError("Failed to create URI", urisyntaxexception);
        }
    }

    public LinkFSPath toAbsolutePath() {
        return this.isAbsolute() ? this : this.fileSystem.rootPath().resolve((Path) this);
    }

    public LinkFSPath toRealPath(LinkOption... alinkoption) {
        return this.toAbsolutePath();
    }

    public WatchKey register(WatchService watchservice, Kind<?>[] akind, Modifier... amodifier) {
        throw new UnsupportedOperationException();
    }

    public int compareTo(Path path) {
        LinkFSPath linkfspath = this.toLinkPath(path);

        return LinkFSPath.PATH_COMPARATOR.compare(this, linkfspath);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof LinkFSPath) {
            LinkFSPath linkfspath = (LinkFSPath) object;

            if (this.fileSystem != linkfspath.fileSystem) {
                return false;
            } else {
                boolean flag = this.hasRealContents();

                return flag != linkfspath.hasRealContents() ? false : (flag ? this.pathContents == linkfspath.pathContents : Objects.equals(this.parent, linkfspath.parent) && Objects.equals(this.name, linkfspath.name));
            }
        } else {
            return false;
        }
    }

    private boolean hasRealContents() {
        return !isRelativeOrMissing(this.pathContents);
    }

    public int hashCode() {
        return this.hasRealContents() ? this.pathContents.hashCode() : this.name.hashCode();
    }

    public String toString() {
        return this.pathToString();
    }

    private String pathToString() {
        if (this.pathString == null) {
            StringBuilder stringbuilder = new StringBuilder();

            if (this.isAbsolute()) {
                stringbuilder.append("/");
            }

            Joiner.on("/").appendTo(stringbuilder, this.pathToRoot());
            this.pathString = stringbuilder.toString();
        }

        return this.pathString;
    }

    private LinkFSPath toLinkPath(@Nullable Path path) {
        if (path == null) {
            throw new NullPointerException();
        } else {
            if (path instanceof LinkFSPath) {
                LinkFSPath linkfspath = (LinkFSPath) path;

                if (linkfspath.fileSystem == this.fileSystem) {
                    return linkfspath;
                }
            }

            throw new ProviderMismatchException();
        }
    }

    public boolean exists() {
        return this.hasRealContents();
    }

    @Nullable
    public Path getTargetPath() {
        PathContents pathcontents = this.pathContents;
        Path path;

        if (pathcontents instanceof PathContents.b) {
            PathContents.b pathcontents_b = (PathContents.b) pathcontents;

            path = pathcontents_b.contents();
        } else {
            path = null;
        }

        return path;
    }

    @Nullable
    public PathContents.a getDirectoryContents() {
        PathContents pathcontents = this.pathContents;
        PathContents.a pathcontents_a;

        if (pathcontents instanceof PathContents.a) {
            PathContents.a pathcontents_a1 = (PathContents.a) pathcontents;

            pathcontents_a = pathcontents_a1;
        } else {
            pathcontents_a = null;
        }

        return pathcontents_a;
    }

    public BasicFileAttributeView getBasicAttributeView() {
        return new BasicFileAttributeView() {
            public String name() {
                return "basic";
            }

            public BasicFileAttributes readAttributes() throws IOException {
                return LinkFSPath.this.getBasicAttributes();
            }

            public void setTimes(FileTime filetime, FileTime filetime1, FileTime filetime2) {
                throw new ReadOnlyFileSystemException();
            }
        };
    }

    public BasicFileAttributes getBasicAttributes() throws IOException {
        if (this.pathContents instanceof PathContents.a) {
            return LinkFSPath.DIRECTORY_ATTRIBUTES;
        } else if (this.pathContents instanceof PathContents.b) {
            return LinkFSPath.FILE_ATTRIBUTES;
        } else {
            throw new NoSuchFileException(this.pathToString());
        }
    }
}
