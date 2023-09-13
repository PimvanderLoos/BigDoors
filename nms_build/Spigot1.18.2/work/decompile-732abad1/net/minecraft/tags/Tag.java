package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;

public class Tag<T> {

    private static final Tag<?> EMPTY = new Tag<>(List.of());
    final List<T> elements;

    public Tag(Collection<T> collection) {
        this.elements = List.copyOf(collection);
    }

    public List<T> getValues() {
        return this.elements;
    }

    public static <T> Tag<T> empty() {
        return Tag.EMPTY;
    }

    private static class f implements Tag.d {

        private final MinecraftKey id;

        public f(MinecraftKey minecraftkey) {
            this.id = minecraftkey;
        }

        @Override
        public <T> boolean build(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer) {
            Tag<T> tag = (Tag) function.apply(this.id);

            if (tag != null) {
                tag.elements.forEach(consumer);
            }

            return true;
        }

        @Override
        public void serializeTo(JsonArray jsonarray) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("id", "#" + this.id);
            jsonobject.addProperty("required", false);
            jsonarray.add(jsonobject);
        }

        public String toString() {
            return "#" + this.id + "?";
        }

        @Override
        public void visitOptionalDependencies(Consumer<MinecraftKey> consumer) {
            consumer.accept(this.id);
        }

