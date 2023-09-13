package net.minecraft.data.structures;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.data.DebugReportGenerator;
import net.minecraft.data.DebugReportProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtToNbt implements DebugReportProvider {

    @Nullable
    private static final Path DUMP_SNBT_TO = null;
    private static final Logger LOGGER = LogManager.getLogger();
    private final DebugReportGenerator generator;
    private final List<SnbtToNbt.a> filters = Lists.newArrayList();

    public SnbtToNbt(DebugReportGenerator debugreportgenerator) {
        this.generator = debugreportgenerator;
    }

    public SnbtToNbt a(SnbtToNbt.a snbttonbt_a) {
        this.filters.add(snbttonbt_a);
        return this;
    }

    private NBTTagCompound a(String s, NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound;

        SnbtToNbt.a snbttonbt_a;

        for (Iterator iterator = this.filters.iterator(); iterator.hasNext(); nbttagcompound1 = snbttonbt_a.a(s, nbttagcompound1)) {
            snbttonbt_a = (SnbtToNbt.a) iterator.next();
        }

        return nbttagcompound1;
    }

    @Override
    public void a(HashCache hashcache) throws IOException {
        Path path = this.generator.b();
        List<CompletableFuture<SnbtToNbt.c>> list = Lists.newArrayList();
        Iterator iterator = this.generator.a().iterator();

        while (iterator.hasNext()) {
            Path path1 = (Path) iterator.next();

            Files.walk(path1).filter((path2) -> {
                return path2.toString().endsWith(".snbt");
            }).forEach((path2) -> {
                list.add(CompletableFuture.supplyAsync(() -> {
                    return this.a(path2, this.a(path1, path2));
                }, SystemUtils.f()));
            });
        }

        boolean flag = false;
        Iterator iterator1 = list.iterator();

        while (iterator1.hasNext()) {
            CompletableFuture completablefuture = (CompletableFuture) iterator1.next();

            try {
                this.a(hashcache, (SnbtToNbt.c) completablefuture.get(), path);
            } catch (Exception exception) {
                SnbtToNbt.LOGGER.error("Failed to process structure", exception);
                flag = true;
            }
        }

        if (flag) {
            throw new IllegalStateException("Failed to convert all structures, aborting");
        }
    }

    @Override
    public String a() {
        return "SNBT -> NBT";
    }

    private String a(Path path, Path path1) {
        String s = path.relativize(path1).toString().replaceAll("\\\\", "/");

        return s.substring(0, s.length() - ".snbt".length());
    }

    private SnbtToNbt.c a(Path path, String s) {
        try {
            BufferedReader bufferedreader = Files.newBufferedReader(path);

            SnbtToNbt.c snbttonbt_c;

            try {
                String s1 = IOUtils.toString(bufferedreader);
                NBTTagCompound nbttagcompound = this.a(s, GameProfileSerializer.a(s1));
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

                NBTCompressedStreamTools.a(nbttagcompound, (OutputStream) bytearrayoutputstream);
                byte[] abyte = bytearrayoutputstream.toByteArray();
                String s2 = SnbtToNbt.SHA1.hashBytes(abyte).toString();
                String s3;

                if (SnbtToNbt.DUMP_SNBT_TO != null) {
                    s3 = GameProfileSerializer.d(nbttagcompound);
                } else {
                    s3 = null;
                }

                snbttonbt_c = new SnbtToNbt.c(s, abyte, s3, s2);
            } catch (Throwable throwable) {
                if (bufferedreader != null) {
                    try {
                        bufferedreader.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (bufferedreader != null) {
                bufferedreader.close();
            }

            return snbttonbt_c;
        } catch (Throwable throwable2) {
            throw new SnbtToNbt.b(path, throwable2);
        }
    }

    private void a(HashCache hashcache, SnbtToNbt.c snbttonbt_c, Path path) {
        Path path1;

        if (snbttonbt_c.snbtPayload != null) {
            path1 = SnbtToNbt.DUMP_SNBT_TO.resolve(snbttonbt_c.name + ".snbt");

            try {
                DebugReportNBT.a(path1, snbttonbt_c.snbtPayload);
            } catch (IOException ioexception) {
                SnbtToNbt.LOGGER.error("Couldn't write structure SNBT {} at {}", snbttonbt_c.name, path1, ioexception);
            }
        }

        path1 = path.resolve(snbttonbt_c.name + ".nbt");

        try {
            if (!Objects.equals(hashcache.a(path1), snbttonbt_c.hash) || !Files.exists(path1, new LinkOption[0])) {
                Files.createDirectories(path1.getParent());
                OutputStream outputstream = Files.newOutputStream(path1);

                try {
                    outputstream.write(snbttonbt_c.payload);
                } catch (Throwable throwable) {
                    if (outputstream != null) {
                        try {
                            outputstream.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    }

                    throw throwable;
                }

                if (outputstream != null) {
                    outputstream.close();
                }
            }

            hashcache.a(path1, snbttonbt_c.hash);
        } catch (IOException ioexception1) {
            SnbtToNbt.LOGGER.error("Couldn't write structure {} at {}", snbttonbt_c.name, path1, ioexception1);
        }

    }

    @FunctionalInterface
    public interface a {

        NBTTagCompound a(String s, NBTTagCompound nbttagcompound);
    }

    private static class c {

        final String name;
        final byte[] payload;
        @Nullable
        final String snbtPayload;
        final String hash;

        public c(String s, byte[] abyte, @Nullable String s1, String s2) {
            this.name = s;
            this.payload = abyte;
            this.snbtPayload = s1;
            this.hash = s2;
        }
    }

    private static class b extends RuntimeException {

        public b(Path path, Throwable throwable) {
            super(path.toAbsolutePath().toString(), throwable);
        }
    }
}
