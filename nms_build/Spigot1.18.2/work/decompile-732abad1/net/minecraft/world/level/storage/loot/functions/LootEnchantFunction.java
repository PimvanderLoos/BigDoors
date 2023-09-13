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
    public LootItemFunctionType getType() {
        return LootItemFunctions.LOOTING_ENCHANT;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return Sets.union(ImmutableSet.of(LootContextParameters.KILLER_ENTITY), this.value.getReferencedContextParams());
    }

    boolean hasLimit() {
        return this.limit > 0;
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        Entity entity = (Entity) loottableinfo.getParamOrNull(LootContextParameters.KILLER_ENTITY);

        if (entity instanceof EntityLiving) {
            int i = EnchantmentManager.getMobLooting((EntityLiving) entity);

            if (i == 0) {
                return itemstack;
            }

            float f = (float) i * this.value.getFloat(loottableinfo);

            itemstack.grow(Math.round(f));
            if (this.hasLimit() && itemstack.getCount() > this.limit) {
                itemstack.setCount(this.limit);
            }
        }

        return itemstack;
    }

    public static LootEnchantFunction.a lootingMultiplier(NumberProvider numberprovider) {
        return new LootEnchantFunction.a(numberprovider);
    }

    public static class a extends LootItemFunctionConditional.a<LootEnchantFunction.a> {

        private final NumberProvider count;
        private int limit = 0;

        public a(NumberProvider numberprovider) {
            this.count = numberprovider;
        }

        @Override
        protected LootEnchantFunction.a getThis() {
            return this;
        }

        public LootEnchantFunction.a setLimit(int i) {
            this.limit = i;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootEnchantFunction(this.getConditions(), this.count, this.limit);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootEnchantFunction> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootEnchantFunction lootenchantfunction, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootenchantfunction, jsonserializationcontext);
            jsonobject.add("count", jsonserializationcontext.serialize(lootenchantfunction.value));
            if (lootenchantfunction.hasLimit()) {
                jsonobject.add("limit", jsonserializationcontext.serialize(lootenchantfunction.limit));
            }

        }

        @Override
        public LootEnchantFunction deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            int i = ChatDeserializer.getAsInt(jsonobject, "limit", 0);

            return new LootEnchantFunction(alootitemcondition, (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "count", jsondeserializationcontext, NumberProvider.class), i);
        }
    }
}
