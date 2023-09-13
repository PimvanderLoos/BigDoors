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

    public JsonRegistry() {}

    public static <E, T extends LootSerializerType<E>> JsonRegistry.a<E, T> builder(IRegistry<T> iregistry, String s, String s1, Function<E, T> function) {
        return new JsonRegistry.a<>(iregistry, s, s1, function);
    }

    public static class a<E, T extends LootSerializerType<E>> {

        private final IRegistry<T> registry;
        private final String elementName;
        private final String typeKey;
        private final Function<E, T> typeGetter;
        @Nullable
        private Pair<T, JsonRegistry.b<? extends E>> inlineType;
        @Nullable
        private T defaultType;

        a(IRegistry<T> iregistry, String s, String s1, Function<E, T> function) {
            this.registry = iregistry;
            this.elementName = s;
            this.typeKey = s1;
            this.typeGetter = function;
        }

        public JsonRegistry.a<E, T> withInlineSerializer(T t0, JsonRegistry.b<? extends E> jsonregistry_b) {
            this.inlineType = Pair.of(t0, jsonregistry_b);
            return this;
        }

        public JsonRegistry.a<E, T> withDefaultType(T t0) {
            this.defaultType = t0;
            return this;
        }

        public Object build() {
            return new JsonRegistry.c<>(this.registry, this.elementName, this.typeKey, this.typeGetter, this.defaultType, this.inlineType);
        }
    }

    public interface b<T> {

        JsonElement serialize(T t0, JsonSerializationContext jsonserializationcontext);

        T deserialize(JsonElement jsonelement, JsonDeserializationContext jsondeserializationcontext);
    }

    private static class c<E, T extends LootSerializerType<E>> implements JsonDeserializer<E>, JsonSerializer<E> {

        private final IRegistry<T> registry;
        private final String elementName;
        private final String typeKey;
        private final Function<E, T> typeGetter;
        @Nullable
        private final T defaultType;
        @Nullable
        private final Pair<T, JsonRegistry.b<? extends E>> inlineType;

        c(IRegistry<T> iregistry, String s, String s1, Function<E, T> function, @Nullable T t0, @Nullable Pair<T, JsonRegistry.b<? extends E>> pair) {
            this.registry = iregistry;
            this.elementName = s;
            this.typeKey = s1;
            this.typeGetter = function;
            this.defaultType = t0;
            this.inlineType = pair;
        }

        public E deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, this.elementName);
                String s = ChatDeserializer.getAsString(jsonobject, this.typeKey, "");
                LootSerializerType lootserializertype;

                if (s.isEmpty()) {
                    lootserializertype = this.defaultType;
                } else {
                    MinecraftKey minecraftkey = new MinecraftKey(s);

                    lootserializertype = (LootSerializerType) this.registry.get(minecraftkey);
                }

                if (lootserializertype == null) {
                    throw new JsonSyntaxException("Unknown type '" + s + "'");
                } else {
                    return lootserializertype.getSerializer().deserialize(jsonobject, jsondeserializationcontext);
                }
            } else if (this.inlineType == null) {
                throw new UnsupportedOperationException("Object " + jsonelement + " can't be deserialized");
            } else {
                return ((JsonRegistry.b) this.inlineType.getSecond()).deserialize(jsonelement, jsondeserializationcontext);
            }
        }

        public JsonElement serialize(E e0, Type type, JsonSerializationContext jsonserializationcontext) {
            T t0 = (LootSerializerType) this.typeGetter.apply(e0);

            if (this.inlineType != null && this.inlineType.getFirst() == t0) {
                return ((JsonRegistry.b) this.inlineType.getSecond()).serialize(e0, jsonserializationcontext);
            } else if (t0 == null) {
                throw new JsonSyntaxException("Unknown type: " + e0);
            } else {
                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty(this.typeKey, this.registry.getKey(t0).toString());
                t0.getSerializer().serialize(jsonobject, e0, jsonserializationcontext);
                return jsonobject;
            }
        }
    }
}
