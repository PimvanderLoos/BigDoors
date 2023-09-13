package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;

public class CriterionConditionEnchantments {

    public static final CriterionConditionEnchantments a = new CriterionConditionEnchantments();
    private final Enchantment b;
    private final CriterionConditionValue c;

    public CriterionConditionEnchantments() {
        this.b = null;
        this.c = CriterionConditionValue.a;
    }

    public CriterionConditionEnchantments(@Nullable Enchantment enchantment, CriterionConditionValue criterionconditionvalue) {
        this.b = enchantment;
        this.c = criterionconditionvalue;
    }

    public boolean a(Map<Enchantment, Integer> map) {
        if (this.b != null) {
            if (!map.containsKey(this.b)) {
                return false;
            }

            int i = ((Integer) map.get(this.b)).intValue();

            if (this.c != null && !this.c.a((float) i)) {
                return false;
            }
        } else if (this.c != null) {
            Iterator iterator = map.values().iterator();

            Integer integer;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                integer = (Integer) iterator.next();
            } while (!this.c.a((float) integer.intValue()));

            return true;
        }

        return true;
    }

    public static CriterionConditionEnchantments a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "enchantment");
            Enchantment enchantment = null;

            if (jsonobject.has("enchantment")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "enchantment"));

                enchantment = (Enchantment) Enchantment.enchantments.get(minecraftkey);
                if (enchantment == null) {
                    throw new JsonSyntaxException("Unknown enchantment \'" + minecraftkey + "\'");
                }
            }

            CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject.get("levels"));

            return new CriterionConditionEnchantments(enchantment, criterionconditionvalue);
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
            return new CriterionConditionEnchantments[0];
        }
    }
}
