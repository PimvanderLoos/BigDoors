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
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemEnchantedBook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.WeightedRandomEnchant;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class LootItemFunctionEnchant extends LootItemFunctionConditional {

    private static final Logger LOGGER = LogUtils.getLogger();
    final List<Enchantment> enchantments;

    LootItemFunctionEnchant(LootItemCondition[] alootitemcondition, Collection<Enchantment> collection) {
        super(alootitemcondition);
        this.enchantments = ImmutableList.copyOf(collection);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.ENCHANT_RANDOMLY;
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        RandomSource randomsource = loottableinfo.getRandom();
        Enchantment enchantment;

        if (this.enchantments.isEmpty()) {
            boolean flag = itemstack.is(Items.BOOK);
            List<Enchantment> list = (List) BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isDiscoverable).filter((enchantment1) -> {
                return flag || enchantment1.canEnchant(itemstack);
            }).collect(Collectors.toList());

            if (list.isEmpty()) {
                LootItemFunctionEnchant.LOGGER.warn("Couldn't find a compatible enchantment for {}", itemstack);
                return itemstack;
            }

            enchantment = (Enchantment) list.get(randomsource.nextInt(list.size()));
        } else {
            enchantment = (Enchantment) this.enchantments.get(randomsource.nextInt(this.enchantments.size()));
        }

        return enchantItem(itemstack, enchantment, randomsource);
    }

    private static ItemStack enchantItem(ItemStack itemstack, Enchantment enchantment, RandomSource randomsource) {
        int i = MathHelper.nextInt(randomsource, enchantment.getMinLevel(), enchantment.getMaxLevel());

        if (itemstack.is(Items.BOOK)) {
            itemstack = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantedBook.addEnchantment(itemstack, new WeightedRandomEnchant(enchantment, i));
        } else {
            itemstack.enchant(enchantment, i);
        }

        return itemstack;
    }

    public static LootItemFunctionEnchant.a randomEnchantment() {
        return new LootItemFunctionEnchant.a();
    }

    public static LootItemFunctionConditional.a<?> randomApplicableEnchantment() {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionEnchant(alootitemcondition, ImmutableList.of());
        });
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionEnchant.a> {

        private final Set<Enchantment> enchantments = Sets.newHashSet();

        public a() {}

        @Override
        protected LootItemFunctionEnchant.a getThis() {
            return this;
        }

        public LootItemFunctionEnchant.a withEnchantment(Enchantment enchantment) {
            this.enchantments.add(enchantment);
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootItemFunctionEnchant(this.getConditions(), this.enchantments);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionEnchant> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionEnchant lootitemfunctionenchant, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionenchant, jsonserializationcontext);
            if (!lootitemfunctionenchant.enchantments.isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = lootitemfunctionenchant.enchantments.iterator();

                while (iterator.hasNext()) {
                    Enchantment enchantment = (Enchantment) iterator.next();
                    MinecraftKey minecraftkey = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);

                    if (minecraftkey == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
                    }

                    jsonarray.add(new JsonPrimitive(minecraftkey.toString()));
                }

                jsonobject.add("enchantments", jsonarray);
            }

        }

        @Override
        public LootItemFunctionEnchant deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            List<Enchantment> list = Lists.newArrayList();

            if (jsonobject.has("enchantments")) {
                JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "enchantments");
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement = (JsonElement) iterator.next();
                    String s = ChatDeserializer.convertToString(jsonelement, "enchantment");
                    Enchantment enchantment = (Enchantment) BuiltInRegistries.ENCHANTMENT.getOptional(new MinecraftKey(s)).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown enchantment '" + s + "'");
                    });

                    list.add(enchantment);
                }
            }

            return new LootItemFunctionEnchant(alootitemcondition, list);
        }
    }
}
