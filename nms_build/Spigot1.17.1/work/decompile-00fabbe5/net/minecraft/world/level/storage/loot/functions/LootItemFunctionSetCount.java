package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootItemFunctionSetCount extends LootItemFunctionConditional {

    final NumberProvider value;
    final boolean add;

    LootItemFunctionSetCount(LootItemCondition[] alootitemcondition, NumberProvider numberprovider, boolean flag) {
        super(alootitemcondition);
        this.value = numberprovider;
        this.add = flag;
    }

    @Override
    public LootItemFunctionType a() {
        return LootItemFunctions.SET_COUNT;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return this.value.b();
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        int i = this.add ? itemstack.getCount() : 0;

        itemstack.setCount(MathHelper.clamp(i + this.value.a(loottableinfo), 0, itemstack.getMaxStackSize()));
        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> a(NumberProvider numberprovider) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionSetCount(alootitemcondition, numberprovider, false);
        });
    }

    public static LootItemFunctionConditional.a<?> a(NumberProvider numberprovider, boolean flag) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionSetCount(alootitemcondition, numberprovider, flag);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetCount> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetCount lootitemfunctionsetcount, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetcount, jsonserializationcontext);
            jsonobject.add("count", jsonserializationcontext.serialize(lootitemfunctionsetcount.value));
            jsonobject.addProperty("add", lootitemfunctionsetcount.add);
        }

        @Override
        public LootItemFunctionSetCount b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.a(jsonobject, "count", jsondeserializationcontext, NumberProvider.class);
            boolean flag = ChatDeserializer.a(jsonobject, "add", false);

            return new LootItemFunctionSetCount(alootitemcondition, numberprovider, flag);
        }
    }
}
