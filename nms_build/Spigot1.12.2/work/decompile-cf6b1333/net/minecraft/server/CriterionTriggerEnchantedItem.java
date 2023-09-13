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

public class CriterionTriggerEnchantedItem implements CriterionTrigger<CriterionTriggerEnchantedItem.b> {

    private static final MinecraftKey a = new MinecraftKey("enchanted_item");
    private final Map<AdvancementDataPlayer, CriterionTriggerEnchantedItem.a> b = Maps.newHashMap();

    public CriterionTriggerEnchantedItem() {}

    public MinecraftKey a() {
        return CriterionTriggerEnchantedItem.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerEnchantedItem.b> criteriontrigger_a) {
        CriterionTriggerEnchantedItem.a criteriontriggerenchanteditem_a = (CriterionTriggerEnchantedItem.a) this.b.get(advancementdataplayer);

        if (criteriontriggerenchanteditem_a == null) {
            criteriontriggerenchanteditem_a = new CriterionTriggerEnchantedItem.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggerenchanteditem_a);
        }

        criteriontriggerenchanteditem_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerEnchantedItem.b> criteriontrigger_a) {
        CriterionTriggerEnchantedItem.a criteriontriggerenchanteditem_a = (CriterionTriggerEnchantedItem.a) this.b.get(advancementdataplayer);

        if (criteriontriggerenchanteditem_a != null) {
            criteriontriggerenchanteditem_a.b(criteriontrigger_a);
            if (criteriontriggerenchanteditem_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerEnchantedItem.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject.get("levels"));

        return new CriterionTriggerEnchantedItem.b(criterionconditionitem, criterionconditionvalue);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, int i) {
        CriterionTriggerEnchantedItem.a criteriontriggerenchanteditem_a = (CriterionTriggerEnchantedItem.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggerenchanteditem_a != null) {
            criteriontriggerenchanteditem_a.a(itemstack, i);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerEnchantedItem.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerEnchantedItem.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerEnchantedItem.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(ItemStack itemstack, int i) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerEnchantedItem.b) criteriontrigger_a.a()).a(itemstack, i)) {
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

        public b(CriterionConditionItem criterionconditionitem, CriterionConditionValue criterionconditionvalue) {
            super(CriterionTriggerEnchantedItem.a);
            this.a = criterionconditionitem;
            this.b = criterionconditionvalue;
        }

        public boolean a(ItemStack itemstack, int i) {
            return !this.a.a(itemstack) ? false : this.b.a((float) i);
        }
    }
}
