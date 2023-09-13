package net.minecraft.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class LootSerializationContext {

    public static final LootSerializationContext a = new LootSerializationContext();
    private final Gson b = LootSerialization.a().create();

    public LootSerializationContext() {}

    public final JsonElement a(LootItemCondition[] alootitemcondition) {
        return this.b.toJsonTree(alootitemcondition);
    }
}
