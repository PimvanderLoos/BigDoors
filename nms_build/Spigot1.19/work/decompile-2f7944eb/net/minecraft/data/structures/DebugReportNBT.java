package net.minecraft.data.structures;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DebugReportGenerator;
import net.minecraft.data.DebugReportProvider;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTCompressedStreamTools;
import org.slf4j.Logger;

public class DebugReportNBT implements DebugReportProvider {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final DebugReportGenerator generator;

    public DebugReportNBT(DebugReportGenerator debugreportgenerator) {
        this.generator = debugreportgenerator;
    }

    @Override
    public void run(CachedOutput cachedoutput) throws IOException {
        Path path = this.generator.getOutputFolder();
        Iterator iterator = this.generator.getInputFolders().iterator();

        while (iterator.hasNext()) {
            Path path1 = (Path) iterator.next();

            Files.walk(path1).filter((path2) -> {
                return path2.toString().endsWith(".nbt");
            }).forEach((path2) -> {
                convertStructure(cachedoutput, path2, this.getName(path1, path2), path);
            });
        }

    }

    @Override
    public String getName() {
        return "NBT to SNBT";
    }

    private String getName(Path path, Path path1) {
        String s = path.relativize(path1).toString().replaceAll("\\\\", "/");

        return s.substring(0, s.length() - ".nbt".length());
    }

    @Nullable
    public static Path convertStructure(CachedOutput cachedoutput, Path path, String s, Path path1) {
        try {
            InputStream inputstream = Files.newInputStream(path);

            Path path2;

            try {
                Path path3 = path1.resolve(s + ".snbt");

                writeSnbt(cachedoutput, path3, GameProfileSerializer.structureToSnbt(NBTCompressedStreamTools.readCompressed(inputstream)));
                DebugReportNBT.LOGGER.info("Converted {} from NBT to SNBT", s);
                path2 = path3;
            } catch (Throwable throwable) {
                if (inputstream != null) {
                    try {
                        inputstream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (inputstream != null) {
                inputstream.close();
            }

            return path2;
        } catch (IOException ioexception) {
            DebugReportNBT.LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", new Object[]{s, path, ioexception});
            return null;
        }
    }

    public static void writeSnbt(CachedOutput cachedoutput, Path path, String s) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

        hashingoutputstream.write(s.getBytes(StandardCharsets.UTF_8));
        hashingoutputstream.write(10);
        cachedoutput.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
    }
}
