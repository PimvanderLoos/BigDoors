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

public class CriterionTriggerConsumeItem implements CriterionTrigger<CriterionTriggerConsumeItem.b> {

    private static final MinecraftKey a = new MinecraftKey("consume_item");
    private final Map<AdvancementDataPlayer, CriterionTriggerConsumeItem.a> b = Maps.newHashMap();

    public CriterionTriggerConsumeItem() {}

    public MinecraftKey a() {
        return CriterionTriggerConsumeItem.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerConsumeItem.b> criteriontrigger_a) {
        CriterionTriggerConsumeItem.a criteriontriggerconsumeitem_a = (CriterionTriggerConsumeItem.a) this.b.get(advancementdataplayer);

        if (criteriontriggerconsumeitem_a == null) {
            criteriontriggerconsumeitem_a = new CriterionTriggerConsumeItem.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggerconsumeitem_a);
        }

        criteriontriggerconsumeitem_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerConsumeItem.b> criteriontrigger_a) {
        CriterionTriggerConsumeItem.a criteriontriggerconsumeitem_a = (CriterionTriggerConsumeItem.a) this.b.get(advancementdataplayer);

        if (criteriontriggerconsumeitem_a != null) {
            criteriontriggerconsumeitem_a.b(criteriontrigger_a);
            if (criteriontriggerconsumeitem_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerConsumeItem.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerConsumeItem.b(criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack) {
        CriterionTriggerConsumeItem.a criteriontriggerconsumeitem_a = (CriterionTriggerConsumeItem.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggerconsumeitem_a != null) {
            criteriontriggerconsumeitem_a.a(itemstack);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerConsumeItem.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerConsumeItem.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerConsumeItem.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(ItemStack itemstack) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerConsumeItem.b) criteriontrigger_a.a()).a(itemstack)) {
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

        public b(CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerConsumeItem.a);
            this.a = criterionconditionitem;
        }

        public boolean a(ItemStack itemstack) {
            return this.a.a(itemstack);
        }
    }
}
