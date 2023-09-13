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
    public LootItemConditionType a() {
        return LootItemConditions.RANDOM_CHANCE;
    }

    public boolean test(LootTableInfo loottableinfo) {
        return loottableinfo.a().nextFloat() < this.probability;
    }

    public static LootItemCondition.a a(float f) {
        return () -> {
            return new LootItemConditionRandomChance(f);
        };
    }

    public static class a implements LootSerializer<LootItemConditionRandomChance> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionRandomChance lootitemconditionrandomchance, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("chance", lootitemconditionrandomchance.probability);
        }

        @Override
        public LootItemConditionRandomChance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return new LootItemConditionRandomChance(ChatDeserializer.l(jsonobject, "chance"));
        }
    }
}
