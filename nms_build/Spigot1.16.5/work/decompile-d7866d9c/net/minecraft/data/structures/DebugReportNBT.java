package net.minecraft.data.structures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.data.DebugReportGenerator;
import net.minecraft.data.DebugReportProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportNBT implements DebugReportProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private final DebugReportGenerator c;

    public DebugReportNBT(DebugReportGenerator debugreportgenerator) {
        this.c = debugreportgenerator;
    }

    @Override
    public void a(HashCache hashcache) throws IOException {
        Path path = this.c.b();
        Iterator iterator = this.c.a().iterator();

        while (iterator.hasNext()) {
            Path path1 = (Path) iterator.next();

            Files.walk(path1).filter((path2) -> {
                return path2.toString().endsWith(".nbt");
            }).forEach((path2) -> {
                a(path2, this.a(path1, path2), path);
            });
        }

    }

    @Override
    public String a() {
        return "NBT to SNBT";
    }

    private String a(Path path, Path path1) {
        String s = path.relativize(path1).toString().replaceAll("\\\\", "/");

        return s.substring(0, s.length() - ".nbt".length());
    }

    @Nullable
    public static Path a(Path path, String s, Path path1) {
        try {
            NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(Files.newInputStream(path));
            IChatBaseComponent ichatbasecomponent = nbttagcompound.a("    ", 0);
            String s1 = ichatbasecomponent.getString() + "\n";
            Path path2 = path1.resolve(s + ".snbt");

            Files.createDirectories(path2.getParent());
            BufferedWriter bufferedwriter = Files.newBufferedWriter(path2);
            Throwable throwable = null;

            try {
                bufferedwriter.write(s1);
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (bufferedwriter != null) {
                    if (throwable != null) {
                        try {
                            bufferedwriter.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        bufferedwriter.close();
                    }
                }

            }

            DebugReportNBT.LOGGER.info("Converted {} from NBT to SNBT", s);
            return path2;
        } catch (IOException ioexception) {
            DebugReportNBT.LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", s, path, ioexception);
            return null;
        }
    }
}
