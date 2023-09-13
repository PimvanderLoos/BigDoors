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
import javax.annotation.Nullable;

public class CriterionTriggerChangedDimension implements CriterionTrigger<CriterionTriggerChangedDimension.b> {

    private static final MinecraftKey a = new MinecraftKey("changed_dimension");
    private final Map<AdvancementDataPlayer, CriterionTriggerChangedDimension.a> b = Maps.newHashMap();

    public CriterionTriggerChangedDimension() {}

    public MinecraftKey a() {
        return CriterionTriggerChangedDimension.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerChangedDimension.b> criteriontrigger_a) {
        CriterionTriggerChangedDimension.a criteriontriggerchangeddimension_a = (CriterionTriggerChangedDimension.a) this.b.get(advancementdataplayer);

        if (criteriontriggerchangeddimension_a == null) {
            criteriontriggerchangeddimension_a = new CriterionTriggerChangedDimension.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggerchangeddimension_a);
        }

        criteriontriggerchangeddimension_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerChangedDimension.b> criteriontrigger_a) {
        CriterionTriggerChangedDimension.a criteriontriggerchangeddimension_a = (CriterionTriggerChangedDimension.a) this.b.get(advancementdataplayer);

        if (criteriontriggerchangeddimension_a != null) {
            criteriontriggerchangeddimension_a.b(criteriontrigger_a);
            if (criteriontriggerchangeddimension_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerChangedDimension.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        DimensionManager dimensionmanager = jsonobject.has("from") ? DimensionManager.a(ChatDeserializer.h(jsonobject, "from")) : null;
        DimensionManager dimensionmanager1 = jsonobject.has("to") ? DimensionManager.a(ChatDeserializer.h(jsonobject, "to")) : null;

        return new CriterionTriggerChangedDimension.b(dimensionmanager, dimensionmanager1);
    }

    public void a(EntityPlayer entityplayer, DimensionManager dimensionmanager, DimensionManager dimensionmanager1) {
        CriterionTriggerChangedDimension.a criteriontriggerchangeddimension_a = (CriterionTriggerChangedDimension.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggerchangeddimension_a != null) {
            criteriontriggerchangeddimension_a.a(dimensionmanager, dimensionmanager1);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerChangedDimension.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerChangedDimension.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerChangedDimension.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(DimensionManager dimensionmanager, DimensionManager dimensionmanager1) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerChangedDimension.b) criteriontrigger_a.a()).a(dimensionmanager, dimensionmanager1)) {
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

        @Nullable
        private final DimensionManager a;
        @Nullable
        private final DimensionManager b;

        public b(@Nullable DimensionManager dimensionmanager, @Nullable DimensionManager dimensionmanager1) {
            super(CriterionTriggerChangedDimension.a);
            this.a = dimensionmanager;
            this.b = dimensionmanager1;
        }

        public boolean a(DimensionManager dimensionmanager, DimensionManager dimensionmanager1) {
            return this.a != null && this.a != dimensionmanager ? false : this.b == null || this.b == dimensionmanager1;
        }
    }
}
