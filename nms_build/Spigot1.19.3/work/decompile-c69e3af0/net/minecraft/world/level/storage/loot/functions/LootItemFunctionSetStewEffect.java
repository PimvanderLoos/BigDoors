package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemSuspiciousStew;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootItemFunctionSetStewEffect extends LootItemFunctionConditional {

    final Map<MobEffectList, NumberProvider> effectDurationMap;

    LootItemFunctionSetStewEffect(LootItemCondition[] alootitemcondition, Map<MobEffectList, NumberProvider> map) {
        super(alootitemcondition);
        this.effectDurationMap = ImmutableMap.copyOf(map);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_STEW_EFFECT;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return (Set) this.effectDurationMap.values().stream().flatMap((numberprovider) -> {
            return numberprovider.getReferencedContextParams().stream();
        }).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.is(Items.SUSPICIOUS_STEW) && !this.effectDurationMap.isEmpty()) {
            RandomSource randomsource = loottableinfo.getRandom();
            int i = randomsource.nextInt(this.effectDurationMap.size());
            Entry<MobEffectList, NumberProvider> entry = (Entry) Iterables.get(this.effectDurationMap.entrySet(), i);
            MobEffectList mobeffectlist = (MobEffectList) entry.getKey();
            int j = ((NumberProvider) entry.getValue()).getInt(loottableinfo);

            if (!mobeffectlist.isInstantenous()) {
                j *= 20;
            }

            ItemSuspiciousStew.saveMobEffect(itemstack, mobeffectlist, j);
            return itemstack;
        } else {
            return itemstack;
        }
    }

    public static LootItemFunctionSetStewEffect.a stewEffect() {
        return new LootItemFunctionSetStewEffect.a();
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionSetStewEffect.a> {

        private final Map<MobEffectList, NumberProvider> effectDurationMap = Maps.newLinkedHashMap();

        public a() {}

        @Override
        protected LootItemFunctionSetStewEffect.a getThis() {
            return this;
        }

        public LootItemFunctionSetStewEffect.a withEffect(MobEffectList mobeffectlist, NumberProvider numberprovider) {
            this.effectDurationMap.put(mobeffectlist, numberprovider);
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootItemFunctionSetStewEffect(this.getConditions(), this.effectDurationMap);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionSetStewEffect> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionSetStewEffect lootitemfunctionsetsteweffect, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetsteweffect, jsonserializationcontext);
            if (!lootitemfunctionsetsteweffect.effectDurationMap.isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = lootitemfunctionsetsteweffect.effectDurationMap.keySet().iterator();

                while (iterator.hasNext()) {
                    MobEffectList mobeffectlist = (MobEffectList) iterator.next();
                    JsonObject jsonobject1 = new JsonObject();
                    MinecraftKey minecraftkey = BuiltInRegistries.MOB_EFFECT.getKey(mobeffectlist);

                    if (minecraftkey == null) {
                        throw new IllegalArgumentException("Don't know how to serialize mob effect " + mobeffectlist);
                    }

                    jsonobject1.add("type", new JsonPrimitive(minecraftkey.toString()));
                    jsonobject1.add("duration", jsonserializationcontext.serialize(lootitemfunctionsetsteweffect.effectDurationMap.get(mobeffectlist)));
                    jsonarray.add(jsonobject1);
                }

                jsonobject.add("effects", jsonarray);
            }

        }

        @Override
        public LootItemFunctionSetStewEffect deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            Map<MobEffectList, NumberProvider> map = Maps.newLinkedHashMap();

            if (jsonobject.has("effects")) {
                JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "effects");
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement = (JsonElement) iterator.next();
                    String s = ChatDeserializer.getAsString(jsonelement.getAsJsonObject(), "type");
                    MobEffectList mobeffectlist = (MobEffectList) BuiltInRegistries.MOB_EFFECT.getOptional(new MinecraftKey(s)).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown mob effect '" + s + "'");
                    });
                    NumberProvider numberprovider = (NumberProvider) ChatDeserializer.getAsObject(jsonelement.getAsJsonObject(), "duration", jsondeserializationcontext, NumberProvider.class);

                    map.put(mobeffectlist, numberprovider);
                }
            }

            return new LootItemFunctionSetStewEffect(alootitemcondition, map);
        }
    }
}
