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

public class CriterionTriggerItemDurabilityChanged implements CriterionTrigger<CriterionTriggerItemDurabilityChanged.b> {

    private static final MinecraftKey a = new MinecraftKey("item_durability_changed");
    private final Map<AdvancementDataPlayer, CriterionTriggerItemDurabilityChanged.a> b = Maps.newHashMap();

    public CriterionTriggerItemDurabilityChanged() {}

    public MinecraftKey a() {
        return CriterionTriggerItemDurabilityChanged.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerItemDurabilityChanged.b> criteriontrigger_a) {
        CriterionTriggerItemDurabilityChanged.a criteriontriggeritemdurabilitychanged_a = (CriterionTriggerItemDurabilityChanged.a) this.b.get(advancementdataplayer);

        if (criteriontriggeritemdurabilitychanged_a == null) {
            criteriontriggeritemdurabilitychanged_a = new CriterionTriggerItemDurabilityChanged.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggeritemdurabilitychanged_a);
        }

        criteriontriggeritemdurabilitychanged_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerItemDurabilityChanged.b> criteriontrigger_a) {
        CriterionTriggerItemDurabilityChanged.a criteriontriggeritemdurabilitychanged_a = (CriterionTriggerItemDurabilityChanged.a) this.b.get(advancementdataplayer);

        if (criteriontriggeritemdurabilitychanged_a != null) {
            criteriontriggeritemdurabilitychanged_a.b(criteriontrigger_a);
            if (criteriontriggeritemdurabilitychanged_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerItemDurabilityChanged.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject.get("durability"));
        CriterionConditionValue criterionconditionvalue1 = CriterionConditionValue.a(jsonobject.get("delta"));

        return new CriterionTriggerItemDurabilityChanged.b(criterionconditionitem, criterionconditionvalue, criterionconditionvalue1);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, int i) {
        CriterionTriggerItemDurabilityChanged.a criteriontriggeritemdurabilitychanged_a = (CriterionTriggerItemDurabilityChanged.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggeritemdurabilitychanged_a != null) {
            criteriontriggeritemdurabilitychanged_a.a(itemstack, i);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerItemDurabilityChanged.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerItemDurabilityChanged.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerItemDurabilityChanged.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(ItemStack itemstack, int i) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerItemDurabilityChanged.b) criteriontrigger_a.a()).a(itemstack, i)) {
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

        private final CriterionConditionItem a;
        private final CriterionConditionValue b;
        private final CriterionConditionValue c;

        public b(CriterionConditionItem criterionconditionitem, CriterionConditionValue criterionconditionvalue, CriterionConditionValue criterionconditionvalue1) {
            super(CriterionTriggerItemDurabilityChanged.a);
            this.a = criterionconditionitem;
            this.b = criterionconditionvalue;
            this.c = criterionconditionvalue1;
        }

        public boolean a(ItemStack itemstack, int i) {
            return !this.a.a(itemstack) ? false : (!this.b.a((float) (itemstack.k() - i)) ? false : this.c.a((float) (itemstack.i() - i)));
        }
    }
}
