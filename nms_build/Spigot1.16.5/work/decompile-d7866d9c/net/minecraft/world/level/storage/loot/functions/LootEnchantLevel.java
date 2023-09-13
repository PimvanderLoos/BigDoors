package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootValue;
import net.minecraft.world.level.storage.loot.LootValueGenerators;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootEnchantLevel extends LootItemFunctionConditional {

    private final LootValue a;
    private final boolean b;

    private LootEnchantLevel(LootItemCondition[] alootitemcondition, LootValue lootvalue, boolean flag) {
        super(alootitemcondition);
        this.a = lootvalue;
        this.b = flag;
    }

    @Override
    public LootItemFunctionType b() {
        return LootItemFunctions.c;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        Random random = loottableinfo.a();

        return EnchantmentManager.a(random, itemstack, this.a.a(random), this.b);
    }

    public static LootEnchantLevel.a a(LootValue lootvalue) {
        return new LootEnchantLevel.a(lootvalue);
    }

    public static class b extends LootItemFunctionConditional.c<LootEnchantLevel> {

        public b() {}

        public void a(JsonObject jsonobject, LootEnchantLevel lootenchantlevel, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootenchantlevel, jsonserializationcontext);
            jsonobject.add("levels", LootValueGenerators.a(lootenchantlevel.a, jsonserializationcontext));
            jsonobject.addProperty("treasure", lootenchantlevel.b);
        }

        @Override
        public LootEnchantLevel b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            LootValue lootvalue = LootValueGenerators.a(jsonobject.get("levels"), jsondeserializationcontext);
            boolean flag = ChatDeserializer.a(jsonobject, "treasure", false);

            return new LootEnchantLevel(alootitemcondition, lootvalue, flag);
        }
    }

    public static class a extends LootItemFunctionConditional.a<LootEnchantLevel.a> {

        private final LootValue a;
        private boolean b;

        public a(LootValue lootvalue) {
            this.a = lootvalue;
        }

        @Override
        protected LootEnchantLevel.a d() {
            return this;
        }

        public LootEnchantLevel.a e() {
            this.b = true;
            return this;
        }

        @Override
        public LootItemFunction b() {
            return new LootEnchantLevel(this.g(), this.a, this.b);
        }
    }
}
