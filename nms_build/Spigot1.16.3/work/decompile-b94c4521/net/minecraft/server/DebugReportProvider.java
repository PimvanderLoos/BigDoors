package net.minecraft.server;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Objects;

public interface DebugReportProvider {

    HashFunction a = Hashing.sha1();

    void a(HashCache hashcache) throws IOException;

    String a();

    static void a(Gson gson, HashCache hashcache, JsonElement jsonelement, java.nio.file.Path java_nio_file_path) throws IOException {
        String s = gson.toJson(jsonelement);
        String s1 = DebugReportProvider.a.hashUnencodedChars(s).toString();

        if (!Objects.equals(hashcache.a(java_nio_file_path), s1) || !Files.exists(java_nio_file_path, new LinkOption[0])) {
            Files.createDirectories(java_nio_file_path.getParent());
            BufferedWriter bufferedwriter = Files.newBufferedWriter(java_nio_file_path);
            Throwable throwable = null;

            try {
                bufferedwriter.write(s);
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
        }

        hashcache.a(java_nio_file_path, s1);
    }
}
