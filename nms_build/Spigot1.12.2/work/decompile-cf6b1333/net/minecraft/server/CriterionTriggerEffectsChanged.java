package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CriterionTriggerEffectsChanged implements CriterionTrigger<CriterionTriggerEffectsChanged.b> {

    private static final MinecraftKey a = new MinecraftKey("effects_changed");
    private final Map<AdvancementDataPlayer, CriterionTriggerEffectsChanged.a> b = Maps.newHashMap();

    public CriterionTriggerEffectsChanged() {}

    public MinecraftKey a() {
        return CriterionTriggerEffectsChanged.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerEffectsChanged.b> criteriontrigger_a) {
        CriterionTriggerEffectsChanged.a criteriontriggereffectschanged_a = (CriterionTriggerEffectsChanged.a) this.b.get(advancementdataplayer);

        if (criteriontriggereffectschanged_a == null) {
            criteriontriggereffectschanged_a = new CriterionTriggerEffectsChanged.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggereffectschanged_a);
        }

        criteriontriggereffectschanged_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerEffectsChanged.b> criteriontrigger_a) {
        CriterionTriggerEffectsChanged.a criteriontriggereffectschanged_a = (CriterionTriggerEffectsChanged.a) this.b.get(advancementdataplayer);

        if (criteriontriggereffectschanged_a != null) {
            criteriontriggereffectschanged_a.b(criteriontrigger_a);
            if (criteriontriggereffectschanged_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerEffectsChanged.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionMobEffect criterionconditionmobeffect = CriterionConditionMobEffect.a(jsonobject.get("effects"));

        return new CriterionTriggerEffectsChanged.b(criterionconditionmobeffect);
    }

    public void a(EntityPlayer entityplayer) {
        CriterionTriggerEffectsChanged.a criteriontriggereffectschanged_a = (CriterionTriggerEffectsChanged.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggereffectschanged_a != null) {
            criteriontriggereffectschanged_a.a(entityplayer);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerEffectsChanged.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerEffectsChanged.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerEffectsChanged.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(EntityPlayer entityplayer) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerEffectsChanged.b) criteriontrigger_a.a()).a(entityplayer)) {
                    if (arraylist == null) {
                        arraylist = Lists.newArrayList();
                    }

                    arraylist.add(criteriontrigger_a);
                }
            }

            if (arraylist != null) {
                iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                    criteriontrigger_a.a(this.a);
                }
            }

        }
    }

    public static class b extends CriterionInstanceAbstract {

        private final CriterionConditionMobEffect a;

        public b(CriterionConditionMobEffect criterionconditionmobeffect) {
            super(CriterionTriggerEffectsChanged.a);
            this.a = criterionconditionmobeffect;
        }

        public boolean a(EntityPlayer entityplayer) {
            return this.a.a((EntityLiving) entityplayer);
        }
    }
}
