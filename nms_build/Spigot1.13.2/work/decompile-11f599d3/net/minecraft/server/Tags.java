package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tags<T> {

    private static final Logger a = LogManager.getLogger();
    private static final Gson b = new Gson();
    private static final int c = ".json".length();
    private final Map<MinecraftKey, Tag<T>> d = Maps.newHashMap();
    private final Function<MinecraftKey, T> e;
    private final Predicate<MinecraftKey> f;
    private final String g;
    private final boolean h;
    private final String i;

    public Tags(Predicate<MinecraftKey> predicate, Function<MinecraftKey, T> function, String s, boolean flag, String s1) {
        this.f = predicate;
        this.e = function;
        this.g = s;
        this.h = flag;
        this.i = s1;
    }

    public void a(Tag<T> tag) {
        if (this.d.containsKey(tag.c())) {
            throw new IllegalArgumentException("Duplicate " + this.i + " tag '" + tag.c() + "'");
        } else {
            this.d.put(tag.c(), tag);
        }
    }

    @Nullable
    public Tag<T> a(MinecraftKey minecraftkey) {
        return (Tag) this.d.get(minecraftkey);
    }

    public Tag<T> b(MinecraftKey minecraftkey) {
        Tag<T> tag = (Tag) this.d.get(minecraftkey);

        return tag == null ? new Tag<>(minecraftkey) : tag;
    }

    public Collection<MinecraftKey> a() {
        return this.d.keySet();
    }

    public void b() {
        this.d.clear();
    }

    public void a(IResourceManager iresourcemanager) {
        Map<MinecraftKey, Tag.a<T>> map = Maps.newHashMap();
        Iterator iterator = iresourcemanager.a(this.g, (s) -> {
            return s.endsWith(".json");
        }).iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            String s = minecraftkey.getKey();
            MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.b(), s.substring(this.g.length() + 1, s.length() - Tags.c));

            try {
                Iterator iterator1 = iresourcemanager.b(minecraftkey).iterator();

                while (iterator1.hasNext()) {
                    IResource iresource = (IResource) iterator1.next();

                    try {
                        JsonObject jsonobject = (JsonObject) ChatDeserializer.a(Tags.b, IOUtils.toString(iresource.b(), StandardCharsets.UTF_8), JsonObject.class);

                        if (jsonobject == null) {
                            Tags.a.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.i, minecraftkey1, minecraftkey, iresource.d());
                        } else {
                            Tag.a<T> tag_a = (Tag.a) map.getOrDefault(minecraftkey1, Tag.a.a());

                            tag_a.a(this.f, this.e, jsonobject);
                            map.put(minecraftkey1, tag_a);
                        }
                    } catch (RuntimeException | IOException ioexception) {
                        Tags.a.error("Couldn't read {} tag list {} from {} in data pack {}", this.i, minecraftkey1, minecraftkey, iresource.d(), ioexception);
                    } finally {
                        IOUtils.closeQuietly(iresource);
                    }
                }
            } catch (IOException ioexception1) {
                Tags.a.error("Couldn't read {} tag list {} from {}", this.i, minecraftkey1, minecraftkey, ioexception1);
            }
        }

        label148:
        while (!map.isEmpty()) {
            boolean flag = false;
            Iterator iterator2 = map.entrySet().iterator();

            Entry entry;

            while (iterator2.hasNext()) {
                entry = (Entry) iterator2.next();
                if (((Tag.a) entry.getValue()).a(this::a)) {
                    flag = true;
                    this.a(((Tag.a) entry.getValue()).b((MinecraftKey) entry.getKey()));
                    iterator2.remove();
                }
            }

            if (!flag) {
                iterator2 = map.entrySet().iterator();

                while (true) {
                    if (!iterator2.hasNext()) {
                        break label148;
                    }

                    entry = (Entry) iterator2.next();
                    Tags.a.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.i, entry.getKey());
                }
            }
        }

        iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MinecraftKey, Tag.a<T>> entry1 = (Entry) iterator.next();

            this.a(((Tag.a) entry1.getValue()).a(this.h).b((MinecraftKey) entry1.getKey()));
        }

    }

    public Map<MinecraftKey, Tag<T>> c() {
        return this.d;
    }
}
