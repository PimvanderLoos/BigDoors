package net.minecraft.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportNBT implements DebugReportProvider {

    private static final Logger b = LogManager.getLogger();
    private final DebugReportGenerator c;

    public DebugReportNBT(DebugReportGenerator debugreportgenerator) {
        this.c = debugreportgenerator;
    }

    public void a(HashCache hashcache) throws IOException {
        java.nio.file.Path java_nio_file_path = this.c.b();
        Iterator iterator = this.c.a().iterator();

        while (iterator.hasNext()) {
            java.nio.file.Path java_nio_file_path1 = (java.nio.file.Path) iterator.next();

            Files.walk(java_nio_file_path1).filter((java_nio_file_path2) -> {
                return java_nio_file_path2.toString().endsWith(".nbt");
            }).forEach((java_nio_file_path2) -> {
                this.a(java_nio_file_path2, this.a(java_nio_file_path1, java_nio_file_path2), java_nio_file_path);
            });
        }

    }

    public String a() {
        return "NBT to SNBT";
    }

    private String a(java.nio.file.Path java_nio_file_path, java.nio.file.Path java_nio_file_path1) {
        String s = java_nio_file_path.relativize(java_nio_file_path1).toString().replaceAll("\\\\", "/");

        return s.substring(0, s.length() - ".nbt".length());
    }

    private void a(java.nio.file.Path java_nio_file_path, String s, java.nio.file.Path java_nio_file_path1) {
        try {
            NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(Files.newInputStream(java_nio_file_path));
            IChatBaseComponent ichatbasecomponent = nbttagcompound.a("    ", 0);
            String s1 = ichatbasecomponent.getString();
            java.nio.file.Path java_nio_file_path2 = java_nio_file_path1.resolve(s + ".snbt");

            Files.createDirectories(java_nio_file_path2.getParent());
            BufferedWriter bufferedwriter = Files.newBufferedWriter(java_nio_file_path2);
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

            DebugReportNBT.b.info("Converted {} from NBT to SNBT", s);
        } catch (IOException ioexception) {
            DebugReportNBT.b.error("Couldn't convert {} from NBT to SNBT at {}", s, java_nio_file_path, ioexception);
        }

    }
}
