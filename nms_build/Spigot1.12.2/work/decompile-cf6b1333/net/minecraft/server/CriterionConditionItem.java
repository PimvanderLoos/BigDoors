package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;

public class CriterionConditionItem {

    public static final CriterionConditionItem a = new CriterionConditionItem();
    private final Item b;
    private final Integer c;
    private final CriterionConditionValue d;
    private final CriterionConditionValue e;
    private final CriterionConditionEnchantments[] f;
    private final PotionRegistry g;
    private final CriterionConditionNBT h;

    public CriterionConditionItem() {
        this.b = null;
        this.c = null;
        this.g = null;
        this.d = CriterionConditionValue.a;
        this.e = CriterionConditionValue.a;
        this.f = new CriterionConditionEnchantments[0];
        this.h = CriterionConditionNBT.a;
    }

    public CriterionConditionItem(@Nullable Item item, @Nullable Integer integer, CriterionConditionValue criterionconditionvalue, CriterionConditionValue criterionconditionvalue1, CriterionConditionEnchantments[] acriterionconditionenchantments, @Nullable PotionRegistry potionregistry, CriterionConditionNBT criterionconditionnbt) {
        this.b = item;
        this.c = integer;
        this.d = criterionconditionvalue;
        this.e = criterionconditionvalue1;
        this.f = acriterionconditionenchantments;
        this.g = potionregistry;
        this.h = criterionconditionnbt;
    }

    public boolean a(ItemStack itemstack) {
        if (this.b != null && itemstack.getItem() != this.b) {
            return false;
        } else if (this.c != null && itemstack.getData() != this.c.intValue()) {
            return false;
        } else if (!this.d.a((float) itemstack.getCount())) {
            return false;
        } else if (this.e != CriterionConditionValue.a && !itemstack.f()) {
            return false;
        } else if (!this.e.a((float) (itemstack.k() - itemstack.i()))) {
            return false;
        } else if (!this.h.a(itemstack)) {
            return false;
        } else {
            Map map = EnchantmentManager.a(itemstack);

            for (int i = 0; i < this.f.length; ++i) {
                if (!this.f[i].a(map)) {
                    return false;
                }
            }

            PotionRegistry potionregistry = PotionUtil.d(itemstack);

            if (this.g != null && this.g != potionregistry) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static CriterionConditionItem a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "item");
            CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject.get("count"));
            CriterionConditionValue criterionconditionvalue1 = CriterionConditionValue.a(jsonobject.get("durability"));
            Integer integer = jsonobject.has("data") ? Integer.valueOf(ChatDeserializer.n(jsonobject, "data")) : null;
            CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.a(jsonobject.get("nbt"));
            Item item = null;

            if (jsonobject.has("item")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "item"));

                item = (Item) Item.REGISTRY.get(minecraftkey);
                if (item == null) {
                    throw new JsonSyntaxException("Unknown item id \'" + minecraftkey + "\'");
                }
            }

            CriterionConditionEnchantments[] acriterionconditionenchantments = CriterionConditionEnchantments.b(jsonobject.get("enchantments"));
            PotionRegistry potionregistry = null;

            if (jsonobject.has("potion")) {
                MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.h(jsonobject, "potion"));

                if (!PotionRegistry.a.d(minecraftkey1)) {
                    throw new JsonSyntaxException("Unknown potion \'" + minecraftkey1 + "\'");
                }

                potionregistry = (PotionRegistry) PotionRegistry.a.get(minecraftkey1);
            }

            return new CriterionConditionItem(item, integer, criterionconditionvalue, criterionconditionvalue1, acriterionconditionenchantments, potionregistry, criterionconditionnbt);
        } else {
            return CriterionConditionItem.a;
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
}
