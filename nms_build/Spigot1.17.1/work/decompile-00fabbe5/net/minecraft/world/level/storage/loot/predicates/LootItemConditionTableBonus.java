package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class LootItemConditionTableBonus implements LootItemCondition {

    final Enchantment enchantment;
    final float[] values;

    LootItemConditionTableBonus(Enchantment enchantment, float[] afloat) {
        this.enchantment = enchantment;
        this.values = afloat;
    }

    @Override
    public LootItemConditionType a() {
        return LootItemConditions.TABLE_BONUS;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    public boolean test(LootTableInfo loottableinfo) {
        ItemStack itemstack = (ItemStack) loottableinfo.getContextParameter(LootContextParameters.TOOL);
        int i = itemstack != null ? EnchantmentManager.getEnchantmentLevel(this.enchantment, itemstack) : 0;
        float f = this.values[Math.min(i, this.values.length - 1)];

        return loottableinfo.a().nextFloat() < f;
    }

    public static LootItemCondition.a a(Enchantment enchantment, float... afloat) {
        return () -> {
            return new LootItemConditionTableBonus(enchantment, afloat);
        };
    }

    public static class a implements LootSerializer<LootItemConditionTableBonus> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionTableBonus lootitemconditiontablebonus, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("enchantment", IRegistry.ENCHANTMENT.getKey(lootitemconditiontablebonus.enchantment).toString());
            jsonobject.add("chances", jsonserializationcontext.serialize(lootitemconditiontablebonus.values));
        }

        @Override
        public LootItemConditionTableBonus a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "enchantment"));
            Enchantment enchantment = (Enchantment) IRegistry.ENCHANTMENT.getOptional(minecraftkey).orElseThrow(() -> {
                return new JsonParseException("Invalid enchantment id: " + minecraftkey);
            });
            float[] afloat = (float[]) ChatDeserializer.a(jsonobject, "chances", jsondeserializationcontext, float[].class);

            return new LootItemConditionTableBonus(enchantment, afloat);
        }
    }
}
