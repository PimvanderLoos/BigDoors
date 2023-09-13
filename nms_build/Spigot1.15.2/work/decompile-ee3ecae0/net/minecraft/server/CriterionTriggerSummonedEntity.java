package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerSummonedEntity extends CriterionTriggerAbstract<CriterionTriggerSummonedEntity.a> {

    private static final MinecraftKey a = new MinecraftKey("summoned_entity");

    public CriterionTriggerSummonedEntity() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerSummonedEntity.a;
    }

    @Override
    public CriterionTriggerSummonedEntity.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("entity"));

        return new CriterionTriggerSummonedEntity.a(criterionconditionentity);
    }

    public void a(EntityPlayer entityplayer, Entity entity) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggersummonedentity_a) -> {
            return criteriontriggersummonedentity_a.a(entityplayer, entity);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity a;

        public a(CriterionConditionEntity criterionconditionentity) {
            super(CriterionTriggerSummonedEntity.a);
            this.a = criterionconditionentity;
        }

        public static CriterionTriggerSummonedEntity.a a(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerSummonedEntity.a(criterionconditionentity_a.b());
        }

        public boolean a(EntityPlayer entityplayer, Entity entity) {
            return this.a.a(entityplayer, entity);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("entity", this.a.a());
            return jsonobject;
        }
    }
}
