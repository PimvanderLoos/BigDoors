package net.minecraft.world.level.chunk.storage;

import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.world.level.ChunkCoordIntPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFile implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int SECTOR_BYTES = 4096;
    @VisibleForTesting
    protected static final int SECTOR_INTS = 1024;
    private static final int CHUNK_HEADER_SIZE = 5;
    private static final int HEADER_OFFSET = 0;
    private static final ByteBuffer PADDING_BUFFER = ByteBuffer.allocateDirect(1);
    private static final String EXTERNAL_FILE_EXTENSION = ".mcc";
    private static final int EXTERNAL_STREAM_FLAG = 128;
    private static final int EXTERNAL_CHUNK_THRESHOLD = 256;
    private static final int CHUNK_NOT_PRESENT = 0;
    private final FileChannel file;
    private final Path externalFileDir;
    final RegionFileCompression version;
    private final ByteBuffer header;
    private final IntBuffer offsets;
    private final IntBuffer timestamps;
    @VisibleForTesting
    protected final RegionFileBitSet usedSectors;

    public RegionFile(File file, File file1, boolean flag) throws IOException {
        this(file.toPath(), file1.toPath(), RegionFileCompression.VERSION_DEFLATE, flag);
    }

    public RegionFile(Path path, Path path1, RegionFileCompression regionfilecompression, boolean flag) throws IOException {
        this.header = ByteBuffer.allocateDirect(8192);
        this.usedSectors = new RegionFileBitSet();
        this.version = regionfilecompression;
        if (!Files.isDirectory(path1, new LinkOption[0])) {
            throw new IllegalArgumentException("Expected directory, got " + path1.toAbsolutePath());
        } else {
            this.externalFileDir = path1;
            this.offsets = this.header.asIntBuffer();
            this.offsets.limit(1024);
            this.header.position(4096);
            this.timestamps = this.header.asIntBuffer();
            if (flag) {
                this.file = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DSYNC);
            } else {
                this.file = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
            }

            this.usedSectors.a(0, 2);
            this.header.position(0);
            int i = this.file.read(this.header, 0L);

            if (i != -1) {
                if (i != 8192) {
                    RegionFile.LOGGER.warn("Region file {} has truncated header: {}", path, i);
                }

                long j = Files.size(path);

                for (int k = 0; k < 1024; ++k) {
                    int l = this.offsets.get(k);

                    if (l != 0) {
                        int i1 = b(l);
                        int j1 = a(l);

                        if (i1 < 2) {
                            RegionFile.LOGGER.warn("Region file {} has invalid sector at index: {}; sector {} overlaps with header", path, k, i1);
                            this.offsets.put(k, 0);
                        } else if (j1 == 0) {
                            RegionFile.LOGGER.warn("Region file {} has an invalid sector at index: {}; size has to be > 0", path, k);
                            this.offsets.put(k, 0);
                        } else if ((long) i1 * 4096L > j) {
                            RegionFile.LOGGER.warn("Region file {} has an invalid sector at index: {}; sector {} is out of bounds", path, k, i1);
                            this.offsets.put(k, 0);
                        } else {
                            this.usedSectors.a(i1, j1);
                        }
                    }
                }
            }

        }
    }

    private Path f(ChunkCoordIntPair chunkcoordintpair) {
        String s = "c." + chunkcoordintpair.x + "." + chunkcoordintpair.z + ".mcc";

        return this.externalFileDir.resolve(s);
    }

    @Nullable
    public synchronized DataInputStream a(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        int i = this.getOffset(chunkcoordintpair);

        if (i == 0) {
            return null;
        } else {
            int j = b(i);
            int k = a(i);
            int l = k * 4096;
            ByteBuffer bytebuffer = ByteBuffer.allocate(l);

            this.file.read(bytebuffer, (long) (j * 4096));
            bytebuffer.flip();
            if (bytebuffer.remaining() < 5) {
                RegionFile.LOGGER.error("Chunk {} header is truncated: expected {} but read {}", chunkcoordintpair, l, bytebuffer.remaining());
                return null;
            } else {
                int i1 = bytebuffer.getInt();
                byte b0 = bytebuffer.get();

                if (i1 == 0) {
                    RegionFile.LOGGER.warn("Chunk {} is allocated, but stream is missing", chunkcoordintpair);
                    return null;
                } else {
                    int j1 = i1 - 1;

                    if (a(b0)) {
                        if (j1 != 0) {
                            RegionFile.LOGGER.warn("Chunk has both internal and external streams");
                        }

                        return this.a(chunkcoordintpair, b(b0));
                    } else if (j1 > bytebuffer.remaining()) {
                        RegionFile.LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", chunkcoordintpair, j1, bytebuffer.remaining());
                        return null;
                    } else if (j1 < 0) {
                        RegionFile.LOGGER.error("Declared size {} of chunk {} is negative", i1, chunkcoordintpair);
                        return null;
                    } else {
                        return this.a(chunkcoordintpair, b0, a(bytebuffer, j1));
                    }
                }
            }
        }
    }

    private static int b() {
        return (int) (SystemUtils.getTimeMillis() / 1000L);
    }

    private static boolean a(byte b0) {
        return (b0 & 128) != 0;
    }

    private static byte b(byte b0) {
        return (byte) (b0 & -129);
    }

    @Nullable
    private DataInputStream a(ChunkCoordIntPair chunkcoordintpair, byte b0, InputStream inputstream) throws IOException {
        RegionFileCompression regionfilecompression = RegionFileCompression.a(b0);

        if (regionfilecompression == null) {
            RegionFile.LOGGER.error("Chunk {} has invalid chunk stream version {}", chunkcoordintpair, b0);
            return null;
        } else {
            return new DataInputStream(new BufferedInputStream(regionfilecompression.a(inputstream)));
        }
    }

    @Nullable
    private DataInputStream a(ChunkCoordIntPair chunkcoordintpair, byte b0) throws IOException {
        Path path = this.f(chunkcoordintpair);

        if (!Files.isRegularFile(path, new LinkOption[0])) {
            RegionFile.LOGGER.error("External chunk path {} is not file", path);
            return null;
        } else {
            return this.a(chunkcoordintpair, b0, Files.newInputStream(path));
        }
    }

    private static ByteArrayInputStream a(ByteBuffer bytebuffer, int i) {
        return new ByteArrayInputStream(bytebuffer.array(), bytebuffer.position(), i);
    }

    private int a(int i, int j) {
        return i << 8 | j;
    }

    private static int a(int i) {
        return i & 255;
    }

    private static int b(int i) {
        return i >> 8 & 16777215;
    }

    private static int c(int i) {
        return (i + 4096 - 1) / 4096;
    }

    public boolean b(ChunkCoordIntPair chunkcoordintpair) {
        int i = this.getOffset(chunkcoordintpair);

        if (i == 0) {
            return false;
        } else {
            int j = b(i);
            int k = a(i);
            ByteBuffer bytebuffer = ByteBuffer.allocate(5);

            try {
                this.file.read(bytebuffer, (long) (j * 4096));
                bytebuffer.flip();
                if (bytebuffer.remaining() != 5) {
                    return false;
                } else {
                    int l = bytebuffer.getInt();
                    byte b0 = bytebuffer.get();

                    if (a(b0)) {
                        if (!RegionFileCompression.b(b(b0))) {
                            return false;
                        }

                        if (!Files.isRegularFile(this.f(chunkcoordintpair), new LinkOption[0])) {
                            return false;
                        }
                    } else {
                        if (!RegionFileCompression.b(b0)) {
                            return false;
                        }

                        if (l == 0) {
                            return false;
                        }

                        int i1 = l - 1;

                        if (i1 < 0 || i1 > 4096 * k) {
                            return false;
                        }
                    }

                    return true;
                }
            } catch (IOException ioexception) {
                return false;
            }
        }
    }

    public DataOutputStream c(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        return new DataOutputStream(new BufferedOutputStream(this.version.a((OutputStream) (new RegionFile.ChunkBuffer(chunkcoordintpair)))));
    }

    public void a() throws IOException {
        this.file.force(true);
    }

    public void d(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        int i = h(chunkcoordintpair);
        int j = this.offsets.get(i);

        if (j != 0) {
            this.offsets.put(i, 0);
            this.timestamps.put(i, b());
            this.d();
            Files.deleteIfExists(this.f(chunkcoordintpair));
            this.usedSectors.b(b(j), a(j));
        }
    }

    protected synchronized void a(ChunkCoordIntPair chunkcoordintpair, ByteBuffer bytebuffer) throws IOException {
        int i = h(chunkcoordintpair);
        int j = this.offsets.get(i);
        int k = b(j);
        int l = a(j);
        int i1 = bytebuffer.remaining();
        int j1 = c(i1);
        int k1;
        RegionFile.b regionfile_b;

        if (j1 >= 256) {
            Path path = this.f(chunkcoordintpair);

            RegionFile.LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", chunkcoordintpair, i1, path);
            j1 = 1;
            k1 = this.usedSectors.a(j1);
            regionfile_b = this.a(path, bytebuffer);
            ByteBuffer bytebuffer1 = this.c();

            this.file.write(bytebuffer1, (long) (k1 * 4096));
        } else {
            k1 = this.usedSectors.a(j1);
            regionfile_b = () -> {
                Files.deleteIfExists(this.f(chunkcoordintpair));
            };
            this.file.write(bytebuffer, (long) (k1 * 4096));
        }

        this.offsets.put(i, this.a(k1, j1));
        this.timestamps.put(i, b());
        this.d();
        regionfile_b.run();
        if (k != 0) {
            this.usedSectors.b(k, l);
        }

    }

    private ByteBuffer c() {
        ByteBuffer bytebuffer = ByteBuffer.allocate(5);

        bytebuffer.putInt(1);
        bytebuffer.put((byte) (this.version.a() | 128));
        bytebuffer.flip();
        return bytebuffer;
    }

    private RegionFile.b a(Path path, ByteBuffer bytebuffer) throws IOException {
        Path path1 = Files.createTempFile(this.externalFileDir, "tmp", (String) null);
        FileChannel filechannel = FileChannel.open(path1, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        try {
            bytebuffer.position(5);
            filechannel.write(bytebuffer);
        } catch (Throwable throwable) {
            if (filechannel != null) {
                try {
                    filechannel.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            }

            throw throwable;
        }

        if (filechannel != null) {
            filechannel.close();
        }

        return () -> {
            Files.move(path1, path, StandardCopyOption.REPLACE_EXISTING);
        };
    }

    private void d() throws IOException {
        this.header.position(0);
        this.file.write(this.header, 0L);
    }

    private int getOffset(ChunkCoordIntPair chunkcoordintpair) {
        return this.offsets.get(h(chunkcoordintpair));
    }

    public boolean chunkExists(ChunkCoordIntPair chunkcoordintpair) {
        return this.getOffset(chunkcoordintpair) != 0;
    }

    private static int h(ChunkCoordIntPair chunkcoordintpair) {
        return chunkcoordintpair.j() + chunkcoordintpair.k() * 32;
    }

    public void close() throws IOException {
        try {
            this.e();
        } finally {
            try {
                this.file.force(true);
            } finally {
                this.file.close();
            }
        }

    }

    private void e() throws IOException {
        int i = (int) this.file.size();
        int j = c(i) * 4096;

        if (i != j) {
            ByteBuffer bytebuffer = RegionFile.PADDING_BUFFER.duplicate();

            bytebuffer.position(0);
            this.file.write(bytebuffer, (long) (j - 1));
        }

    }

    private class ChunkBuffer extends ByteArrayOutputStream {

        private final ChunkCoordIntPair pos;

        public ChunkBuffer(ChunkCoordIntPair chunkcoordintpair) {
            super(8096);
            super.write(0);
            super.write(0);
            super.write(0);
            super.write(0);
            super.write(RegionFile.this.version.a());
            this.pos = chunkcoordintpair;
        }

        public void close() throws IOException {
            ByteBuffer bytebuffer = ByteBuffer.wrap(this.buf, 0, this.count);

            bytebuffer.putInt(0, this.count - 5 + 1);
            RegionFile.this.a(this.pos, bytebuffer);
        }
    }

    private interface b {

        void run() throws IOException;
    }
}
