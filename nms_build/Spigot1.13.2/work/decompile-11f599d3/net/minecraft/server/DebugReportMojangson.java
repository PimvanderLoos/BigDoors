package net.minecraft.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Iterator;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportMojangson implements DebugReportProvider {

    private static final Logger b = LogManager.getLogger();
    private final DebugReportGenerator c;

    public DebugReportMojangson(DebugReportGenerator debugreportgenerator) {
        this.c = debugreportgenerator;
    }

    public void a(HashCache hashcache) throws IOException {
        java.nio.file.Path java_nio_file_path = this.c.b();
        Iterator iterator = this.c.a().iterator();

        while (iterator.hasNext()) {
            java.nio.file.Path java_nio_file_path1 = (java.nio.file.Path) iterator.next();

            Files.walk(java_nio_file_path1).filter((java_nio_file_path2) -> {
                return java_nio_file_path2.toString().endsWith(".snbt");
            }).forEach((java_nio_file_path2) -> {
                this.a(hashcache, java_nio_file_path2, this.a(java_nio_file_path1, java_nio_file_path2), java_nio_file_path);
            });
        }

    }

    public String a() {
        return "SNBT -> NBT";
    }

    private String a(java.nio.file.Path java_nio_file_path, java.nio.file.Path java_nio_file_path1) {
        String s = java_nio_file_path.relativize(java_nio_file_path1).toString().replaceAll("\\\\", "/");

        return s.substring(0, s.length() - ".snbt".length());
    }

    private void a(HashCache hashcache, java.nio.file.Path java_nio_file_path, String s, java.nio.file.Path java_nio_file_path1) {
        try {
            java.nio.file.Path java_nio_file_path2 = java_nio_file_path1.resolve(s + ".nbt");
            BufferedReader bufferedreader = Files.newBufferedReader(java_nio_file_path);
            Throwable throwable = null;

            try {
                String s1 = IOUtils.toString(bufferedreader);
                String s2 = DebugReportMojangson.a.hashUnencodedChars(s1).toString();

                if (!Objects.equals(hashcache.a(java_nio_file_path2), s2) || !Files.exists(java_nio_file_path2, new LinkOption[0])) {
                    Files.createDirectories(java_nio_file_path2.getParent());
                    OutputStream outputstream = Files.newOutputStream(java_nio_file_path2);
                    Throwable throwable1 = null;

                    try {
                        NBTCompressedStreamTools.a(MojangsonParser.parse(s1), outputstream);
                    } catch (Throwable throwable2) {
                        throwable1 = throwable2;
                        throw throwable2;
                    } finally {
                        if (outputstream != null) {
                            if (throwable1 != null) {
                                try {
                                    outputstream.close();
                                } catch (Throwable throwable3) {
                                    throwable1.addSuppressed(throwable3);
                                }
                            } else {
                                outputstream.close();
                            }
                        }

                    }
                }

                hashcache.a(java_nio_file_path2, s2);
            } catch (Throwable throwable4) {
                throwable = throwable4;
                throw throwable4;
            } finally {
                if (bufferedreader != null) {
                    if (throwable != null) {
                        try {
                            bufferedreader.close();
                        } catch (Throwable throwable5) {
                            throwable.addSuppressed(throwable5);
                        }
                    } else {
                        bufferedreader.close();
                    }
                }

            }
        } catch (CommandSyntaxException commandsyntaxexception) {
            DebugReportMojangson.b.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", s, java_nio_file_path, commandsyntaxexception);
        } catch (IOException ioexception) {
            DebugReportMojangson.b.error("Couldn't convert {} from SNBT to NBT at {}", s, java_nio_file_path, ioexception);
        }

    }
}
