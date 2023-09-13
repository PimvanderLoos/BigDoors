package net.minecraft.world.level.storage.loot.providers.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public final class ConstantValue implements NumberProvider {

    final float value;

    ConstantValue(float f) {
        this.value = f;
    }

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.CONSTANT;
    }

    @Override
    public float getFloat(LootTableInfo loottableinfo) {
        return this.value;
    }

    public static ConstantValue exactly(float f) {
        return new ConstantValue(f);
    }

    public boolean equals(Object object) {
        return this == object ? true : (object != null && this.getClass() == object.getClass() ? Float.compare(((ConstantValue) object).value, this.value) == 0 : false);
    }

    public int hashCode() {
        return this.value != 0.0F ? Float.floatToIntBits(this.value) : 0;
    }

    public static class a implements JsonRegistry.b<ConstantValue> {

        public a() {}

        public JsonElement serialize(ConstantValue constantvalue, JsonSerializationContext jsonserializationcontext) {
            return new JsonPrimitive(constantvalue.value);
        }

        @Override
        public ConstantValue deserialize(JsonElement jsonelement, JsonDeserializationContext jsondeserializationcontext) {
            return new ConstantValue(ChatDeserializer.convertToFloat(jsonelement, "value"));
        }
    }

    public static class b implements LootSerializer<ConstantValue> {

        public b() {}

        public void serialize(JsonObject jsonobject, ConstantValue constantvalue, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("value", constantvalue.value);
        }

        @Override
        public ConstantValue deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            float f = ChatDeserializer.getAsFloat(jsonobject, "value");

            return new ConstantValue(f);
        }
    }
}
