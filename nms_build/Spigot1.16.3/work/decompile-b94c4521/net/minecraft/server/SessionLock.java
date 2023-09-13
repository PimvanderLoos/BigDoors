package net.minecraft.server;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.StandardOpenOption;

public class SessionLock implements AutoCloseable {

    private final FileChannel a;
    private final FileLock b;
    private static final ByteBuffer c;

    public static SessionLock a(java.nio.file.Path java_nio_file_path) throws IOException {
        java.nio.file.Path java_nio_file_path1 = java_nio_file_path.resolve("session.lock");

        if (!Files.isDirectory(java_nio_file_path, new LinkOption[0])) {
            Files.createDirectories(java_nio_file_path);
        }

        FileChannel filechannel = FileChannel.open(java_nio_file_path1, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        try {
            filechannel.write(SessionLock.c.duplicate());
            filechannel.force(true);
            FileLock filelock = filechannel.tryLock();

            if (filelock == null) {
                throw SessionLock.ExceptionWorldConflict.a(java_nio_file_path1);
            } else {
                return new SessionLock(filechannel, filelock);
            }
        } catch (IOException ioexception) {
            try {
                filechannel.close();
            } catch (IOException ioexception1) {
                ioexception.addSuppressed(ioexception1);
            }

            throw ioexception;
        }
    }

    private SessionLock(FileChannel filechannel, FileLock filelock) {
        this.a = filechannel;
        this.b = filelock;
    }

    public void close() throws IOException {
        try {
            if (this.b.isValid()) {
                this.b.release();
            }
        } finally {
            if (this.a.isOpen()) {
                this.a.close();
            }

        }

    }

    public boolean a() {
        return this.b.isValid();
    }

    static {
        byte[] abyte = "\u2603".getBytes(Charsets.UTF_8);

        c = ByteBuffer.allocateDirect(abyte.length);
        SessionLock.c.put(abyte);
        SessionLock.c.flip();
    }

    public static class ExceptionWorldConflict extends IOException {

        private ExceptionWorldConflict(java.nio.file.Path java_nio_file_path, String s) {
            super(java_nio_file_path.toAbsolutePath() + ": " + s);
        }

        public static SessionLock.ExceptionWorldConflict a(java.nio.file.Path java_nio_file_path) {
            return new SessionLock.ExceptionWorldConflict(java_nio_file_path, "already locked (possibly by other Minecraft instance?)");
        }
    }
}
