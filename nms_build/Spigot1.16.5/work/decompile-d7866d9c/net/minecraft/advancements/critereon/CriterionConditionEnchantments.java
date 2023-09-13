package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.enchantment.Enchantment;

public class CriterionConditionEnchantments {

    public static final CriterionConditionEnchantments a = new CriterionConditionEnchantments();
    public static final CriterionConditionEnchantments[] b = new CriterionConditionEnchantments[0];
    private final Enchantment c;
    private final CriterionConditionValue.IntegerRange d;

    public CriterionConditionEnchantments() {
        this.c = null;
        this.d = CriterionConditionValue.IntegerRange.e;
    }

    public CriterionConditionEnchantments(@Nullable Enchantment enchantment, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
        this.c = enchantment;
        this.d = criterionconditionvalue_integerrange;
    }

    public boolean a(Map<Enchantment, Integer> map) {
        if (this.c != null) {
            if (!map.containsKey(this.c)) {
                return false;
            }

            int i = (Integer) map.get(this.c);

            if (this.d != null && !this.d.d(i)) {
                return false;
            }
        } else if (this.d != null) {
            Iterator iterator = map.values().iterator();

            Integer integer;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                integer = (Integer) iterator.next();
            } while (!this.d.d(integer));

            return true;
        }

        return true;
    }

    public JsonElement a() {
        if (this == CriterionConditionEnchantments.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.c != null) {
                jsonobject.addProperty("enchantment", IRegistry.ENCHANTMENT.getKey(this.c).toString());
            }

            jsonobject.add("levels", this.d.d());
            return jsonobject;
        }
    }

    public static CriterionConditionEnchantments a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "enchantment");
            Enchantment enchantment = null;

            if (jsonobject.has("enchantment")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "enchantment"));

                enchantment = (Enchantment) IRegistry.ENCHANTMENT.getOptional(minecraftkey).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown enchantment '" + minecraftkey + "'");
                });
            }

            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("levels"));

            return new CriterionConditionEnchantments(enchantment, criterionconditionvalue_integerrange);
        } else {
            return CriterionConditionEnchantments.a;
        }
    }

    public static CriterionConditionEnchantments[] b(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonArray jsonarray = ChatDeserializer.n(jsonelement, "enchantments");
            CriterionConditionEnchantments[] acriterionconditionenchantments = new CriterionConditionEnchantments[jsonarray.size()];

            for (int i = 0; i < acriterionconditionenchantments.length; ++i) {
                acriterionconditionenchantments[i] = a(jsonarray.get(i));
            }

            return acriterionconditionenchantments;
        } else {
            return CriterionConditionEnchantments.b;
        }
    }
}
