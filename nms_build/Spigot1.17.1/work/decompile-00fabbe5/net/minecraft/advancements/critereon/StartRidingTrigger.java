package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;

public class StartRidingTrigger extends CriterionTriggerAbstract<StartRidingTrigger.a> {

    static final MinecraftKey ID = new MinecraftKey("started_riding");

    public StartRidingTrigger() {}

    @Override
    public MinecraftKey a() {
        return StartRidingTrigger.ID;
    }

    @Override
    public StartRidingTrigger.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        return new StartRidingTrigger.a(criterionconditionentity_b);
    }

    public void a(EntityPlayer entityplayer) {
        this.a(entityplayer, (startridingtrigger_a) -> {
            return true;
        });
    }

    public static class a extends CriterionInstanceAbstract {

        public a(CriterionConditionEntity.b criterionconditionentity_b) {
            super(StartRidingTrigger.ID, criterionconditionentity_b);
        }

        public static StartRidingTrigger.a a(CriterionConditionEntity.a criterionconditionentity_a) {
            return new StartRidingTrigger.a(CriterionConditionEntity.b.a(criterionconditionentity_a.b()));
        }
    }
}
