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

public class CriterionTriggerConstructBeacon implements CriterionTrigger<CriterionTriggerConstructBeacon.b> {

    private static final MinecraftKey a = new MinecraftKey("construct_beacon");
    private final Map<AdvancementDataPlayer, CriterionTriggerConstructBeacon.a> b = Maps.newHashMap();

    public CriterionTriggerConstructBeacon() {}

    public MinecraftKey a() {
        return CriterionTriggerConstructBeacon.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerConstructBeacon.b> criteriontrigger_a) {
        CriterionTriggerConstructBeacon.a criteriontriggerconstructbeacon_a = (CriterionTriggerConstructBeacon.a) this.b.get(advancementdataplayer);

        if (criteriontriggerconstructbeacon_a == null) {
            criteriontriggerconstructbeacon_a = new CriterionTriggerConstructBeacon.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggerconstructbeacon_a);
        }

        criteriontriggerconstructbeacon_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerConstructBeacon.b> criteriontrigger_a) {
        CriterionTriggerConstructBeacon.a criteriontriggerconstructbeacon_a = (CriterionTriggerConstructBeacon.a) this.b.get(advancementdataplayer);

        if (criteriontriggerconstructbeacon_a != null) {
            criteriontriggerconstructbeacon_a.b(criteriontrigger_a);
            if (criteriontriggerconstructbeacon_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerConstructBeacon.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject.get("level"));

        return new CriterionTriggerConstructBeacon.b(criterionconditionvalue);
    }

    public void a(EntityPlayer entityplayer, TileEntityBeacon tileentitybeacon) {
        CriterionTriggerConstructBeacon.a criteriontriggerconstructbeacon_a = (CriterionTriggerConstructBeacon.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggerconstructbeacon_a != null) {
            criteriontriggerconstructbeacon_a.a(tileentitybeacon);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerConstructBeacon.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerConstructBeacon.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerConstructBeacon.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(TileEntityBeacon tileentitybeacon) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerConstructBeacon.b) criteriontrigger_a.a()).a(tileentitybeacon)) {
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

        private final CriterionConditionValue a;

        public b(CriterionConditionValue criterionconditionvalue) {
            super(CriterionTriggerConstructBeacon.a);
            this.a = criterionconditionvalue;
        }

        public boolean a(TileEntityBeacon tileentitybeacon) {
            return this.a.a((float) tileentitybeacon.s());
        }
    }
}
