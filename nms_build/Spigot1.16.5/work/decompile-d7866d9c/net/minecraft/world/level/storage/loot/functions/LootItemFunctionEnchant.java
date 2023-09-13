package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemEnchantedBook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.WeightedRandomEnchant;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionEnchant extends LootItemFunctionConditional {

    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Enchantment> b;

    private LootItemFunctionEnchant(LootItemCondition[] alootitemcondition, Collection<Enchantment> collection) {
        super(alootitemcondition);
        this.b = ImmutableList.copyOf(collection);
    }

    @Override
    public LootItemFunctionType b() {
        return LootItemFunctions.d;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        Random random = loottableinfo.a();
        Enchantment enchantment;

        if (this.b.isEmpty()) {
            boolean flag = itemstack.getItem() == Items.BOOK;
            List<Enchantment> list = (List) IRegistry.ENCHANTMENT.g().filter(Enchantment::i).filter((enchantment1) -> {
                return flag || enchantment1.canEnchant(itemstack);
            }).collect(Collectors.toList());

            if (list.isEmpty()) {
                LootItemFunctionEnchant.LOGGER.warn("Couldn't find a compatible enchantment for {}", itemstack);
                return itemstack;
            }

            enchantment = (Enchantment) list.get(random.nextInt(list.size()));
        } else {
            enchantment = (Enchantment) this.b.get(random.nextInt(this.b.size()));
        }

        return a(itemstack, enchantment, random);
    }

    private static ItemStack a(ItemStack itemstack, Enchantment enchantment, Random random) {
        int i = MathHelper.nextInt(random, enchantment.getStartLevel(), enchantment.getMaxLevel());

        if (itemstack.getItem() == Items.BOOK) {
            itemstack = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantedBook.a(itemstack, new WeightedRandomEnchant(enchantment, i));
        } else {
            itemstack.addEnchantment(enchantment, i);
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> d() {
        return a((alootitemcondition) -> {
            return new LootItemFunctionEnchant(alootitemcondition, ImmutableList.of());
        });
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionEnchant> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemFunctionEnchant lootitemfunctionenchant, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionenchant, jsonserializationcontext);
            if (!lootitemfunctionenchant.b.isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = lootitemfunctionenchant.b.iterator();

                while (iterator.hasNext()) {
                    Enchantment enchantment = (Enchantment) iterator.next();
                    MinecraftKey minecraftkey = IRegistry.ENCHANTMENT.getKey(enchantment);

                    if (minecraftkey == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
                    }

                    jsonarray.add(new JsonPrimitive(minecraftkey.toString()));
                }

                jsonobject.add("enchantments", jsonarray);
            }

        }

        @Override
        public LootItemFunctionEnchant b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            List<Enchantment> list = Lists.newArrayList();

            if (jsonobject.has("enchantments")) {
                JsonArray jsonarray = ChatDeserializer.u(jsonobject, "enchantments");
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement = (JsonElement) iterator.next();
                    String s = ChatDeserializer.a(jsonelement, "enchantment");
                    Enchantment enchantment = (Enchantment) IRegistry.ENCHANTMENT.getOptional(new MinecraftKey(s)).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown enchantment '" + s + "'");
                    });

                    list.add(enchantment);
                }
            }

            return new LootItemFunctionEnchant(alootitemcondition, list);
        }
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionEnchant.a> {

        private final Set<Enchantment> a = Sets.newHashSet();

        public a() {}

        @Override
        protected LootItemFunctionEnchant.a d() {
            return this;
        }

        public LootItemFunctionEnchant.a a(Enchantment enchantment) {
            this.a.add(enchantment);
            return this;
        }

        @Override
        public LootItemFunction b() {
            return new LootItemFunctionEnchant(this.g(), this.a);
        }
    }
}
