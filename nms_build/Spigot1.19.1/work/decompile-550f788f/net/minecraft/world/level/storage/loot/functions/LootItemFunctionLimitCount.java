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
    public LootItemFunctionType getType() {
        return LootItemFunctions.LIMIT_COUNT;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return this.limiter.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        int i = this.limiter.clamp(loottableinfo, itemstack.getCount());

        itemstack.setCount(i);
        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> limitCount(IntRange intrange) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionLimitCount(alootitemcondition, intrange);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionLimitCount> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionLimitCount lootitemfunctionlimitcount, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionlimitcount, jsonserializationcontext);
            jsonobject.add("limit", jsonserializationcontext.serialize(lootitemfunctionlimitcount.limiter));
        }

        @Override
        public LootItemFunctionLimitCount deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            IntRange intrange = (IntRange) ChatDeserializer.getAsObject(jsonobject, "limit", jsondeserializationcontext, IntRange.class);

            return new LootItemFunctionLimitCount(alootitemcondition, intrange);
        }
    }
}
