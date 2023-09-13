package net.minecraft.server;

import com.google.gson.JsonObject;

public class CriterionTriggerPlayerHurtEntity extends CriterionTriggerAbstract<CriterionTriggerPlayerHurtEntity.a> {

    private static final MinecraftKey a = new MinecraftKey("player_hurt_entity");

    public CriterionTriggerPlayerHurtEntity() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerPlayerHurtEntity.a;
    }

    @Override
    public CriterionTriggerPlayerHurtEntity.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionDamage criterionconditiondamage = CriterionConditionDamage.a(jsonobject.get("damage"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "entity", lootdeserializationcontext);

        return new CriterionTriggerPlayerHurtEntity.a(criterionconditionentity_b, criterionconditiondamage, criterionconditionentity_b1);
    }

    public void a(EntityPlayer entityplayer, Entity entity, DamageSource damagesource, float f, float f1, boolean flag) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entity);

        this.a(entityplayer, (criteriontriggerplayerhurtentity_a) -> {
            return criteriontriggerplayerhurtentity_a.a(entityplayer, loottableinfo, damagesource, f, f1, flag);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionDamage a;
        private final CriterionConditionEntity.b b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionDamage criterionconditiondamage, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerPlayerHurtEntity.a, criterionconditionentity_b);
            this.a = criterionconditiondamage;
            this.b = criterionconditionentity_b1;
        }

        public static CriterionTriggerPlayerHurtEntity.a a(CriterionConditionDamage.a criterionconditiondamage_a) {
            return new CriterionTriggerPlayerHurtEntity.a(CriterionConditionEntity.b.a, criterionconditiondamage_a.b(), CriterionConditionEntity.b.a);
        }

        public boolean a(EntityPlayer entityplayer, LootTableInfo loottableinfo, DamageSource damagesource, float f, float f1, boolean flag) {
            return !this.a.a(entityplayer, damagesource, f, f1, flag) ? false : this.b.a(loottableinfo);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("damage", this.a.a());
            jsonobject.add("entity", this.b.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
