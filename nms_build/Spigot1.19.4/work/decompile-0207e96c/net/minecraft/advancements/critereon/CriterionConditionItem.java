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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.TagKey;
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
    private final TagKey<Item> tag;
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

    public CriterionConditionItem(@Nullable TagKey<Item> tagkey, @Nullable Set<Item> set, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1, CriterionConditionEnchantments[] acriterionconditionenchantments, CriterionConditionEnchantments[] acriterionconditionenchantments1, @Nullable PotionRegistry potionregistry, CriterionConditionNBT criterionconditionnbt) {
        this.tag = tagkey;
        this.items = set;
        this.count = criterionconditionvalue_integerrange;
        this.durability = criterionconditionvalue_integerrange1;
        this.enchantments = acriterionconditionenchantments;
        this.storedEnchantments = acriterionconditionenchantments1;
        this.potion = potionregistry;
        this.nbt = criterionconditionnbt;
    }

    public boolean matches(ItemStack itemstack) {
        if (this == CriterionConditionItem.ANY) {
            return true;
        } else if (this.tag != null && !itemstack.is(this.tag)) {
            return false;
        } else if (this.items != null && !this.items.contains(itemstack.getItem())) {
            return false;
        } else if (!this.count.matches(itemstack.getCount())) {
            return false;
        } else if (!this.durability.isAny() && !itemstack.isDamageableItem()) {
            return false;
        } else if (!this.durability.matches(itemstack.getMaxDamage() - itemstack.getDamageValue())) {
            return false;
        } else if (!this.nbt.matches(itemstack)) {
            return false;
        } else {
            Map map;
            CriterionConditionEnchantments[] acriterionconditionenchantments;
            int i;
            CriterionConditionEnchantments criterionconditionenchantments;
            int j;

            if (this.enchantments.length > 0) {
                map = EnchantmentManager.deserializeEnchantments(itemstack.getEnchantmentTags());
                acriterionconditionenchantments = this.enchantments;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    if (!criterionconditionenchantments.containedIn(map)) {
                        return false;
                    }
                }
            }

            if (this.storedEnchantments.length > 0) {
                map = EnchantmentManager.deserializeEnchantments(ItemEnchantedBook.getEnchantments(itemstack));
                acriterionconditionenchantments = this.storedEnchantments;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    if (!criterionconditionenchantments.containedIn(map)) {
                        return false;
                    }
                }
            }

            PotionRegistry potionregistry = PotionUtil.getPotion(itemstack);

            return this.potion == null || this.potion == potionregistry;
        }
    }

    public static CriterionConditionItem fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "item");
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("count"));
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("durability"));

            if (jsonobject.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.fromJson(jsonobject.get("nbt"));
                Set<Item> set = null;
                JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "items", (JsonArray) null);

                if (jsonarray != null) {
                    Builder<Item> builder = ImmutableSet.builder();
                    Iterator iterator = jsonarray.iterator();

                    while (iterator.hasNext()) {
                        JsonElement jsonelement1 = (JsonElement) iterator.next();
                        MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.convertToString(jsonelement1, "item"));

                        builder.add((Item) BuiltInRegistries.ITEM.getOptional(minecraftkey).orElseThrow(() -> {
                            return new JsonSyntaxException("Unknown item id '" + minecraftkey + "'");
                        }));
                    }

                    set = builder.build();
                }

                TagKey<Item> tagkey = null;

                if (jsonobject.has("tag")) {
                    MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "tag"));

                    tagkey = TagKey.create(Registries.ITEM, minecraftkey1);
                }

                PotionRegistry potionregistry = null;

                if (jsonobject.has("potion")) {
                    MinecraftKey minecraftkey2 = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "potion"));

                    potionregistry = (PotionRegistry) BuiltInRegistries.POTION.getOptional(minecraftkey2).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown potion '" + minecraftkey2 + "'");
                    });
                }

                CriterionConditionEnchantments[] acriterionconditionenchantments = CriterionConditionEnchantments.fromJsonArray(jsonobject.get("enchantments"));
                CriterionConditionEnchantments[] acriterionconditionenchantments1 = CriterionConditionEnchantments.fromJsonArray(jsonobject.get("stored_enchantments"));

                return new CriterionConditionItem(tagkey, set, criterionconditionvalue_integerrange, criterionconditionvalue_integerrange1, acriterionconditionenchantments, acriterionconditionenchantments1, potionregistry, criterionconditionnbt);
            }
        } else {
            return CriterionConditionItem.ANY;
        }
    }

    public JsonElement serializeToJson() {
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

                    jsonarray.add(BuiltInRegistries.ITEM.getKey(item).toString());
                }

                jsonobject.add("items", jsonarray);
            }

            if (this.tag != null) {
                jsonobject.addProperty("tag", this.tag.location().toString());
            }

            jsonobject.add("count", this.count.serializeToJson());
            jsonobject.add("durability", this.durability.serializeToJson());
            jsonobject.add("nbt", this.nbt.serializeToJson());
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
                    jsonarray.add(criterionconditionenchantments.serializeToJson());
                }

                jsonobject.add("enchantments", jsonarray);
            }

            if (this.storedEnchantments.length > 0) {
                jsonarray = new JsonArray();
                acriterionconditionenchantments = this.storedEnchantments;
                i = acriterionconditionenchantments.length;

                for (j = 0; j < i; ++j) {
                    criterionconditionenchantments = acriterionconditionenchantments[j];
                    jsonarray.add(criterionconditionenchantments.serializeToJson());
                }

                jsonobject.add("stored_enchantments", jsonarray);
            }

            if (this.potion != null) {
                jsonobject.addProperty("potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
            }

            return jsonobject;
        }
    }

    public static CriterionConditionItem[] fromJsonArray(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonArray jsonarray = ChatDeserializer.convertToJsonArray(jsonelement, "items");
            CriterionConditionItem[] acriterionconditionitem = new CriterionConditionItem[jsonarray.size()];

            for (int i = 0; i < acriterionconditionitem.length; ++i) {
                acriterionconditionitem[i] = fromJson(jsonarray.get(i));
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
        private TagKey<Item> tag;
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

        public static CriterionConditionItem.a item() {
            return new CriterionConditionItem.a();
        }

        public CriterionConditionItem.a of(IMaterial... aimaterial) {
            this.items = (Set) Stream.of(aimaterial).map(IMaterial::asItem).collect(ImmutableSet.toImmutableSet());
            return this;
        }

        public CriterionConditionItem.a of(TagKey<Item> tagkey) {
            this.tag = tagkey;
            return this;
        }

        public CriterionConditionItem.a withCount(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            this.count = criterionconditionvalue_integerrange;
            return this;
        }

        public CriterionConditionItem.a hasDurability(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            this.durability = criterionconditionvalue_integerrange;
            return this;
        }

        public CriterionConditionItem.a isPotion(PotionRegistry potionregistry) {
            this.potion = potionregistry;
            return this;
        }

        public CriterionConditionItem.a hasNbt(NBTTagCompound nbttagcompound) {
            this.nbt = new CriterionConditionNBT(nbttagcompound);
            return this;
        }

        public CriterionConditionItem.a hasEnchantment(CriterionConditionEnchantments criterionconditionenchantments) {
            this.enchantments.add(criterionconditionenchantments);
            return this;
        }

        public CriterionConditionItem.a hasStoredEnchantment(CriterionConditionEnchantments criterionconditionenchantments) {
            this.storedEnchantments.add(criterionconditionenchantments);
            return this;
        }

        public CriterionConditionItem build() {
            return new CriterionConditionItem(this.tag, this.items, this.count, this.durability, (CriterionConditionEnchantments[]) this.enchantments.toArray(CriterionConditionEnchantments.NONE), (CriterionConditionEnchantments[]) this.storedEnchantments.toArray(CriterionConditionEnchantments.NONE), this.potion, this.nbt);
        }
    }
}
