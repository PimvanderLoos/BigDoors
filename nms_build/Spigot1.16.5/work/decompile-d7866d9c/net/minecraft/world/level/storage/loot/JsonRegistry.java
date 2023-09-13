package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;

public class JsonRegistry {

    public static <E, T extends LootSerializerType<E>> JsonRegistry.a<E, T> a(IRegistry<T> iregistry, String s, String s1, Function<E, T> function) {
        return new JsonRegistry.a<>(iregistry, s, s1, function);
    }

    public interface b<T> {

        JsonElement a(T t0, JsonSerializationContext jsonserializationcontext);

        T a(JsonElement jsonelement, JsonDeserializationContext jsondeserializationcontext);
    }

    static class c<E, T extends LootSerializerType<E>> implements JsonDeserializer<E>, JsonSerializer<E> {

        private final IRegistry<T> a;
        private final String b;
        private final String c;
        private final Function<E, T> d;
        @Nullable
        private final Pair<T, JsonRegistry.b<? extends E>> e;

        private c(IRegistry<T> iregistry, String s, String s1, Function<E, T> function, @Nullable Pair<T, JsonRegistry.b<? extends E>> pair) {
            this.a = iregistry;
            this.b = s;
            this.c = s1;
            this.d = function;
            this.e = pair;
        }

        public E deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject = ChatDeserializer.m(jsonelement, this.b);
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, this.c));
                T t0 = (LootSerializerType) this.a.get(minecraftkey);

                if (t0 == null) {
                    throw new JsonSyntaxException("Unknown type '" + minecraftkey + "'");
                } else {
                    return t0.a().a(jsonobject, jsondeserializationcontext);
                }
            } else if (this.e == null) {
                throw new UnsupportedOperationException("Object " + jsonelement + " can't be deserialized");
            } else {
                return ((JsonRegistry.b) this.e.getSecond()).a(jsonelement, jsondeserializationcontext);
            }
        }

        public JsonElement serialize(E e0, Type type, JsonSerializationContext jsonserializationcontext) {
            T t0 = (LootSerializerType) this.d.apply(e0);

            if (this.e != null && this.e.getFirst() == t0) {
                return ((JsonRegistry.b) this.e.getSecond()).a(e0, jsonserializationcontext);
            } else if (t0 == null) {
                throw new JsonSyntaxException("Unknown type: " + e0);
            } else {
                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty(this.c, this.a.getKey(t0).toString());
                t0.a().a(jsonobject, e0, jsonserializationcontext);
                return jsonobject;
            }
        }
    }

    public static class a<E, T extends LootSerializerType<E>> {

        private final IRegistry<T> a;
        private final String b;
        private final String c;
        private final Function<E, T> d;
        @Nullable
        private Pair<T, JsonRegistry.b<? extends E>> e;

        private a(IRegistry<T> iregistry, String s, String s1, Function<E, T> function) {
            this.a = iregistry;
            this.b = s;
            this.c = s1;
            this.d = function;
        }

        public Object a() {
            return new JsonRegistry.c<>(this.a, this.b, this.c, this.d, this.e);
        }
    }
}
