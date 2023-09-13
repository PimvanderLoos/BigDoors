package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.enchantment.Enchantment;

public class CriterionConditionEnchantments {

    public static final CriterionConditionEnchantments ANY = new CriterionConditionEnchantments();
    public static final CriterionConditionEnchantments[] NONE = new CriterionConditionEnchantments[0];
    @Nullable
    private final Enchantment enchantment;
    private final CriterionConditionValue.IntegerRange level;

    public CriterionConditionEnchantments() {
        this.enchantment = null;
        this.level = CriterionConditionValue.IntegerRange.ANY;
    }

    public CriterionConditionEnchantments(@Nullable Enchantment enchantment, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
        this.enchantment = enchantment;
        this.level = criterionconditionvalue_integerrange;
    }

    public boolean containedIn(Map<Enchantment, Integer> map) {
        if (this.enchantment != null) {
            if (!map.containsKey(this.enchantment)) {
                return false;
            }

            int i = (Integer) map.get(this.enchantment);

            if (this.level != CriterionConditionValue.IntegerRange.ANY && !this.level.matches(i)) {
                return false;
            }
        } else if (this.level != CriterionConditionValue.IntegerRange.ANY) {
            Iterator iterator = map.values().iterator();

            Integer integer;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                integer = (Integer) iterator.next();
            } while (!this.level.matches(integer));

            return true;
        }

        return true;
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionEnchantments.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.enchantment != null) {
                jsonobject.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this.enchantment).toString());
            }

            jsonobject.add("levels", this.level.serializeToJson());
            return jsonobject;
        }
    }

    public static CriterionConditionEnchantments fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "enchantment");
            Enchantment enchantment = null;

            if (jsonobject.has("enchantment")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "enchantment"));

                enchantment = (Enchantment) BuiltInRegistries.ENCHANTMENT.getOptional(minecraftkey).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown enchantment '" + minecraftkey + "'");
                });
            }

            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("levels"));

            return new CriterionConditionEnchantments(enchantment, criterionconditionvalue_integerrange);
        } else {
            return CriterionConditionEnchantments.ANY;
        }
    }

    public static CriterionConditionEnchantments[] fromJsonArray(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonArray jsonarray = ChatDeserializer.convertToJsonArray(jsonelement, "enchantments");
            CriterionConditionEnchantments[] acriterionconditionenchantments = new CriterionConditionEnchantments[jsonarray.size()];

            for (int i = 0; i < acriterionconditionenchantments.length; ++i) {
                acriterionconditionenchantments[i] = fromJson(jsonarray.get(i));
            }

            return acriterionconditionenchantments;
        } else {
            return CriterionConditionEnchantments.NONE;
        }
    }
}
