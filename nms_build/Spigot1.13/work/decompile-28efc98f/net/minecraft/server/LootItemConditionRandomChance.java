package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;

public class LootItemConditionRandomChance implements LootItemCondition {

    private final float a;

    public LootItemConditionRandomChance(float f) {
        this.a = f;
    }

    public boolean a(Random random, LootTableInfo loottableinfo) {
        return random.nextFloat() < this.a;
    }

    public static class a extends LootItemCondition.a<LootItemConditionRandomChance> {

        protected a() {
            super(new MinecraftKey("random_chance"), LootItemConditionRandomChance.class);
        }

        public void a(JsonObject jsonobject, LootItemConditionRandomChance lootitemconditionrandomchance, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("chance", Float.valueOf(lootitemconditionrandomchance.a));
        }

        public LootItemConditionRandomChance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return new LootItemConditionRandomChance(ChatDeserializer.l(jsonobject, "chance"));
        }

        public LootItemCondition b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return this.a(jsonobject, jsondeserializationcontext);
        }
    }
}
