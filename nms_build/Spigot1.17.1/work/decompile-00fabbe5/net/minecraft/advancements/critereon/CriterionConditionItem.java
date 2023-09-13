package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsInstance;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemEnchantedBook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.IMaterial;

public class CriterionConditionItem {

    public static final CriterionConditionItem ANY = new CriterionConditionItem();
    @Nullable
    private final Tag<Item> tag;
    @Nullable
    private final Set<Item> items;
    private final CriterionConditionValue.IntegerRange count;
    private final CriterionConditionValue.IntegerRange durability;
    private final CriterionConditionEnchantments[] enchantments;
    private final CriterionConditionEnchantments[] storedEnchantments;
    @Nullable
    private final PotionRegistry potion;
    private final CriterionConditionNBT nbt;

    public CriterionConditionItem() {
        this.tag = null;
        this.items = null;
        this.potion = null;
        this.count = CriterionConditionValue.IntegerRange.ANY;
        this.durability = CriterionConditionValue.IntegerRange.ANY;
        this.enchantments = CriterionConditionEnchantments.NONE;
        this.storedEnchantments = CriterionConditionEnchantments.NONE;
        this.nbt = CriterionConditionNBT.ANY;
    }

    public CriterionConditionItem(@Nullable Tag<Item> tag, @Nullable Set<Item> set, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1, CriterionConditionEnchantments[] acriterionconditionenchantments, CriterionConditionEnchantments[] acriterionconditionenchantments1, @Nullable PotionRegistry potionregistry, CriterionConditionNBT criterionconditionnbt) {
        this.tag = tag;
        this.items = set;
        this.count = criterionconditionvalue_integerrange;
        this.durability = criterionconditionvalue_integerrange1;
        this.enchantments = acriterionconditionenchantments;
        this.storedEnchantments = acriterionconditionenchantments1;
        this.potion = potionregistry;
        this.nbt = criterionconditionnbt;
    }

    public boolean a(ItemStack itemstack) {
        if (this == CriterionConditionItem.ANY) {
            return true;
        } else if (this.tag != null && !itemstack.a(this.tag)) {
            return false;
        } else if (this.items != null && !this.items.contains(itemstack.getItem())) {
            return false;
        } else if (!this.count.d(itemstack.getCount())) {
            return false;
        } else if (!this.durability.c() && !itemstack.f()) {
            return false;
        } else if (!this.durability.d(itemstack.i() - itemstack.getDamage())) {
            return false;
        } else if (!this.nbt.a(itemstack)) {
            return false;
        } else {
            Map map;
            CriterionConditionEnchantments[] acriterionconditionenchantments;
            int i;
            CriterionConditionEnchantments criterionconditionenchantments;
            int j;

            if (this.enchantments.length > 0) {
                map = EnchantmentManager.a(itemstack.getEnchantments());
                acriterionconditionenchantments = this.enchantments;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    if (!criterionconditionenchantments.a(map)) {
                        return false;
                    }
                }
            }

            if (this.storedEnchantments.length > 0) {
                map = EnchantmentManager.a(ItemEnchantedBook.d(itemstack));
                acriterionconditionenchantments = this.storedEnchantments;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    if (!criterionconditionenchantments.a(map)) {
                        return false;
                    }
                }
            }

            PotionRegistry potionregistry = PotionUtil.d(itemstack);

