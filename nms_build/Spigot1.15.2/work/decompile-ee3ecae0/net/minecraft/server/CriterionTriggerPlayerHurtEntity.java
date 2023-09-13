package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerPlayerHurtEntity extends CriterionTriggerAbstract<CriterionTriggerPlayerHurtEntity.a> {

    private static final MinecraftKey a = new MinecraftKey("player_hurt_entity");

    public CriterionTriggerPlayerHurtEntity() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerPlayerHurtEntity.a;
    }

    @Override
    public CriterionTriggerPlayerHurtEntity.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionDamage criterionconditiondamage = CriterionConditionDamage.a(jsonobject.get("damage"));
        CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("entity"));

        return new CriterionTriggerPlayerHurtEntity.a(criterionconditiondamage, criterionconditionentity);
    }

    public void a(EntityPlayer entityplayer, Entity entity, DamageSource damagesource, float f, float f1, boolean flag) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerplayerhurtentity_a) -> {
            return criteriontriggerplayerhurtentity_a.a(entityplayer, entity, damagesource, f, f1, flag);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionDamage a;
        private final CriterionConditionEntity b;

        public a(CriterionConditionDamage criterionconditiondamage, CriterionConditionEntity criterionconditionentity) {
            super(CriterionTriggerPlayerHurtEntity.a);
            this.a = criterionconditiondamage;
            this.b = criterionconditionentity;
        }

        public static CriterionTriggerPlayerHurtEntity.a a(CriterionConditionDamage.a criterionconditiondamage_a) {
            return new CriterionTriggerPlayerHurtEntity.a(criterionconditiondamage_a.b(), CriterionConditionEntity.a);
        }

        public boolean a(EntityPlayer entityplayer, Entity entity, DamageSource damagesource, float f, float f1, boolean flag) {
            return !this.a.a(entityplayer, damagesource, f, f1, flag) ? false : this.b.a(entityplayer, entity);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("damage", this.a.a());
            jsonobject.add("entity", this.b.a());
            return jsonobject;
        }
    }
}
