package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.ToIntFunction;
import net.minecraft.SystemUtils;
import net.minecraft.util.ChatDeserializer;

public interface DebugReportProvider {

    ToIntFunction<String> FIXED_ORDER_FIELDS = (ToIntFunction) SystemUtils.make(new Object2IntOpenHashMap(), (object2intopenhashmap) -> {
        object2intopenhashmap.put("type", 0);
        object2intopenhashmap.put("parent", 1);
        object2intopenhashmap.defaultReturnValue(2);
    });
    Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(DebugReportProvider.FIXED_ORDER_FIELDS).thenComparing((s) -> {
        return s;
    });

    void run(CachedOutput cachedoutput) throws IOException;

    String getName();

    static void saveStable(CachedOutput cachedoutput, JsonElement jsonelement, Path path) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);
        OutputStreamWriter outputstreamwriter = new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8);
        JsonWriter jsonwriter = new JsonWriter(outputstreamwriter);

        jsonwriter.setSerializeNulls(false);
        jsonwriter.setIndent("  ");
        ChatDeserializer.writeValue(jsonwriter, jsonelement, DebugReportProvider.KEY_COMPARATOR);
        jsonwriter.close();
        cachedoutput.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
    }
}
