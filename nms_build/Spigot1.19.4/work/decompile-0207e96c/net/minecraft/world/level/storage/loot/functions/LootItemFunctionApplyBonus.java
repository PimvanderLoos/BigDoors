package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionApplyBonus extends LootItemFunctionConditional {

    static final Map<MinecraftKey, LootItemFunctionApplyBonus.c> FORMULAS = Maps.newHashMap();
    final Enchantment enchantment;
    final LootItemFunctionApplyBonus.b formula;

    LootItemFunctionApplyBonus(LootItemCondition[] alootitemcondition, Enchantment enchantment, LootItemFunctionApplyBonus.b lootitemfunctionapplybonus_b) {
        super(alootitemcondition);
        this.enchantment = enchantment;
        this.formula = lootitemfunctionapplybonus_b;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.APPLY_BONUS;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        ItemStack itemstack1 = (ItemStack) loottableinfo.getParamOrNull(LootContextParameters.TOOL);

        if (itemstack1 != null) {
            int i = EnchantmentManager.getItemEnchantmentLevel(this.enchantment, itemstack1);
            int j = this.formula.calculateNewCount(loottableinfo.getRandom(), itemstack.getCount(), i);

            itemstack.setCount(j);
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> addBonusBinomialDistributionCount(Enchantment enchantment, float f, int i) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionApplyBonus(alootitemcondition, enchantment, new LootItemFunctionApplyBonus.a(i, f));
        });
    }

    public static LootItemFunctionConditional.a<?> addOreBonusCount(Enchantment enchantment) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionApplyBonus(alootitemcondition, enchantment, new LootItemFunctionApplyBonus.d());
        });
    }

    public static LootItemFunctionConditional.a<?> addUniformBonusCount(Enchantment enchantment) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionApplyBonus(alootitemcondition, enchantment, new LootItemFunctionApplyBonus.f(1));
        });
    }

    public static LootItemFunctionConditional.a<?> addUniformBonusCount(Enchantment enchantment, int i) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionApplyBonus(alootitemcondition, enchantment, new LootItemFunctionApplyBonus.f(i));
        });
    }

    static {
        LootItemFunctionApplyBonus.FORMULAS.put(LootItemFunctionApplyBonus.a.TYPE, LootItemFunctionApplyBonus.a::deserialize);
        LootItemFunctionApplyBonus.FORMULAS.put(LootItemFunctionApplyBonus.d.TYPE, LootItemFunctionApplyBonus.d::deserialize);
        LootItemFunctionApplyBonus.FORMULAS.put(LootItemFunctionApplyBonus.f.TYPE, LootItemFunctionApplyBonus.f::deserialize);
    }

    private interface b {

        int calculateNewCount(RandomSource randomsource, int i, int j);

        void serializeParams(JsonObject jsonobject, JsonSerializationContext jsonserializationcontext);

        MinecraftKey getType();
    }

    private static final class f implements LootItemFunctionApplyBonus.b {

        public static final MinecraftKey TYPE = new MinecraftKey("uniform_bonus_count");
        private final int bonusMultiplier;

        public f(int i) {
            this.bonusMultiplier = i;
        }

        @Override
        public int calculateNewCount(RandomSource randomsource, int i, int j) {
            return i + randomsource.nextInt(this.bonusMultiplier * j + 1);
        }

        @Override
        public void serializeParams(JsonObject jsonobject, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("bonusMultiplier", this.bonusMultiplier);
        }

        public static LootItemFunctionApplyBonus.b deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            int i = ChatDeserializer.getAsInt(jsonobject, "bonusMultiplier");

            return new LootItemFunctionApplyBonus.f(i);
        }

        @Override
        public MinecraftKey getType() {
            return LootItemFunctionApplyBonus.f.TYPE;
        }
    }

    private static final class d implements LootItemFunctionApplyBonus.b {

        public static final MinecraftKey TYPE = new MinecraftKey("ore_drops");

        d() {}

        @Override
        public int calculateNewCount(RandomSource randomsource, int i, int j) {
            if (j > 0) {
                int k = randomsource.nextInt(j + 2) - 1;

                if (k < 0) {
                    k = 0;
                }

                return i * (k + 1);
            } else {
                return i;
            }
        }

        @Override
        public void serializeParams(JsonObject jsonobject, JsonSerializationContext jsonserializationcontext) {}

        public static LootItemFunctionApplyBonus.b deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return new LootItemFunctionApplyBonus.d();
        }

        @Override
        public MinecraftKey getType() {
            return LootItemFunctionApplyBonus.d.TYPE;
        }
    }

    private static final class a implements LootItemFunctionApplyBonus.b {

        public static final MinecraftKey TYPE = new MinecraftKey("binomial_with_bonus_count");
        private final int extraRounds;
        private final float probability;

        public a(int i, float f) {
            this.extraRounds = i;
            this.probability = f;
        }

        @Override
        public int calculateNewCount(RandomSource randomsource, int i, int j) {
            for (int k = 0; k < j + this.extraRounds; ++k) {
                if (randomsource.nextFloat() < this.probability) {
                    ++i;
                }
            }

            return i;
        }

        @Override
        public void serializeParams(JsonObject jsonobject, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("extra", this.extraRounds);
            jsonobject.addProperty("probability", this.probability);
        }

        public static LootItemFunctionApplyBonus.b deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            int i = ChatDeserializer.getAsInt(jsonobject, "extra");
            float f = ChatDeserializer.getAsFloat(jsonobject, "probability");

            return new LootItemFunctionApplyBonus.a(i, f);
        }

        @Override
        public MinecraftKey getType() {
            return LootItemFunctionApplyBonus.a.TYPE;
        }
    }

    private interface c {

        LootItemFunctionApplyBonus.b deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext);
    }

    public static class e extends LootItemFunctionConditional.c<LootItemFunctionApplyBonus> {

        public e() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionApplyBonus lootitemfunctionapplybonus, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionapplybonus, jsonserializationcontext);
            jsonobject.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(lootitemfunctionapplybonus.enchantment).toString());
            jsonobject.addProperty("formula", lootitemfunctionapplybonus.formula.getType().toString());
            JsonObject jsonobject1 = new JsonObject();

            lootitemfunctionapplybonus.formula.serializeParams(jsonobject1, jsonserializationcontext);
            if (jsonobject1.size() > 0) {
                jsonobject.add("parameters", jsonobject1);
            }

        }

        @Override
        public LootItemFunctionApplyBonus deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "enchantment"));
            Enchantment enchantment = (Enchantment) BuiltInRegistries.ENCHANTMENT.getOptional(minecraftkey).orElseThrow(() -> {
                return new JsonParseException("Invalid enchantment id: " + minecraftkey);
            });
            MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "formula"));
            LootItemFunctionApplyBonus.c lootitemfunctionapplybonus_c = (LootItemFunctionApplyBonus.c) LootItemFunctionApplyBonus.FORMULAS.get(minecraftkey1);

            if (lootitemfunctionapplybonus_c == null) {
                throw new JsonParseException("Invalid formula id: " + minecraftkey1);
            } else {
                LootItemFunctionApplyBonus.b lootitemfunctionapplybonus_b;

                if (jsonobject.has("parameters")) {
                    lootitemfunctionapplybonus_b = lootitemfunctionapplybonus_c.deserialize(ChatDeserializer.getAsJsonObject(jsonobject, "parameters"), jsondeserializationcontext);
                } else {
                    lootitemfunctionapplybonus_b = lootitemfunctionapplybonus_c.deserialize(new JsonObject(), jsondeserializationcontext);
                }

                return new LootItemFunctionApplyBonus(alootitemcondition, enchantment, lootitemfunctionapplybonus_b);
            }
        }
    }
}
