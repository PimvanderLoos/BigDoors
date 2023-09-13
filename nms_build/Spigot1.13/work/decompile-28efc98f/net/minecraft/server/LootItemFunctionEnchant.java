package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionEnchant extends LootItemFunction {

    private static final Logger a = LogManager.getLogger();
    private final List<Enchantment> b;

    public LootItemFunctionEnchant(LootItemCondition[] alootitemcondition, @Nullable List<Enchantment> list) {
        super(alootitemcondition);
        this.b = list == null ? Collections.emptyList() : list;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        Enchantment enchantment;

        if (this.b.isEmpty()) {
            ArrayList arraylist = Lists.newArrayList();
            Iterator iterator = Enchantment.enchantments.iterator();

            while (iterator.hasNext()) {
                Enchantment enchantment1 = (Enchantment) iterator.next();

                if (itemstack.getItem() == Items.BOOK || enchantment1.canEnchant(itemstack)) {
                    arraylist.add(enchantment1);
                }
            }

            if (arraylist.isEmpty()) {
                LootItemFunctionEnchant.a.warn("Couldn\'t find a compatible enchantment for {}", itemstack);
                return itemstack;
            }

            enchantment = (Enchantment) arraylist.get(random.nextInt(arraylist.size()));
        } else {
            enchantment = (Enchantment) this.b.get(random.nextInt(this.b.size()));
        }

        int i = MathHelper.nextInt(random, enchantment.getStartLevel(), enchantment.getMaxLevel());

        if (itemstack.getItem() == Items.BOOK) {
            itemstack = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantedBook.a(itemstack, new WeightedRandomEnchant(enchantment, i));
        } else {
            itemstack.addEnchantment(enchantment, i);
        }

        return itemstack;
    }

    public static class a extends LootItemFunction.a<LootItemFunctionEnchant> {

        public a() {
            super(new MinecraftKey("enchant_randomly"), LootItemFunctionEnchant.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionEnchant lootitemfunctionenchant, JsonSerializationContext jsonserializationcontext) {
            if (!lootitemfunctionenchant.b.isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = lootitemfunctionenchant.b.iterator();

                while (iterator.hasNext()) {
                    Enchantment enchantment = (Enchantment) iterator.next();
                    MinecraftKey minecraftkey = (MinecraftKey) Enchantment.enchantments.b(enchantment);

                    if (minecraftkey == null) {
                        throw new IllegalArgumentException("Don\'t know how to serialize enchantment " + enchantment);
                    }

                    jsonarray.add(new JsonPrimitive(minecraftkey.toString()));
                }

                jsonobject.add("enchantments", jsonarray);
            }

        }

        public LootItemFunctionEnchant a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            ArrayList arraylist = Lists.newArrayList();

            if (jsonobject.has("enchantments")) {
                JsonArray jsonarray = ChatDeserializer.u(jsonobject, "enchantments");
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement = (JsonElement) iterator.next();
                    String s = ChatDeserializer.a(jsonelement, "enchantment");
                    Enchantment enchantment = (Enchantment) Enchantment.enchantments.get(new MinecraftKey(s));

                    if (enchantment == null) {
                        throw new JsonSyntaxException("Unknown enchantment \'" + s + "\'");
                    }

                    arraylist.add(enchantment);
                }
            }

            return new LootItemFunctionEnchant(alootitemcondition, arraylist);
        }

        public LootItemFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return this.a(jsonobject, jsondeserializationcontext, alootitemcondition);
        }
    }
}
