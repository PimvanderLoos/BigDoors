package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;

public class CriterionTriggerFishingRodHooked extends CriterionTriggerAbstract<CriterionTriggerFishingRodHooked.a> {

    private static final MinecraftKey a = new MinecraftKey("fishing_rod_hooked");

    public CriterionTriggerFishingRodHooked() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerFishingRodHooked.a;
    }

    @Override
    public CriterionTriggerFishingRodHooked.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("rod"));
        CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("entity"));
        CriterionConditionItem criterionconditionitem1 = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerFishingRodHooked.a(criterionconditionitem, criterionconditionentity, criterionconditionitem1);
    }

    public void a(EntityPlayer entityplayer, ItemStack itemstack, EntityFishingHook entityfishinghook, Collection<ItemStack> collection) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerfishingrodhooked_a) -> {
            return criteriontriggerfishingrodhooked_a.a(entityplayer, itemstack, entityfishinghook, collection);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionItem a;
        private final CriterionConditionEntity b;
        private final CriterionConditionItem c;

        public a(CriterionConditionItem criterionconditionitem, CriterionConditionEntity criterionconditionentity, CriterionConditionItem criterionconditionitem1) {
            super(CriterionTriggerFishingRodHooked.a);
            this.a = criterionconditionitem;
            this.b = criterionconditionentity;
            this.c = criterionconditionitem1;
        }

        public static CriterionTriggerFishingRodHooked.a a(CriterionConditionItem criterionconditionitem, CriterionConditionEntity criterionconditionentity, CriterionConditionItem criterionconditionitem1) {
            return new CriterionTriggerFishingRodHooked.a(criterionconditionitem, criterionconditionentity, criterionconditionitem1);
        }

        public boolean a(EntityPlayer entityplayer, ItemStack itemstack, EntityFishingHook entityfishinghook, Collection<ItemStack> collection) {
            if (!this.a.a(itemstack)) {
                return false;
            } else if (!this.b.a(entityplayer, entityfishinghook.hooked)) {
                return false;
            } else {
                if (this.c != CriterionConditionItem.a) {
                    boolean flag = false;

                    if (entityfishinghook.hooked instanceof EntityItem) {
                        EntityItem entityitem = (EntityItem) entityfishinghook.hooked;

                        if (this.c.a(entityitem.getItemStack())) {
                            flag = true;
                        }
                    }

                    Iterator iterator = collection.iterator();

                    while (iterator.hasNext()) {
                        ItemStack itemstack1 = (ItemStack) iterator.next();

                        if (this.c.a(itemstack1)) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }

                return true;
            }
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("rod", this.a.a());
            jsonobject.add("entity", this.b.a());
            jsonobject.add("item", this.c.a());
            return jsonobject;
        }
    }
}
