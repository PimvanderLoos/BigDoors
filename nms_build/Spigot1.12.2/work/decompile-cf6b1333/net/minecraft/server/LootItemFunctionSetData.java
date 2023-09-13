package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionSetData extends LootItemFunction {

    private static final Logger a = LogManager.getLogger();
    private final LootValueBounds b;

    public LootItemFunctionSetData(LootItemCondition[] alootitemcondition, LootValueBounds lootvaluebounds) {
        super(alootitemcondition);
        this.b = lootvaluebounds;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        if (itemstack.f()) {
            LootItemFunctionSetData.a.warn("Couldn\'t set data of loot item {}", itemstack);
        } else {
            itemstack.setData(this.b.a(random));
        }

        return itemstack;
    }

    public static class a extends LootItemFunction.a<LootItemFunctionSetData> {

        protected a() {
            super(new MinecraftKey("set_data"), LootItemFunctionSetData.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionSetData lootitemfunctionsetdata, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("data", jsonserializationcontext.serialize(lootitemfunctionsetdata.b));
        }

        public LootItemFunctionSetData a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return new LootItemFunctionSetData(alootitemcondition, (LootValueBounds) ChatDeserializer.a(jsonobject, "data", jsondeserializationcontext, LootValueBounds.class));
        }

        public LootItemFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return this.a(jsonobject, jsondeserializationcontext, alootitemcondition);
        }
    }
}
