package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerCuredZombieVillager extends CriterionTriggerAbstract<CriterionTriggerCuredZombieVillager.a> {

    private static final MinecraftKey a = new MinecraftKey("cured_zombie_villager");

    public CriterionTriggerCuredZombieVillager() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerCuredZombieVillager.a;
    }

    @Override
    public CriterionTriggerCuredZombieVillager.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("zombie"));
        CriterionConditionEntity criterionconditionentity1 = CriterionConditionEntity.a(jsonobject.get("villager"));

        return new CriterionTriggerCuredZombieVillager.a(criterionconditionentity, criterionconditionentity1);
    }

    public void a(EntityPlayer entityplayer, EntityZombie entityzombie, EntityVillager entityvillager) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggercuredzombievillager_a) -> {
            return criteriontriggercuredzombievillager_a.a(entityplayer, entityzombie, entityvillager);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity a;
        private final CriterionConditionEntity b;

        public a(CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1) {
            super(CriterionTriggerCuredZombieVillager.a);
            this.a = criterionconditionentity;
            this.b = criterionconditionentity1;
        }

        public static CriterionTriggerCuredZombieVillager.a c() {
            return new CriterionTriggerCuredZombieVillager.a(CriterionConditionEntity.a, CriterionConditionEntity.a);
        }

        public boolean a(EntityPlayer entityplayer, EntityZombie entityzombie, EntityVillager entityvillager) {
            return !this.a.a(entityplayer, entityzombie) ? false : this.b.a(entityplayer, entityvillager);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("zombie", this.a.a());
            jsonobject.add("villager", this.b.a());
            return jsonobject;
        }
    }
}