            return this.potion == null || this.potion == potionregistry;
        }
    }

    public static CriterionConditionItem a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "item");
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("count"));
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.a(jsonobject.get("durability"));

            if (jsonobject.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.a(jsonobject.get("nbt"));
                Set<Item> set = null;
                JsonArray jsonarray = ChatDeserializer.a(jsonobject, "items", (JsonArray) null);

                if (jsonarray != null) {
                    Builder<Item> builder = ImmutableSet.builder();
                    Iterator iterator = jsonarray.iterator();

                    while (iterator.hasNext()) {
                        JsonElement jsonelement1 = (JsonElement) iterator.next();
                        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.a(jsonelement1, "item"));

                        builder.add((Item) IRegistry.ITEM.getOptional(minecraftkey).orElseThrow(() -> {
                            return new JsonSyntaxException("Unknown item id '" + minecraftkey + "'");
                        }));
                    }

                    set = builder.build();
                }

                Tag<Item> tag = null;

                if (jsonobject.has("tag")) {
                    MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.h(jsonobject, "tag"));

                    tag = TagsInstance.a().a(IRegistry.ITEM_REGISTRY, minecraftkey1, (minecraftkey2) -> {
                        return new JsonSyntaxException("Unknown item tag '" + minecraftkey2 + "'");
                    });
                }

                PotionRegistry potionregistry = null;

                if (jsonobject.has("potion")) {
                    MinecraftKey minecraftkey2 = new MinecraftKey(ChatDeserializer.h(jsonobject, "potion"));

                    potionregistry = (PotionRegistry) IRegistry.POTION.getOptional(minecraftkey2).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown potion '" + minecraftkey2 + "'");
                    });
                }

                CriterionConditionEnchantments[] acriterionconditionenchantments = CriterionConditionEnchantments.b(jsonobject.get("enchantments"));
                CriterionConditionEnchantments[] acriterionconditionenchantments1 = CriterionConditionEnchantments.b(jsonobject.get("stored_enchantments"));

                return new CriterionConditionItem(tag, set, criterionconditionvalue_integerrange, criterionconditionvalue_integerrange1, acriterionconditionenchantments, acriterionconditionenchantments1, potionregistry, criterionconditionnbt);
            }
        } else {
            return CriterionConditionItem.ANY;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionItem.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();
            JsonArray jsonarray;

            if (this.items != null) {
                jsonarray = new JsonArray();
                Iterator iterator = this.items.iterator();

                while (iterator.hasNext()) {
                    Item item = (Item) iterator.next();

                    jsonarray.add(IRegistry.ITEM.getKey(item).toString());
                }

                jsonobject.add("items", jsonarray);
            }

            if (this.tag != null) {
                jsonobject.addProperty("tag", TagsInstance.a().a(IRegistry.ITEM_REGISTRY, this.tag, () -> {
                    return new IllegalStateException("Unknown item tag");
                }).toString());
            }

            jsonobject.add("count", this.count.d());
            jsonobject.add("durability", this.durability.d());
            jsonobject.add("nbt", this.nbt.a());
            CriterionConditionEnchantments[] acriterionconditionenchantments;
            int i;
            CriterionConditionEnchantments criterionconditionenchantments;
            int j;

            if (this.enchantments.length > 0) {
                jsonarray = new JsonArray();
                acriterionconditionenchantments = this.enchantments;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    jsonarray.add(criterionconditionenchantments.a());
                }

                jsonobject.add("enchantments", jsonarray);
            }

            if (this.storedEnchantments.length > 0) {
                jsonarray = new JsonArray();
                acriterionconditionenchantments = this.storedEnchantments;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    jsonarray.add(criterionconditionenchantments.a());
                }

                jsonobject.add("stored_enchantments", jsonarray);
            }

            if (this.potion != null) {
                jsonobject.addProperty("potion", IRegistry.POTION.getKey(this.potion).toString());
            }

            return jsonobject;
        }
    }

    public static CriterionConditionItem[] b(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonArray jsonarray = ChatDeserializer.n(jsonelement, "items");
            CriterionConditionItem[] acriterionconditionitem = new CriterionConditionItem[jsonarray.size()];

            for (int i = 0; i < acriterionconditionitem.length; ++i) {
                acriterionconditionitem[i] = a(jsonarray.get(i));
            }

            return acriterionconditionitem;
        } else {
            return new CriterionConditionItem[0];
        }
    }

    public static class a {

        private final List<CriterionConditionEnchantments> enchantments = Lists.newArrayList();
        private final List<CriterionConditionEnchantments> storedEnchantments = Lists.newArrayList();
        @Nullable
        private Set<Item> items;
        @Nullable
        private Tag<Item> tag;
        private CriterionConditionValue.IntegerRange count;
        private CriterionConditionValue.IntegerRange durability;
        @Nullable
        private PotionRegistry potion;
        private CriterionConditionNBT nbt;

        private a() {
            this.count = CriterionConditionValue.IntegerRange.ANY;
            this.durability = CriterionConditionValue.IntegerRange.ANY;
            this.nbt = CriterionConditionNBT.ANY;
        }

        public static CriterionConditionItem.a a() {
            return new CriterionConditionItem.a();
        }

        public CriterionConditionItem.a a(IMaterial... aimaterial) {
            this.items = (Set) Stream.of(aimaterial).map(IMaterial::getItem).collect(ImmutableSet.toImmutableSet());
            return this;
        }

        public CriterionConditionItem.a a(Tag<Item> tag) {
            this.tag = tag;
            return this;
        }

        public CriterionConditionItem.a a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            this.count = criterionconditionvalue_integerrange;
            return this;
        }

        public CriterionConditionItem.a b(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            this.durability = criterionconditionvalue_integerrange;
            return this;
        }

        public CriterionConditionItem.a a(PotionRegistry potionregistry) {
            this.potion = potionregistry;
            return this;
        }

        public CriterionConditionItem.a a(NBTTagCompound nbttagcompound) {
            this.nbt = new CriterionConditionNBT(nbttagcompound);
            return this;
        }

        public CriterionConditionItem.a a(CriterionConditionEnchantments criterionconditionenchantments) {
            this.enchantments.add(criterionconditionenchantments);
            return this;
        }

        public CriterionConditionItem.a b(CriterionConditionEnchantments criterionconditionenchantments) {
            this.storedEnchantments.add(criterionconditionenchantments);
            return this;
        }

        public CriterionConditionItem b() {
            return new CriterionConditionItem(this.tag, this.items, this.count, this.durability, (CriterionConditionEnchantments[]) this.enchantments.toArray(CriterionConditionEnchantments.NONE), (CriterionConditionEnchantments[]) this.storedEnchantments.toArray(CriterionConditionEnchantments.NONE), this.potion, this.nbt);
        }
    }
}
