package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
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

    public boolean a(Entity entity) {
        return this == CriterionConditionMobEffect.a ? true : (entity instanceof EntityLiving ? this.a(((EntityLiving) entity).cb()) : false);
    }

    public boolean a(EntityLiving entityliving) {
        return this == CriterionConditionMobEffect.a ? true : this.a(entityliving.cb());
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
                MobEffectList mobeffectlist = (MobEffectList) MobEffectList.REGISTRY.get(minecraftkey);

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

    public static class a {

        private final CriterionConditionValue a;
        private final CriterionConditionValue b;
        @Nullable
        private final Boolean c;
        @Nullable
        private final Boolean d;

        public a(CriterionConditionValue criterionconditionvalue, CriterionConditionValue criterionconditionvalue1, @Nullable Boolean obool, @Nullable Boolean obool1) {
            this.a = criterionconditionvalue;
            this.b = criterionconditionvalue1;
            this.c = obool;
            this.d = obool1;
        }

        public boolean a(@Nullable MobEffect mobeffect) {
            return mobeffect == null ? false : (!this.a.a((float) mobeffect.getAmplifier()) ? false : (!this.b.a((float) mobeffect.getDuration()) ? false : (this.c != null && this.c.booleanValue() != mobeffect.isAmbient() ? false : this.d == null || this.d.booleanValue() == mobeffect.isShowParticles())));
        }

        public static CriterionConditionMobEffect.a a(JsonObject jsonobject) {
            CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject.get("amplifier"));
            CriterionConditionValue criterionconditionvalue1 = CriterionConditionValue.a(jsonobject.get("duration"));
            Boolean obool = jsonobject.has("ambient") ? Boolean.valueOf(ChatDeserializer.j(jsonobject, "ambient")) : null;
            Boolean obool1 = jsonobject.has("visible") ? Boolean.valueOf(ChatDeserializer.j(jsonobject, "visible")) : null;

            return new CriterionConditionMobEffect.a(criterionconditionvalue, criterionconditionvalue1, obool, obool1);
        }
    }
}
