package net.minecraft.advancements.critereon;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.world.level.storage.loot.LootSerialization;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootSerializationContext {

    public static final LootSerializationContext a = new LootSerializationContext();
    private final Gson b = LootSerialization.a().create();

    public LootSerializationContext() {}

    public final JsonElement a(LootItemCondition[] alootitemcondition) {
        return this.b.toJsonTree(alootitemcondition);
    }
}
