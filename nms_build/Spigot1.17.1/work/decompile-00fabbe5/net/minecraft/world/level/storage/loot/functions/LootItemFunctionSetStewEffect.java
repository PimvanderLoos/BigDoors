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
import java.util.Random;
import java.util.Set;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
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
    public LootItemFunctionType a() {
        return LootItemFunctions.SET_STEW_EFFECT;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return (Set) this.effectDurationMap.values().stream().flatMap((numberprovider) -> {
            return numberprovider.b().stream();
        }).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.a(Items.SUSPICIOUS_STEW) && !this.effectDurationMap.isEmpty()) {
            Random random = loottableinfo.a();
            int i = random.nextInt(this.effectDurationMap.size());
            Entry<MobEffectList, NumberProvider> entry = (Entry) Iterables.get(this.effectDurationMap.entrySet(), i);
            MobEffectList mobeffectlist = (MobEffectList) entry.getKey();
            int j = ((NumberProvider) entry.getValue()).a(loottableinfo);

            if (!mobeffectlist.isInstant()) {
                j *= 20;
            }

            ItemSuspiciousStew.a(itemstack, mobeffectlist, j);
            return itemstack;
        } else {
            return itemstack;
        }
    }

    public static LootItemFunctionSetStewEffect.a c() {
        return new LootItemFunctionSetStewEffect.a();
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionSetStewEffect.a> {

        private final Map<MobEffectList, NumberProvider> effectDurationMap = Maps.newHashMap();

        public a() {}

        @Override
        protected LootItemFunctionSetStewEffect.a d() {
            return this;
        }

        public LootItemFunctionSetStewEffect.a a(MobEffectList mobeffectlist, NumberProvider numberprovider) {
            this.effectDurationMap.put(mobeffectlist, numberprovider);
            return this;
        }

        @Override
        public LootItemFunction b() {
            return new LootItemFunctionSetStewEffect(this.g(), this.effectDurationMap);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionSetStewEffect> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetStewEffect lootitemfunctionsetsteweffect, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetsteweffect, jsonserializationcontext);
            if (!lootitemfunctionsetsteweffect.effectDurationMap.isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = lootitemfunctionsetsteweffect.effectDurationMap.keySet().iterator();

                while (iterator.hasNext()) {
                    MobEffectList mobeffectlist = (MobEffectList) iterator.next();
                    JsonObject jsonobject1 = new JsonObject();
                    MinecraftKey minecraftkey = IRegistry.MOB_EFFECT.getKey(mobeffectlist);

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
        public LootItemFunctionSetStewEffect b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            Map<MobEffectList, NumberProvider> map = Maps.newHashMap();

            if (jsonobject.has("effects")) {
                JsonArray jsonarray = ChatDeserializer.u(jsonobject, "effects");
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement = (JsonElement) iterator.next();
                    String s = ChatDeserializer.h(jsonelement.getAsJsonObject(), "type");
                    MobEffectList mobeffectlist = (MobEffectList) IRegistry.MOB_EFFECT.getOptional(new MinecraftKey(s)).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown mob effect '" + s + "'");
                    });
                    NumberProvider numberprovider = (NumberProvider) ChatDeserializer.a(jsonelement.getAsJsonObject(), "duration", jsondeserializationcontext, NumberProvider.class);

                    map.put(mobeffectlist, numberprovider);
                }
            }

            return new LootItemFunctionSetStewEffect(alootitemcondition, map);
        }
    }
}
