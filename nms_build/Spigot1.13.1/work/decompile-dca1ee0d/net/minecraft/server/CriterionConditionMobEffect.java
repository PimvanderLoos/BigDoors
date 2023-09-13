package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class CriterionConditionMobEffect {

    public static final CriterionConditionMobEffect a = new CriterionConditionMobEffect(Collections.emptyMap());
    private final Map<MobEffectList, CriterionConditionMobEffect.a> b;

    public CriterionConditionMobEffect(Map<MobEffectList, CriterionConditionMobEffect.a> map) {
        this.b = map;
    }

    public static CriterionConditionMobEffect a() {
        return new CriterionConditionMobEffect(Maps.newHashMap());
    }

    public CriterionConditionMobEffect a(MobEffectList mobeffectlist) {
        this.b.put(mobeffectlist, new CriterionConditionMobEffect.a());
        return this;
    }

    public boolean a(Entity entity) {
        return this == CriterionConditionMobEffect.a ? true : (entity instanceof EntityLiving ? this.a(((EntityLiving) entity).co()) : false);
    }

    public boolean a(EntityLiving entityliving) {
        return this == CriterionConditionMobEffect.a ? true : this.a(entityliving.co());
    }

    public boolean a(Map<MobEffectList, MobEffect> map) {
        if (this == CriterionConditionMobEffect.a) {
            return true;
        } else {
            Iterator iterator = this.b.entrySet().iterator();

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
            HashMap hashmap = Maps.newHashMap();
            Iterator iterator = jsonobject.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                MinecraftKey minecraftkey = new MinecraftKey((String) entry.getKey());
                MobEffectList mobeffectlist = (MobEffectList) IRegistry.MOB_EFFECT.get(minecraftkey);

                if (mobeffectlist == null) {
                    throw new JsonSyntaxException("Unknown effect \'" + minecraftkey + "\'");
                }

                CriterionConditionMobEffect.a criterionconditionmobeffect_a = CriterionConditionMobEffect.a.a(ChatDeserializer.m((JsonElement) entry.getValue(), (String) entry.getKey()));

                hashmap.put(mobeffectlist, criterionconditionmobeffect_a);
            }

            return new CriterionConditionMobEffect(hashmap);
        } else {
            return CriterionConditionMobEffect.a;
        }
    }

    public JsonElement b() {
        if (this == CriterionConditionMobEffect.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();
            Iterator iterator = this.b.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                jsonobject.add(IRegistry.MOB_EFFECT.getKey(entry.getKey()).toString(), ((CriterionConditionMobEffect.a) entry.getValue()).a());
            }

            return jsonobject;
        }
    }

    public static class a {

        private final CriterionConditionValue.d a;
        private final CriterionConditionValue.d b;
        @Nullable
        private final Boolean c;
        @Nullable
        private final Boolean d;

        public a(CriterionConditionValue.d criterionconditionvalue_d, CriterionConditionValue.d criterionconditionvalue_d1, @Nullable Boolean obool, @Nullable Boolean obool1) {
            this.a = criterionconditionvalue_d;
            this.b = criterionconditionvalue_d1;
            this.c = obool;
            this.d = obool1;
        }

        public a() {
            this(CriterionConditionValue.d.e, CriterionConditionValue.d.e, (Boolean) null, (Boolean) null);
        }

        public boolean a(@Nullable MobEffect mobeffect) {
            return mobeffect == null ? false : (!this.a.d(mobeffect.getAmplifier()) ? false : (!this.b.d(mobeffect.getDuration()) ? false : (this.c != null && this.c.booleanValue() != mobeffect.isAmbient() ? false : this.d == null || this.d.booleanValue() == mobeffect.isShowParticles())));
        }

        public JsonElement a() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("amplifier", this.a.d());
            jsonobject.add("duration", this.b.d());
            jsonobject.addProperty("ambient", this.c);
            jsonobject.addProperty("visible", this.d);
            return jsonobject;
        }

        public static CriterionConditionMobEffect.a a(JsonObject jsonobject) {
            CriterionConditionValue.d criterionconditionvalue_d = CriterionConditionValue.d.a(jsonobject.get("amplifier"));
            CriterionConditionValue.d criterionconditionvalue_d1 = CriterionConditionValue.d.a(jsonobject.get("duration"));
            Boolean obool = jsonobject.has("ambient") ? Boolean.valueOf(ChatDeserializer.j(jsonobject, "ambient")) : null;
            Boolean obool1 = jsonobject.has("visible") ? Boolean.valueOf(ChatDeserializer.j(jsonobject, "visible")) : null;

            return new CriterionConditionMobEffect.a(criterionconditionvalue_d, criterionconditionvalue_d1, obool, obool1);
        }
    }
}
