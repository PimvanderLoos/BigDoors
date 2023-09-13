package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootEnchantLevel extends LootItemFunctionConditional {

    final NumberProvider levels;
    final boolean treasure;

    LootEnchantLevel(LootItemCondition[] alootitemcondition, NumberProvider numberprovider, boolean flag) {
        super(alootitemcondition);
        this.levels = numberprovider;
        this.treasure = flag;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.ENCHANT_WITH_LEVELS;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return this.levels.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        RandomSource randomsource = loottableinfo.getRandom();

        return EnchantmentManager.enchantItem(randomsource, itemstack, this.levels.getInt(loottableinfo), this.treasure);
    }

    public static LootEnchantLevel.a enchantWithLevels(NumberProvider numberprovider) {
        return new LootEnchantLevel.a(numberprovider);
    }

    public static class a extends LootItemFunctionConditional.a<LootEnchantLevel.a> {

        private final NumberProvider levels;
        private boolean treasure;

        public a(NumberProvider numberprovider) {
            this.levels = numberprovider;
        }

        @Override
        protected LootEnchantLevel.a getThis() {
            return this;
        }

        public LootEnchantLevel.a allowTreasure() {
            this.treasure = true;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootEnchantLevel(this.getConditions(), this.levels, this.treasure);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootEnchantLevel> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootEnchantLevel lootenchantlevel, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootenchantlevel, jsonserializationcontext);
            jsonobject.add("levels", jsonserializationcontext.serialize(lootenchantlevel.levels));
            jsonobject.addProperty("treasure", lootenchantlevel.treasure);
        }

        @Override
        public LootEnchantLevel deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "levels", jsondeserializationcontext, NumberProvider.class);
            boolean flag = ChatDeserializer.getAsBoolean(jsonobject, "treasure", false);

            return new LootEnchantLevel(alootitemcondition, numberprovider, flag);
        }
    }
}