        @Override
        public boolean verifyIfPresent(Predicate<MinecraftKey> predicate, Predicate<MinecraftKey> predicate1) {
            return true;
        }
    }

    private static class g implements Tag.d {

        private final MinecraftKey id;

        public g(MinecraftKey minecraftkey) {
            this.id = minecraftkey;
        }

        @Override
        public <T> boolean build(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer) {
            Tag<T> tag = (Tag) function.apply(this.id);

            if (tag == null) {
                return false;
            } else {
                tag.elements.forEach(consumer);
                return true;
            }
        }

        @Override
        public void serializeTo(JsonArray jsonarray) {
            jsonarray.add("#" + this.id);
        }

        public String toString() {
            return "#" + this.id;
        }

        @Override
        public boolean verifyIfPresent(Predicate<MinecraftKey> predicate, Predicate<MinecraftKey> predicate1) {
            return predicate1.test(this.id);
        }

        @Override
        public void visitRequiredDependencies(Consumer<MinecraftKey> consumer) {
            consumer.accept(this.id);
        }
    }

    private static class e implements Tag.d {

        private final MinecraftKey id;

        public e(MinecraftKey minecraftkey) {
            this.id = minecraftkey;
        }

        @Override
        public <T> boolean build(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer) {
            T t0 = function1.apply(this.id);

            if (t0 != null) {
                consumer.accept(t0);
            }

            return true;
        }

        @Override
        public void serializeTo(JsonArray jsonarray) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("id", this.id.toString());
            jsonobject.addProperty("required", false);
            jsonarray.add(jsonobject);
        }

        @Override
        public boolean verifyIfPresent(Predicate<MinecraftKey> predicate, Predicate<MinecraftKey> predicate1) {
            return true;
        }

        public String toString() {
            return this.id + "?";
        }
    }

    private static class c implements Tag.d {

        private final MinecraftKey id;

        public c(MinecraftKey minecraftkey) {
            this.id = minecraftkey;
        }

        @Override
        public <T> boolean build(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer) {
            T t0 = function1.apply(this.id);

            if (t0 == null) {
                return false;
            } else {
                consumer.accept(t0);
                return true;
            }
        }

        @Override
        public void serializeTo(JsonArray jsonarray) {
            jsonarray.add(this.id.toString());
        }

        @Override
        public boolean verifyIfPresent(Predicate<MinecraftKey> predicate, Predicate<MinecraftKey> predicate1) {
            return predicate.test(this.id);
        }

        public String toString() {
            return this.id.toString();
        }
    }

    public interface d {

        <T> boolean build(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1, Consumer<T> consumer);

        void serializeTo(JsonArray jsonarray);

        default void visitRequiredDependencies(Consumer<MinecraftKey> consumer) {}

        default void visitOptionalDependencies(Consumer<MinecraftKey> consumer) {}

        boolean verifyIfPresent(Predicate<MinecraftKey> predicate, Predicate<MinecraftKey> predicate1);
    }

    public static class a {

        private final List<Tag.b> entries = new ArrayList();

        public a() {}

        public static Tag.a tag() {
            return new Tag.a();
        }

        public Tag.a add(Tag.b tag_b) {
            this.entries.add(tag_b);
            return this;
        }

        public Tag.a add(Tag.d tag_d, String s) {
            return this.add(new Tag.b(tag_d, s));
        }

        public Tag.a addElement(MinecraftKey minecraftkey, String s) {
            return this.add(new Tag.c(minecraftkey), s);
        }

        public Tag.a addOptionalElement(MinecraftKey minecraftkey, String s) {
            return this.add(new Tag.e(minecraftkey), s);
        }

        public Tag.a addTag(MinecraftKey minecraftkey, String s) {
            return this.add(new Tag.g(minecraftkey), s);
        }

        public Tag.a addOptionalTag(MinecraftKey minecraftkey, String s) {
            return this.add(new Tag.f(minecraftkey), s);
        }

        public <T> Either<Collection<Tag.b>, Tag<T>> build(Function<MinecraftKey, Tag<T>> function, Function<MinecraftKey, T> function1) {
            Builder<T> builder = ImmutableSet.builder();
            List<Tag.b> list = new ArrayList();
            Iterator iterator = this.entries.iterator();

            while (iterator.hasNext()) {
                Tag.b tag_b = (Tag.b) iterator.next();
                Tag.d tag_d = tag_b.entry();

                Objects.requireNonNull(builder);
                if (!tag_d.build(function, function1, builder::add)) {
                    list.add(tag_b);
                }
            }

            return list.isEmpty() ? Either.right(new Tag<>(builder.build())) : Either.left(list);
        }

        public Stream<Tag.b> getEntries() {
            return this.entries.stream();
        }

        public void visitRequiredDependencies(Consumer<MinecraftKey> consumer) {
            this.entries.forEach((tag_b) -> {
                tag_b.entry.visitRequiredDependencies(consumer);
            });
        }

        public void visitOptionalDependencies(Consumer<MinecraftKey> consumer) {
            this.entries.forEach((tag_b) -> {
                tag_b.entry.visitOptionalDependencies(consumer);
            });
        }

        public Tag.a addFromJson(JsonObject jsonobject, String s) {
            JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "values");
            List<Tag.d> list = new ArrayList();
            Iterator iterator = jsonarray.iterator();

            while (iterator.hasNext()) {
                JsonElement jsonelement = (JsonElement) iterator.next();

                list.add(parseEntry(jsonelement));
            }

            if (ChatDeserializer.getAsBoolean(jsonobject, "replace", false)) {
                this.entries.clear();
            }

            list.forEach((tag_d) -> {
                this.entries.add(new Tag.b(tag_d, s));
            });
            return this;
        }

        private static Tag.d parseEntry(JsonElement jsonelement) {
            String s;
            boolean flag;

            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject = jsonelement.getAsJsonObject();

                s = ChatDeserializer.getAsString(jsonobject, "id");
                flag = ChatDeserializer.getAsBoolean(jsonobject, "required", true);
            } else {
                s = ChatDeserializer.convertToString(jsonelement, "id");
                flag = true;
            }

            MinecraftKey minecraftkey;

            if (s.startsWith("#")) {
                minecraftkey = new MinecraftKey(s.substring(1));
                return (Tag.d) (flag ? new Tag.g(minecraftkey) : new Tag.f(minecraftkey));
            } else {
                minecraftkey = new MinecraftKey(s);
                return (Tag.d) (flag ? new Tag.c(minecraftkey) : new Tag.e(minecraftkey));
            }
        }

        public JsonObject serializeToJson() {
            JsonObject jsonobject = new JsonObject();
            JsonArray jsonarray = new JsonArray();
            Iterator iterator = this.entries.iterator();

            while (iterator.hasNext()) {
                Tag.b tag_b = (Tag.b) iterator.next();

                tag_b.entry().serializeTo(jsonarray);
            }

            jsonobject.addProperty("replace", false);
            jsonobject.add("values", jsonarray);
            return jsonobject;
        }
    }

    public static record b(Tag.d a, String b) {

        final Tag.d entry;
        private final String source;

        public b(Tag.d tag_d, String s) {
            this.entry = tag_d;
            this.source = s;
        }

        public String toString() {
            return this.entry + " (from " + this.source + ")";
        }

        public Tag.d entry() {
            return this.entry;
        }

        public String source() {
            return this.source;
        }
    }
}
