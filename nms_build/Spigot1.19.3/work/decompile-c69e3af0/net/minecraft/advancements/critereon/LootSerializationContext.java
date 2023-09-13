package net.minecraft.advancements.critereon;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.world.level.storage.loot.LootSerialization;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootSerializationContext {

    public static final LootSerializationContext INSTANCE = new LootSerializationContext();
    private final Gson predicateGson = LootSerialization.createConditionSerializer().create();

    public LootSerializationContext() {}

    public final JsonElement serializeConditions(LootItemCondition[] alootitemcondition) {
        return this.predicateGson.toJsonTree(alootitemcondition);
    }
}
