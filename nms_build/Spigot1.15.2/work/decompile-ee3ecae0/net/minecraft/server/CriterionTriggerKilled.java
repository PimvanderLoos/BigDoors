package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerKilled extends CriterionTriggerAbstract<CriterionTriggerKilled.a> {

    private final MinecraftKey a;

    public CriterionTriggerKilled(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    @Override
    public MinecraftKey a() {
        return this.a;
    }

    @Override
    public CriterionTriggerKilled.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        return new CriterionTriggerKilled.a(this.a, CriterionConditionEntity.a(jsonobject.get("entity")), CriterionConditionDamageSource.a(jsonobject.get("killing_blow")));
    }

    public void a(EntityPlayer entityplayer, Entity entity, DamageSource damagesource) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerkilled_a) -> {
            return criteriontriggerkilled_a.a(entityplayer, entity, damagesource);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity a;
        private final CriterionConditionDamageSource b;

        public a(MinecraftKey minecraftkey, CriterionConditionEntity criterionconditionentity, CriterionConditionDamageSource criterionconditiondamagesource) {
            super(minecraftkey);
            this.a = criterionconditionentity;
            this.b = criterionconditiondamagesource;
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.b.a, criterionconditionentity_a.b(), CriterionConditionDamageSource.a);
        }

        public static CriterionTriggerKilled.a c() {
            return new CriterionTriggerKilled.a(CriterionTriggers.b.a, CriterionConditionEntity.a, CriterionConditionDamageSource.a);
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.b.a, criterionconditionentity_a.b(), criterionconditiondamagesource_a.b());
        }

        public static CriterionTriggerKilled.a d() {
            return new CriterionTriggerKilled.a(CriterionTriggers.c.a, CriterionConditionEntity.a, CriterionConditionDamageSource.a);
        }

        public boolean a(EntityPlayer entityplayer, Entity entity, DamageSource damagesource) {
            return !this.b.a(entityplayer, damagesource) ? false : this.a.a(entityplayer, entity);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("entity", this.a.a());
            jsonobject.add("killing_blow", this.b.a());
            return jsonobject;
        }
    }
}
