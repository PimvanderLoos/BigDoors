package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootValue;
import net.minecraft.world.level.storage.loot.LootValueGenerators;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionSetCount extends LootItemFunctionConditional {

    private final LootValue a;

    private LootItemFunctionSetCount(LootItemCondition[] alootitemcondition, LootValue lootvalue) {
        super(alootitemcondition);
        this.a = lootvalue;
    }

    @Override
    public LootItemFunctionType b() {
        return LootItemFunctions.b;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        itemstack.setCount(this.a.a(loottableinfo.a()));
        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> a(LootValue lootvalue) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionSetCount(alootitemcondition, lootvalue);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetCount> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetCount lootitemfunctionsetcount, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetcount, jsonserializationcontext);
            jsonobject.add("count", LootValueGenerators.a(lootitemfunctionsetcount.a, jsonserializationcontext));
        }

        @Override
        public LootItemFunctionSetCount b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            LootValue lootvalue = LootValueGenerators.a(jsonobject.get("count"), jsondeserializationcontext);

            return new LootItemFunctionSetCount(alootitemcondition, lootvalue);
        }
    }
}
