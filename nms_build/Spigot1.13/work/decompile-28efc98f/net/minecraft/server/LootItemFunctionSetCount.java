package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;

public class LootItemFunctionSetCount extends LootItemFunction {

    private final LootValueBounds a;

    public LootItemFunctionSetCount(LootItemCondition[] alootitemcondition, LootValueBounds lootvaluebounds) {
        super(alootitemcondition);
        this.a = lootvaluebounds;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        itemstack.setCount(this.a.a(random));
        return itemstack;
    }

    public static class a extends LootItemFunction.a<LootItemFunctionSetCount> {

        protected a() {
            super(new MinecraftKey("set_count"), LootItemFunctionSetCount.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionSetCount lootitemfunctionsetcount, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("count", jsonserializationcontext.serialize(lootitemfunctionsetcount.a));
        }

        public LootItemFunctionSetCount a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return new LootItemFunctionSetCount(alootitemcondition, (LootValueBounds) ChatDeserializer.a(jsonobject, "count", jsondeserializationcontext, LootValueBounds.class));
        }

        public LootItemFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return this.a(jsonobject, jsondeserializationcontext, alootitemcondition);
        }
    }
}
