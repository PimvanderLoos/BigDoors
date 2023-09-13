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

public class CriterionTriggerLocation implements CriterionTrigger<CriterionTriggerLocation.b> {

    private final MinecraftKey a;
    private final Map<AdvancementDataPlayer, CriterionTriggerLocation.a> b = Maps.newHashMap();

    public CriterionTriggerLocation(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    public MinecraftKey a() {
        return this.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerLocation.b> criteriontrigger_a) {
        CriterionTriggerLocation.a criteriontriggerlocation_a = (CriterionTriggerLocation.a) this.b.get(advancementdataplayer);

        if (criteriontriggerlocation_a == null) {
            criteriontriggerlocation_a = new CriterionTriggerLocation.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggerlocation_a);
        }

        criteriontriggerlocation_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerLocation.b> criteriontrigger_a) {
        CriterionTriggerLocation.a criteriontriggerlocation_a = (CriterionTriggerLocation.a) this.b.get(advancementdataplayer);

        if (criteriontriggerlocation_a != null) {
            criteriontriggerlocation_a.b(criteriontrigger_a);
            if (criteriontriggerlocation_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerLocation.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a(jsonobject);

        return new CriterionTriggerLocation.b(this.a, criterionconditionlocation);
    }

    public void a(EntityPlayer entityplayer) {
        CriterionTriggerLocation.a criteriontriggerlocation_a = (CriterionTriggerLocation.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggerlocation_a != null) {
            criteriontriggerlocation_a.a(entityplayer.x(), entityplayer.locX, entityplayer.locY, entityplayer.locZ);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerLocation.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerLocation.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerLocation.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(WorldServer worldserver, double d0, double d1, double d2) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerLocation.b) criteriontrigger_a.a()).a(worldserver, d0, d1, d2)) {
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

        private final CriterionConditionLocation a;

        public b(MinecraftKey minecraftkey, CriterionConditionLocation criterionconditionlocation) {
            super(minecraftkey);
            this.a = criterionconditionlocation;
        }

        public boolean a(WorldServer worldserver, double d0, double d1, double d2) {
            return this.a.a(worldserver, d0, d1, d2);
        }
    }
}
