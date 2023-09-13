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

public class CriterionTriggerLevitation implements CriterionTrigger<CriterionTriggerLevitation.b> {

    private static final MinecraftKey a = new MinecraftKey("levitation");
    private final Map<AdvancementDataPlayer, CriterionTriggerLevitation.a> b = Maps.newHashMap();

    public CriterionTriggerLevitation() {}

    public MinecraftKey a() {
        return CriterionTriggerLevitation.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerLevitation.b> criteriontrigger_a) {
        CriterionTriggerLevitation.a criteriontriggerlevitation_a = (CriterionTriggerLevitation.a) this.b.get(advancementdataplayer);

        if (criteriontriggerlevitation_a == null) {
            criteriontriggerlevitation_a = new CriterionTriggerLevitation.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggerlevitation_a);
        }

        criteriontriggerlevitation_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerLevitation.b> criteriontrigger_a) {
        CriterionTriggerLevitation.a criteriontriggerlevitation_a = (CriterionTriggerLevitation.a) this.b.get(advancementdataplayer);

        if (criteriontriggerlevitation_a != null) {
            criteriontriggerlevitation_a.b(criteriontrigger_a);
            if (criteriontriggerlevitation_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerLevitation.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.a(jsonobject.get("distance"));
        CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject.get("duration"));

        return new CriterionTriggerLevitation.b(criterionconditiondistance, criterionconditionvalue);
    }

    public void a(EntityPlayer entityplayer, Vec3D vec3d, int i) {
        CriterionTriggerLevitation.a criteriontriggerlevitation_a = (CriterionTriggerLevitation.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggerlevitation_a != null) {
            criteriontriggerlevitation_a.a(entityplayer, vec3d, i);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerLevitation.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerLevitation.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerLevitation.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(EntityPlayer entityplayer, Vec3D vec3d, int i) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerLevitation.b) criteriontrigger_a.a()).a(entityplayer, vec3d, i)) {
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

        private final CriterionConditionDistance a;
        private final CriterionConditionValue b;

        public b(CriterionConditionDistance criterionconditiondistance, CriterionConditionValue criterionconditionvalue) {
            super(CriterionTriggerLevitation.a);
            this.a = criterionconditiondistance;
            this.b = criterionconditionvalue;
        }

        public boolean a(EntityPlayer entityplayer, Vec3D vec3d, int i) {
            return !this.a.a(vec3d.x, vec3d.y, vec3d.z, entityplayer.locX, entityplayer.locY, entityplayer.locZ) ? false : this.b.a((float) i);
        }
    }
}
