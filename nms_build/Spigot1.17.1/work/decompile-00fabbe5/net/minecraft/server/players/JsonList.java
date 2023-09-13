package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.util.ChatDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JsonList<K, V extends JsonListEntry<K>> {

    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final File file;
    private final Map<String, V> map = Maps.newHashMap();

    public JsonList(File file) {
        this.file = file;
    }

    public File b() {
        return this.file;
    }

    public void add(V v0) {
        this.map.put(this.a(v0.getKey()), v0);

        try {
            this.save();
        } catch (IOException ioexception) {
            JsonList.LOGGER.warn("Could not save the list after adding a user.", ioexception);
        }

    }

    @Nullable
    public V get(K k0) {
        this.g();
        return (JsonListEntry) this.map.get(this.a(k0));
    }

    public void remove(K k0) {
        this.map.remove(this.a(k0));

        try {
            this.save();
        } catch (IOException ioexception) {
            JsonList.LOGGER.warn("Could not save the list after removing a user.", ioexception);
        }

    }

    public void b(JsonListEntry<K> jsonlistentry) {
        this.remove(jsonlistentry.getKey());
    }

    public String[] getEntries() {
        return (String[]) this.map.keySet().toArray(new String[0]);
    }

    public boolean isEmpty() {
        return this.map.size() < 1;
    }

    protected String a(K k0) {
        return k0.toString();
    }

    protected boolean d(K k0) {
        return this.map.containsKey(this.a(k0));
    }

    private void g() {
        List<K> list = Lists.newArrayList();
        Iterator iterator = this.map.values().iterator();

        while (iterator.hasNext()) {
            V v0 = (JsonListEntry) iterator.next();

            if (v0.hasExpired()) {
                list.add(v0.getKey());
            }
        }

        iterator = list.iterator();

        while (iterator.hasNext()) {
            K k0 = iterator.next();

            this.map.remove(this.a(k0));
        }

    }

    protected abstract JsonListEntry<K> a(JsonObject jsonobject);

    public Collection<V> d() {
        return this.map.values();
    }

    public void save() throws IOException {
        JsonArray jsonarray = new JsonArray();
        Stream stream = this.map.values().stream().map((jsonlistentry) -> {
            JsonObject jsonobject = new JsonObject();

            Objects.requireNonNull(jsonlistentry);
            return (JsonObject) SystemUtils.a((Object) jsonobject, jsonlistentry::a);
        });

        Objects.requireNonNull(jsonarray);
        stream.forEach(jsonarray::add);
        BufferedWriter bufferedwriter = Files.newWriter(this.file, StandardCharsets.UTF_8);

        try {
            JsonList.GSON.toJson(jsonarray, bufferedwriter);
        } catch (Throwable throwable) {
            if (bufferedwriter != null) {
                try {
                    bufferedwriter.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            }

            throw throwable;
        }

        if (bufferedwriter != null) {
            bufferedwriter.close();
        }

    }

    public void load() throws IOException {
        if (this.file.exists()) {
            BufferedReader bufferedreader = Files.newReader(this.file, StandardCharsets.UTF_8);

            try {
                JsonArray jsonarray = (JsonArray) JsonList.GSON.fromJson(bufferedreader, JsonArray.class);

                this.map.clear();
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement = (JsonElement) iterator.next();
                    JsonObject jsonobject = ChatDeserializer.m(jsonelement, "entry");
                    JsonListEntry<K> jsonlistentry = this.a(jsonobject);

                    if (jsonlistentry.getKey() != null) {
                        this.map.put(this.a(jsonlistentry.getKey()), jsonlistentry);
                    }
                }
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

        }
    }
}
