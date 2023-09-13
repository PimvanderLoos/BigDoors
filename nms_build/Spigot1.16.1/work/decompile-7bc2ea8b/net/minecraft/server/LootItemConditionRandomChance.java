package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class LootItemConditionRandomChance implements LootItemCondition {

    private final float a;

    private LootItemConditionRandomChance(float f) {
        this.a = f;
    }

    @Override
    public LootItemConditionType b() {
        return LootItemConditions.c;
    }

    public boolean test(LootTableInfo loottableinfo) {
        return loottableinfo.a().nextFloat() < this.a;
    }

    public static LootItemCondition.a a(float f) {
        return () -> {
            return new LootItemConditionRandomChance(f);
        };
    }

    public static class a implements LootSerializer<LootItemConditionRandomChance> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionRandomChance lootitemconditionrandomchance, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("chance", lootitemconditionrandomchance.a);
        }

        @Override
        public LootItemConditionRandomChance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return new LootItemConditionRandomChance(ChatDeserializer.l(jsonobject, "chance"));
        }
    }
}
