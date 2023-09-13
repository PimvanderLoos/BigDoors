package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;

public interface Tag<T> {

    static <T> Codec<Tag<T>> a(Supplier<Tags<T>> supplier) {
        return MinecraftKey.a.flatXmap((minecraftkey) -> {
            return (DataResult) Optional.ofNullable(((Tags) supplier.get()).a(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown tag: " + minecraftkey);
            });
        }, (tag) -> {
            return (DataResult) Optional.ofNullable(((Tags) supplier.get()).a(tag)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown tag: " + tag);
            });
        });
    }

    boolean isTagged(T t0);

    List<T> getTagged();

    default T a(Random random) {
        List<T> list = this.getTagged();

        return list.get(random.nextInt(list.size()));
    }

    static <T> Tag<T> b(Set<T> set) {
        return TagSet.a(set);
    }

    public interface e<T> extends Tag<T> {

        MinecraftKey a();
    }

    public static class g implements Tag.d {

        private final MinecraftKey a;

        public g(MinecraftKey minecraftkey) {
            this.a = minecraftkey;
        }

        @Override
        public <T> boolean a(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer) {
            Tag<T> tag = (Tag) function.apply(this.a);

            if (tag != null) {
                tag.getTagged().forEach(consumer);
            }

            return true;
        }

        @Override
        public void a(JsonArray jsonarray) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("id", "#" + this.a);
            jsonobject.addProperty("required", false);
            jsonarray.add(jsonobject);
        }

        public String toString() {
            return "#" + this.a + "?";
        }
    }

    public static class h implements Tag.d {

        private final MinecraftKey a;

        public h(MinecraftKey minecraftkey) {
            this.a = minecraftkey;
        }

        @Override
        public <T> boolean a(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer) {
            Tag<T> tag = (Tag) function.apply(this.a);

            if (tag == null) {
                return false;
            } else {
                tag.getTagged().forEach(consumer);
                return true;
            }
        }

        @Override
        public void a(JsonArray jsonarray) {
            jsonarray.add("#" + this.a);
        }

        public String toString() {
            return "#" + this.a;
        }
    }

    public static class f implements Tag.d {

        private final MinecraftKey a;

        public f(MinecraftKey minecraftkey) {
            this.a = minecraftkey;
        }

        @Override
        public <T> boolean a(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer) {
            T t0 = function1.apply(this.a);

            if (t0 != null) {
                consumer.accept(t0);
            }

            return true;
        }

        @Override
        public void a(JsonArray jsonarray) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("id", this.a.toString());
            jsonobject.addProperty("required", false);
            jsonarray.add(jsonobject);
        }

        public String toString() {
            return this.a.toString() + "?";
        }
    }

    public static class c implements Tag.d {

        private final MinecraftKey a;

        public c(MinecraftKey minecraftkey) {
            this.a = minecraftkey;
        }

        @Override
        public <T> boolean a(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer) {
            T t0 = function1.apply(this.a);

            if (t0 == null) {
                return false;
            } else {
                consumer.accept(t0);
                return true;
            }
        }

        @Override
        public void a(JsonArray jsonarray) {
            jsonarray.add(this.a.toString());
        }

        public String toString() {
            return this.a.toString();
        }
    }

    public interface d {

        <T> boolean a(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer);

        void a(JsonArray jsonarray);
    }

    public static class a {

        private final List<Tag.b> a = Lists.newArrayList();

        public a() {}

        public static Tag.a a() {
            return new Tag.a();
        }

        public Tag.a a(Tag.b tag_b) {
            this.a.add(tag_b);
            return this;
        }

        public Tag.a a(Tag.d tag_d, String s) {
            return this.a(new Tag.b(tag_d, s));
        }

        public Tag.a a(MinecraftKey minecraftkey, String s) {
            return this.a((Tag.d) (new Tag.c(minecraftkey)), s);
        }

        public Tag.a c(MinecraftKey minecraftkey, String s) {
            return this.a((Tag.d) (new Tag.h(minecraftkey)), s);
        }

        public <T> Optional<Tag<T>> a(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1) {
            Builder<T> builder = ImmutableSet.builder();
            Iterator iterator = this.a.iterator();

            Tag.d tag_d;

            do {
                if (!iterator.hasNext()) {
                    return Optional.of(Tag.b(builder.build()));
                }

                Tag.b tag_b = (Tag.b) iterator.next();

                tag_d = tag_b.a();
                builder.getClass();
            } while (tag_d.a(function, function1, builder::add));

            return Optional.empty();
        }

        public Stream<Tag.b> b() {
            return this.a.stream();
        }

        public <T> Stream<Tag.b> b(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1) {
            return this.b().filter((tag_b) -> {
                return !tag_b.a().a(function, function1, (object) -> {
                });
            });
        }

        public Tag.a a(JsonObject jsonobject, String s) {
            JsonArray jsonarray = ChatDeserializer.u(jsonobject, "values");
            List<Tag.d> list = Lists.newArrayList();
            Iterator iterator = jsonarray.iterator();

            while (iterator.hasNext()) {
                JsonElement jsonelement = (JsonElement) iterator.next();

                list.add(a(jsonelement));
            }

            if (ChatDeserializer.a(jsonobject, "replace", false)) {
                this.a.clear();
            }

            list.forEach((tag_d) -> {
                this.a.add(new Tag.b(tag_d, s));
            });
            return this;
        }

        private static Tag.d a(JsonElement jsonelement) {
            String s;
            boolean flag;

            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject = jsonelement.getAsJsonObject();

                s = ChatDeserializer.h(jsonobject, "id");
                flag = ChatDeserializer.a(jsonobject, "required", true);
            } else {
                s = ChatDeserializer.a(jsonelement, "id");
                flag = true;
            }

            MinecraftKey minecraftkey;

            if (s.startsWith("#")) {
                minecraftkey = new MinecraftKey(s.substring(1));
                return (Tag.d) (flag ? new Tag.h(minecraftkey) : new Tag.g(minecraftkey));
            } else {
                minecraftkey = new MinecraftKey(s);
                return (Tag.d) (flag ? new Tag.c(minecraftkey) : new Tag.f(minecraftkey));
            }
        }

        public JsonObject c() {
            JsonObject jsonobject = new JsonObject();
            JsonArray jsonarray = new JsonArray();
            Iterator iterator = this.a.iterator();

            while (iterator.hasNext()) {
                Tag.b tag_b = (Tag.b) iterator.next();

                tag_b.a().a(jsonarray);
            }

            jsonobject.addProperty("replace", false);
            jsonobject.add("values", jsonarray);
            return jsonobject;
        }
    }

    public static class b {

        private final Tag.d a;
        private final String b;

        private b(Tag.d tag_d, String s) {
            this.a = tag_d;
            this.b = s;
        }

        public Tag.d a() {
            return this.a;
        }

        public String toString() {
            return this.a.toString() + " (from " + this.b + ")";
        }
    }
}
