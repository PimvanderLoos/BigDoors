package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootEnchantFunction extends LootItemFunctionConditional {

    public static final int NO_LIMIT = 0;
    final NumberProvider value;
    final int limit;

    LootEnchantFunction(LootItemCondition[] alootitemcondition, NumberProvider numberprovider, int i) {
        super(alootitemcondition);
        this.value = numberprovider;
        this.limit = i;
    }

    @Override
    public LootItemFunctionType a() {
        return LootItemFunctions.LOOTING_ENCHANT;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return Sets.union(ImmutableSet.of(LootContextParameters.KILLER_ENTITY), this.value.b());
    }

    boolean c() {
        return this.limit > 0;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        Entity entity = (Entity) loottableinfo.getContextParameter(LootContextParameters.KILLER_ENTITY);

        if (entity instanceof EntityLiving) {
            int i = EnchantmentManager.g((EntityLiving) entity);

            if (i == 0) {
                return itemstack;
            }

            float f = (float) i * this.value.b(loottableinfo);

            itemstack.add(Math.round(f));
            if (this.c() && itemstack.getCount() > this.limit) {
                itemstack.setCount(this.limit);
            }
        }

        return itemstack;
    }

    public static LootEnchantFunction.a a(NumberProvider numberprovider) {
        return new LootEnchantFunction.a(numberprovider);
    }

    public static class a extends LootItemFunctionConditional.a<LootEnchantFunction.a> {

        private final NumberProvider count;
        private int limit = 0;

        public a(NumberProvider numberprovider) {
            this.count = numberprovider;
        }

        @Override
        protected LootEnchantFunction.a d() {
            return this;
        }

        public LootEnchantFunction.a a(int i) {
            this.limit = i;
            return this;
        }

        @Override
        public LootItemFunction b() {
            return new LootEnchantFunction(this.g(), this.count, this.limit);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootEnchantFunction> {

        public b() {}

        public void a(JsonObject jsonobject, LootEnchantFunction lootenchantfunction, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootenchantfunction, jsonserializationcontext);
            jsonobject.add("count", jsonserializationcontext.serialize(lootenchantfunction.value));
            if (lootenchantfunction.c()) {
                jsonobject.add("limit", jsonserializationcontext.serialize(lootenchantfunction.limit));
            }

        }

        @Override
        public LootEnchantFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            int i = ChatDeserializer.a(jsonobject, "limit", (int) 0);

            return new LootEnchantFunction(alootitemcondition, (NumberProvider) ChatDeserializer.a(jsonobject, "count", jsondeserializationcontext, NumberProvider.class), i);
        }
    }
}
