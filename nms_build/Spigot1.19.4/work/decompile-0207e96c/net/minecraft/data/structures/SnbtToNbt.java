package net.minecraft.data.structures;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DebugReportProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class SnbtToNbt implements DebugReportProvider {

    @Nullable
    private static final Path DUMP_SNBT_TO = null;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackOutput output;
    private final Iterable<Path> inputFolders;
    private final List<SnbtToNbt.a> filters = Lists.newArrayList();

    public SnbtToNbt(PackOutput packoutput, Iterable<Path> iterable) {
        this.output = packoutput;
        this.inputFolders = iterable;
    }

    public SnbtToNbt addFilter(SnbtToNbt.a snbttonbt_a) {
        this.filters.add(snbttonbt_a);
        return this;
    }

    private NBTTagCompound applyFilters(String s, NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound;

        SnbtToNbt.a snbttonbt_a;

        for (Iterator iterator = this.filters.iterator(); iterator.hasNext(); nbttagcompound1 = snbttonbt_a.apply(s, nbttagcompound1)) {
            snbttonbt_a = (SnbtToNbt.a) iterator.next();
        }

        return nbttagcompound1;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedoutput) {
        Path path = this.output.getOutputFolder();
        List<CompletableFuture<?>> list = Lists.newArrayList();
        Iterator iterator = this.inputFolders.iterator();

        while (iterator.hasNext()) {
            Path path1 = (Path) iterator.next();

            list.add(CompletableFuture.supplyAsync(() -> {
                try {
                    Stream stream = Files.walk(path1);

                    CompletableFuture completablefuture;

                    try {
                        completablefuture = CompletableFuture.allOf((CompletableFuture[]) stream.filter((path2) -> {
                            return path2.toString().endsWith(".snbt");
                        }).map((path2) -> {
                            return CompletableFuture.runAsync(() -> {
                                SnbtToNbt.c snbttonbt_c = this.readStructure(path2, this.getName(path1, path2));

                                this.storeStructureIfChanged(cachedoutput, snbttonbt_c, path);
                            }, SystemUtils.backgroundExecutor());
                        }).toArray((i) -> {
                            return new CompletableFuture[i];
                        }));
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

                    return completablefuture;
                } catch (Exception exception) {
                    throw new RuntimeException("Failed to read structure input directory, aborting", exception);
                }
            }, SystemUtils.backgroundExecutor()).thenCompose((completablefuture) -> {
                return completablefuture;
            }));
        }

        return SystemUtils.sequenceFailFast(list);
    }

    @Override
    public final String getName() {
        return "SNBT -> NBT";
    }

    private String getName(Path path, Path path1) {
        String s = path.relativize(path1).toString().replaceAll("\\\\", "/");

        return s.substring(0, s.length() - ".snbt".length());
    }

    private SnbtToNbt.c readStructure(Path path, String s) {
        try {
            BufferedReader bufferedreader = Files.newBufferedReader(path);

            SnbtToNbt.c snbttonbt_c;

            try {
                String s1 = IOUtils.toString(bufferedreader);
                NBTTagCompound nbttagcompound = this.applyFilters(s, GameProfileSerializer.snbtToStructure(s1));
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                NBTCompressedStreamTools.writeCompressed(nbttagcompound, (OutputStream) hashingoutputstream);
                byte[] abyte = bytearrayoutputstream.toByteArray();
                HashCode hashcode = hashingoutputstream.hash();
                String s2;

                if (SnbtToNbt.DUMP_SNBT_TO != null) {
                    s2 = GameProfileSerializer.structureToSnbt(nbttagcompound);
                } else {
                    s2 = null;
                }

                snbttonbt_c = new SnbtToNbt.c(s, abyte, s2, hashcode);
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

    private void storeStructureIfChanged(CachedOutput cachedoutput, SnbtToNbt.c snbttonbt_c, Path path) {
        Path path1;

        if (snbttonbt_c.snbtPayload != null) {
            path1 = SnbtToNbt.DUMP_SNBT_TO.resolve(snbttonbt_c.name + ".snbt");

            try {
                DebugReportNBT.writeSnbt(CachedOutput.NO_CACHE, path1, snbttonbt_c.snbtPayload);
            } catch (IOException ioexception) {
                SnbtToNbt.LOGGER.error("Couldn't write structure SNBT {} at {}", new Object[]{snbttonbt_c.name, path1, ioexception});
            }
        }

        path1 = path.resolve(snbttonbt_c.name + ".nbt");

        try {
            cachedoutput.writeIfNeeded(path1, snbttonbt_c.payload, snbttonbt_c.hash);
        } catch (IOException ioexception1) {
            SnbtToNbt.LOGGER.error("Couldn't write structure {} at {}", new Object[]{snbttonbt_c.name, path1, ioexception1});
        }

    }

    @FunctionalInterface
    public interface a {

        NBTTagCompound apply(String s, NBTTagCompound nbttagcompound);
    }

    private static record c(String name, byte[] payload, @Nullable String snbtPayload, HashCode hash) {

    }

    private static class b extends RuntimeException {

        public b(Path path, Throwable throwable) {
            super(path.toAbsolutePath().toString(), throwable);
        }
    }
}
