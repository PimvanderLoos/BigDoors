package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface LootSerializer<T> {

    void serialize(JsonObject jsonobject, T t0, JsonSerializationContext jsonserializationcontext);

    T deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext);
}
