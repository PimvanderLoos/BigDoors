package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;

public class CriterionConditionMobEffect {

    public static final CriterionConditionMobEffect ANY = new CriterionConditionMobEffect(Collections.emptyMap());
    private final Map<MobEffectList, CriterionConditionMobEffect.a> effects;

    public CriterionConditionMobEffect(Map<MobEffectList, CriterionConditionMobEffect.a> map) {
        this.effects = map;
    }

    public static CriterionConditionMobEffect a() {
        return new CriterionConditionMobEffect(Maps.newLinkedHashMap());
    }

    public CriterionConditionMobEffect a(MobEffectList mobeffectlist) {
        this.effects.put(mobeffectlist, new CriterionConditionMobEffect.a());
        return this;
    }

    public CriterionConditionMobEffect a(MobEffectList mobeffectlist, CriterionConditionMobEffect.a criterionconditionmobeffect_a) {
        this.effects.put(mobeffectlist, criterionconditionmobeffect_a);
        return this;
    }

    public boolean a(Entity entity) {
        return this == CriterionConditionMobEffect.ANY ? true : (entity instanceof EntityLiving ? this.a(((EntityLiving) entity).dS()) : false);
    }

    public boolean a(EntityLiving entityliving) {
        return this == CriterionConditionMobEffect.ANY ? true : this.a(entityliving.dS());
    }

    public boolean a(Map<MobEffectList, MobEffect> map) {
        if (this == CriterionConditionMobEffect.ANY) {
            return true;
        } else {
            Iterator iterator = this.effects.entrySet().iterator();

            Entry entry;
            MobEffect mobeffect;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                entry = (Entry) iterator.next();
                mobeffect = (MobEffect) map.get(entry.getKey());
            } while (((CriterionConditionMobEffect.a) entry.getValue()).a(mobeffect));

            return false;
        }
    }

    public static CriterionConditionMobEffect a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "effects");
            Map<MobEffectList, CriterionConditionMobEffect.a> map = Maps.newLinkedHashMap();
            Iterator iterator = jsonobject.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, JsonElement> entry = (Entry) iterator.next();
                MinecraftKey minecraftkey = new MinecraftKey((String) entry.getKey());
                MobEffectList mobeffectlist = (MobEffectList) IRegistry.MOB_EFFECT.getOptional(minecraftkey).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown effect '" + minecraftkey + "'");
                });
                CriterionConditionMobEffect.a criterionconditionmobeffect_a = CriterionConditionMobEffect.a.a(ChatDeserializer.m((JsonElement) entry.getValue(), (String) entry.getKey()));

                map.put(mobeffectlist, criterionconditionmobeffect_a);
            }

            return new CriterionConditionMobEffect(map);
        } else {
            return CriterionConditionMobEffect.ANY;
        }
    }

    public JsonElement b() {
        if (this == CriterionConditionMobEffect.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();
            Iterator iterator = this.effects.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<MobEffectList, CriterionConditionMobEffect.a> entry = (Entry) iterator.next();

                jsonobject.add(IRegistry.MOB_EFFECT.getKey((MobEffectList) entry.getKey()).toString(), ((CriterionConditionMobEffect.a) entry.getValue()).a());
            }

            return jsonobject;
        }
    }

    public static class a {

        private final CriterionConditionValue.IntegerRange amplifier;
        private final CriterionConditionValue.IntegerRange duration;
        @Nullable
        private final Boolean ambient;
        @Nullable
        private final Boolean visible;

        public a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1, @Nullable Boolean obool, @Nullable Boolean obool1) {
            this.amplifier = criterionconditionvalue_integerrange;
            this.duration = criterionconditionvalue_integerrange1;
            this.ambient = obool;
            this.visible = obool1;
        }

        public a() {
            this(CriterionConditionValue.IntegerRange.ANY, CriterionConditionValue.IntegerRange.ANY, (Boolean) null, (Boolean) null);
        }

        public boolean a(@Nullable MobEffect mobeffect) {
            return mobeffect == null ? false : (!this.amplifier.d(mobeffect.getAmplifier()) ? false : (!this.duration.d(mobeffect.getDuration()) ? false : (this.ambient != null && this.ambient != mobeffect.isAmbient() ? false : this.visible == null || this.visible == mobeffect.isShowParticles())));
        }

        public JsonElement a() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("amplifier", this.amplifier.d());
            jsonobject.add("duration", this.duration.d());
            jsonobject.addProperty("ambient", this.ambient);
            jsonobject.addProperty("visible", this.visible);
            return jsonobject;
        }

        public static CriterionConditionMobEffect.a a(JsonObject jsonobject) {
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("amplifier"));
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.a(jsonobject.get("duration"));
            Boolean obool = jsonobject.has("ambient") ? ChatDeserializer.j(jsonobject, "ambient") : null;
            Boolean obool1 = jsonobject.has("visible") ? ChatDeserializer.j(jsonobject, "visible") : null;

            return new CriterionConditionMobEffect.a(criterionconditionvalue_integerrange, criterionconditionvalue_integerrange1, obool, obool1);
        }
    }
}
