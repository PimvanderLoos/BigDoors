package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionLimitCount extends LootItemFunctionConditional {

    final IntRange limiter;

    LootItemFunctionLimitCount(LootItemCondition[] alootitemcondition, IntRange intrange) {
        super(alootitemcondition);
        this.limiter = intrange;
    }

    @Override
    public LootItemFunctionType a() {
        return LootItemFunctions.LIMIT_COUNT;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return this.limiter.a();
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        int i = this.limiter.a(loottableinfo, itemstack.getCount());

        itemstack.setCount(i);
        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> a(IntRange intrange) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionLimitCount(alootitemcondition, intrange);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionLimitCount> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionLimitCount lootitemfunctionlimitcount, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionlimitcount, jsonserializationcontext);
            jsonobject.add("limit", jsonserializationcontext.serialize(lootitemfunctionlimitcount.limiter));
        }

        @Override
        public LootItemFunctionLimitCount b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            IntRange intrange = (IntRange) ChatDeserializer.a(jsonobject, "limit", jsondeserializationcontext, IntRange.class);

            return new LootItemFunctionLimitCount(alootitemcondition, intrange);
        }
    }
}
