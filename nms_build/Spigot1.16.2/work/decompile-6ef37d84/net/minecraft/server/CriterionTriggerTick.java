package net.minecraft.server;

import com.google.gson.JsonObject;

public class CriterionTriggerTick extends CriterionTriggerAbstract<CriterionTriggerTick.a> {

    public static final MinecraftKey a = new MinecraftKey("tick");

    public CriterionTriggerTick() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerTick.a;
    }

    @Override
    public CriterionTriggerTick.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        return new CriterionTriggerTick.a(criterionconditionentity_b);
    }

    public void a(EntityPlayer entityplayer) {
        this.a(entityplayer, (criteriontriggertick_a) -> {
            return true;
        });
    }

    public static class a extends CriterionInstanceAbstract {

        public a(CriterionConditionEntity.b criterionconditionentity_b) {
            super(CriterionTriggerTick.a, criterionconditionentity_b);
        }
    }
}
