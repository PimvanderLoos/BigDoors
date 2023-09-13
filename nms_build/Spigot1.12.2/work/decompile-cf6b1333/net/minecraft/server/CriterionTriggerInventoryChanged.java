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

public class CriterionTriggerInventoryChanged implements CriterionTrigger<CriterionTriggerInventoryChanged.b> {

    private static final MinecraftKey a = new MinecraftKey("inventory_changed");
    private final Map<AdvancementDataPlayer, CriterionTriggerInventoryChanged.a> b = Maps.newHashMap();

    public CriterionTriggerInventoryChanged() {}

    public MinecraftKey a() {
        return CriterionTriggerInventoryChanged.a;
    }

    public void a(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerInventoryChanged.b> criteriontrigger_a) {
        CriterionTriggerInventoryChanged.a criteriontriggerinventorychanged_a = (CriterionTriggerInventoryChanged.a) this.b.get(advancementdataplayer);

        if (criteriontriggerinventorychanged_a == null) {
            criteriontriggerinventorychanged_a = new CriterionTriggerInventoryChanged.a(advancementdataplayer);
            this.b.put(advancementdataplayer, criteriontriggerinventorychanged_a);
        }

        criteriontriggerinventorychanged_a.a(criteriontrigger_a);
    }

    public void b(AdvancementDataPlayer advancementdataplayer, CriterionTrigger.a<CriterionTriggerInventoryChanged.b> criteriontrigger_a) {
        CriterionTriggerInventoryChanged.a criteriontriggerinventorychanged_a = (CriterionTriggerInventoryChanged.a) this.b.get(advancementdataplayer);

        if (criteriontriggerinventorychanged_a != null) {
            criteriontriggerinventorychanged_a.b(criteriontrigger_a);
            if (criteriontriggerinventorychanged_a.a()) {
                this.b.remove(advancementdataplayer);
            }
        }

    }

    public void a(AdvancementDataPlayer advancementdataplayer) {
        this.b.remove(advancementdataplayer);
    }

    public CriterionTriggerInventoryChanged.b b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "slots", new JsonObject());
        CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject1.get("occupied"));
        CriterionConditionValue criterionconditionvalue1 = CriterionConditionValue.a(jsonobject1.get("full"));
        CriterionConditionValue criterionconditionvalue2 = CriterionConditionValue.a(jsonobject1.get("empty"));
        CriterionConditionItem[] acriterionconditionitem = CriterionConditionItem.b(jsonobject.get("items"));

        return new CriterionTriggerInventoryChanged.b(criterionconditionvalue, criterionconditionvalue1, criterionconditionvalue2, acriterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, PlayerInventory playerinventory) {
        CriterionTriggerInventoryChanged.a criteriontriggerinventorychanged_a = (CriterionTriggerInventoryChanged.a) this.b.get(entityplayer.getAdvancementData());

        if (criteriontriggerinventorychanged_a != null) {
            criteriontriggerinventorychanged_a.a(playerinventory);
        }

    }

    public CriterionInstance a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return this.b(jsonobject, jsondeserializationcontext);
    }

    static class a {

        private final AdvancementDataPlayer a;
        private final Set<CriterionTrigger.a<CriterionTriggerInventoryChanged.b>> b = Sets.newHashSet();

        public a(AdvancementDataPlayer advancementdataplayer) {
            this.a = advancementdataplayer;
        }

        public boolean a() {
            return this.b.isEmpty();
        }

        public void a(CriterionTrigger.a<CriterionTriggerInventoryChanged.b> criteriontrigger_a) {
            this.b.add(criteriontrigger_a);
        }

        public void b(CriterionTrigger.a<CriterionTriggerInventoryChanged.b> criteriontrigger_a) {
            this.b.remove(criteriontrigger_a);
        }

        public void a(PlayerInventory playerinventory) {
            ArrayList arraylist = null;
            Iterator iterator = this.b.iterator();

            CriterionTrigger.a criteriontrigger_a;

            while (iterator.hasNext()) {
                criteriontrigger_a = (CriterionTrigger.a) iterator.next();
                if (((CriterionTriggerInventoryChanged.b) criteriontrigger_a.a()).a(playerinventory)) {
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
        private final CriterionConditionValue b;
        private final CriterionConditionValue c;
        private final CriterionConditionItem[] d;

        public b(CriterionConditionValue criterionconditionvalue, CriterionConditionValue criterionconditionvalue1, CriterionConditionValue criterionconditionvalue2, CriterionConditionItem[] acriterionconditionitem) {
            super(CriterionTriggerInventoryChanged.a);
            this.a = criterionconditionvalue;
            this.b = criterionconditionvalue1;
            this.c = criterionconditionvalue2;
            this.d = acriterionconditionitem;
        }

        public boolean a(PlayerInventory playerinventory) {
            int i = 0;
            int j = 0;
            int k = 0;
            ArrayList arraylist = Lists.newArrayList(this.d);

            for (int l = 0; l < playerinventory.getSize(); ++l) {
                ItemStack itemstack = playerinventory.getItem(l);

                if (itemstack.isEmpty()) {
                    ++j;
                } else {
                    ++k;
                    if (itemstack.getCount() >= itemstack.getMaxStackSize()) {
                        ++i;
                    }

                    Iterator iterator = arraylist.iterator();

                    while (iterator.hasNext()) {
                        CriterionConditionItem criterionconditionitem = (CriterionConditionItem) iterator.next();

                        if (criterionconditionitem.a(itemstack)) {
                            iterator.remove();
                        }
                    }
                }
            }

            if (!this.b.a((float) i)) {
                return false;
            } else if (!this.c.a((float) j)) {
                return false;
            } else if (!this.a.a((float) k)) {
                return false;
            } else if (!arraylist.isEmpty()) {
                return false;
            } else {
                return true;
            }
        }
    }
}
