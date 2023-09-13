package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerVillagerTrade extends CriterionTriggerAbstract<CriterionTriggerVillagerTrade.a> {

    private static final MinecraftKey a = new MinecraftKey("villager_trade");

    public CriterionTriggerVillagerTrade() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerVillagerTrade.a;
    }

    @Override
    public CriterionTriggerVillagerTrade.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("villager"));
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerVillagerTrade.a(criterionconditionentity, criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, EntityVillagerAbstract entityvillagerabstract, ItemStack itemstack) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggervillagertrade_a) -> {
            return criteriontriggervillagertrade_a.a(entityplayer, entityvillagerabstract, itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity a;
        private final CriterionConditionItem b;

        public a(CriterionConditionEntity criterionconditionentity, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerVillagerTrade.a);
            this.a = criterionconditionentity;
            this.b = criterionconditionitem;
        }

        public static CriterionTriggerVillagerTrade.a c() {
            return new CriterionTriggerVillagerTrade.a(CriterionConditionEntity.a, CriterionConditionItem.a);
        }

        public boolean a(EntityPlayer entityplayer, EntityVillagerAbstract entityvillagerabstract, ItemStack itemstack) {
            return !this.a.a(entityplayer, entityvillagerabstract) ? false : this.b.a(itemstack);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("item", this.b.a());
            jsonobject.add("villager", this.a.a());
            return jsonobject;
        }
    }
}
