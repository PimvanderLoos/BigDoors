package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class LootItemConditionRandomChance implements LootItemCondition {

    final float probability;

    LootItemConditionRandomChance(float f) {
        this.probability = f;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.RANDOM_CHANCE;
    }

    public boolean test(LootTableInfo loottableinfo) {
        return loottableinfo.getRandom().nextFloat() < this.probability;
    }

    public static LootItemCondition.a randomChance(float f) {
        return () -> {
            return new LootItemConditionRandomChance(f);
        };
    }

    public static class a implements LootSerializer<LootItemConditionRandomChance> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemConditionRandomChance lootitemconditionrandomchance, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("chance", lootitemconditionrandomchance.probability);
        }

        @Override
        public LootItemConditionRandomChance deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return new LootItemConditionRandomChance(ChatDeserializer.getAsFloat(jsonobject, "chance"));
        }
    }
}
