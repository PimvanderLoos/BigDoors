package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootIntegerLimit;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionLimitCount extends LootItemFunctionConditional {

    private final LootIntegerLimit a;

    private LootItemFunctionLimitCount(LootItemCondition[] alootitemcondition, LootIntegerLimit lootintegerlimit) {
        super(alootitemcondition);
        this.a = lootintegerlimit;
    }

    @Override
    public LootItemFunctionType b() {
        return LootItemFunctions.o;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        int i = this.a.applyAsInt(itemstack.getCount());

        itemstack.setCount(i);
        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> a(LootIntegerLimit lootintegerlimit) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionLimitCount(alootitemcondition, lootintegerlimit);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionLimitCount> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionLimitCount lootitemfunctionlimitcount, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionlimitcount, jsonserializationcontext);
            jsonobject.add("limit", jsonserializationcontext.serialize(lootitemfunctionlimitcount.a));
        }

        @Override
        public LootItemFunctionLimitCount b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            LootIntegerLimit lootintegerlimit = (LootIntegerLimit) ChatDeserializer.a(jsonobject, "limit", jsondeserializationcontext, LootIntegerLimit.class);

            return new LootItemFunctionLimitCount(alootitemcondition, lootintegerlimit);
        }
    }
}
