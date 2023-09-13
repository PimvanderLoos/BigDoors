package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;

public class LootValueBounds {

    private final float a;
    private final float b;

    public LootValueBounds(float f, float f1) {
        this.a = f;
        this.b = f1;
    }

    public LootValueBounds(float f) {
        this.a = f;
        this.b = f;
    }

    public float a() {
        return this.a;
    }

    public float b() {
        return this.b;
    }

    public int a(Random random) {
        return MathHelper.nextInt(random, MathHelper.d(this.a), MathHelper.d(this.b));
    }

    public float b(Random random) {
        return MathHelper.a(random, this.a, this.b);
    }

    public boolean a(int i) {
        return (float) i <= this.b && (float) i >= this.a;
    }

    public static class a implements JsonDeserializer<LootValueBounds>, JsonSerializer<LootValueBounds> {

        public a() {}

        public LootValueBounds a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (ChatDeserializer.b(jsonelement)) {
                return new LootValueBounds(ChatDeserializer.e(jsonelement, "value"));
            } else {
                JsonObject jsonobject = ChatDeserializer.m(jsonelement, "value");
                float f = ChatDeserializer.l(jsonobject, "min");
                float f1 = ChatDeserializer.l(jsonobject, "max");

                return new LootValueBounds(f, f1);
            }
        }

        public JsonElement a(LootValueBounds lootvaluebounds, Type type, JsonSerializationContext jsonserializationcontext) {
            if (lootvaluebounds.a == lootvaluebounds.b) {
                return new JsonPrimitive(Float.valueOf(lootvaluebounds.a));
            } else {
                JsonObject jsonobject = new JsonObject();

                jsonobject.addProperty("min", Float.valueOf(lootvaluebounds.a));
                jsonobject.addProperty("max", Float.valueOf(lootvaluebounds.b));
                return jsonobject;
            }
        }

        public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonserializationcontext) {
            return this.a((LootValueBounds) object, type, jsonserializationcontext);
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }
}
