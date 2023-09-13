package net.minecraft.server;

import com.google.gson.JsonObject;

public class CriterionTriggerUsedEnderEye extends CriterionTriggerAbstract<CriterionTriggerUsedEnderEye.a> {

    private static final MinecraftKey a = new MinecraftKey("used_ender_eye");

    public CriterionTriggerUsedEnderEye() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerUsedEnderEye.a;
    }

    @Override
    public CriterionTriggerUsedEnderEye.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionValue.FloatRange criterionconditionvalue_floatrange = CriterionConditionValue.FloatRange.a(jsonobject.get("distance"));

        return new CriterionTriggerUsedEnderEye.a(criterionconditionentity_b, criterionconditionvalue_floatrange);
    }

    public void a(EntityPlayer entityplayer, BlockPosition blockposition) {
        double d0 = entityplayer.locX() - (double) blockposition.getX();
        double d1 = entityplayer.locZ() - (double) blockposition.getZ();
        double d2 = d0 * d0 + d1 * d1;

        this.a(entityplayer, (criteriontriggerusedendereye_a) -> {
            return criteriontriggerusedendereye_a.a(d2);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionValue.FloatRange a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionValue.FloatRange criterionconditionvalue_floatrange) {
            super(CriterionTriggerUsedEnderEye.a, criterionconditionentity_b);
            this.a = criterionconditionvalue_floatrange;
        }

        public boolean a(double d0) {
            return this.a.a(d0);
        }
    }
}
