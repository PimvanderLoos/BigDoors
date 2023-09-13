package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionSetDamage extends LootItemFunction {

    private static final Logger a = LogManager.getLogger();
    private final LootValueBounds b;

    public LootItemFunctionSetDamage(LootItemCondition[] alootitemcondition, LootValueBounds lootvaluebounds) {
        super(alootitemcondition);
        this.b = lootvaluebounds;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        if (itemstack.f()) {
            float f = 1.0F - this.b.b(random);

            itemstack.setData(MathHelper.d(f * (float) itemstack.k()));
        } else {
            LootItemFunctionSetDamage.a.warn("Couldn\'t set damage of loot item {}", itemstack);
        }

        return itemstack;
    }

    public static class a extends LootItemFunction.a<LootItemFunctionSetDamage> {

        protected a() {
            super(new MinecraftKey("set_damage"), LootItemFunctionSetDamage.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionSetDamage lootitemfunctionsetdamage, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("damage", jsonserializationcontext.serialize(lootitemfunctionsetdamage.b));
        }

        public LootItemFunctionSetDamage a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return new LootItemFunctionSetDamage(alootitemcondition, (LootValueBounds) ChatDeserializer.a(jsonobject, "damage", jsondeserializationcontext, LootValueBounds.class));
        }

        public LootItemFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return this.a(jsonobject, jsondeserializationcontext, alootitemcondition);
        }
    }
}
