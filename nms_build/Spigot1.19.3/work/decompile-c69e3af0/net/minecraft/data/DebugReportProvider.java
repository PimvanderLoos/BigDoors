package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import net.minecraft.SystemUtils;
import net.minecraft.util.ChatDeserializer;
import org.slf4j.Logger;

public interface DebugReportProvider {

    ToIntFunction<String> FIXED_ORDER_FIELDS = (ToIntFunction) SystemUtils.make(new Object2IntOpenHashMap(), (object2intopenhashmap) -> {
        object2intopenhashmap.put("type", 0);
        object2intopenhashmap.put("parent", 1);
        object2intopenhashmap.defaultReturnValue(2);
    });
    Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(DebugReportProvider.FIXED_ORDER_FIELDS).thenComparing((s) -> {
        return s;
    });
    Logger LOGGER = LogUtils.getLogger();

    CompletableFuture<?> run(CachedOutput cachedoutput);

    String getName();

    static CompletableFuture<?> saveStable(CachedOutput cachedoutput, JsonElement jsonelement, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);
                JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8));

                try {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent("  ");
                    ChatDeserializer.writeValue(jsonwriter, jsonelement, DebugReportProvider.KEY_COMPARATOR);
                } catch (Throwable throwable) {
                    try {
                        jsonwriter.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }

                    throw throwable;
                }

                jsonwriter.close();
                cachedoutput.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {
                DebugReportProvider.LOGGER.error("Failed to save file to {}", path, ioexception);
            }

        }, SystemUtils.backgroundExecutor());
    }

    @FunctionalInterface
    public interface a<T extends DebugReportProvider> {

        T create(PackOutput packoutput);
    }
}
