package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerSummonedEntity extends CriterionTriggerAbstract<CriterionTriggerSummonedEntity.a> {

    static final MinecraftKey ID = new MinecraftKey("summoned_entity");

    public CriterionTriggerSummonedEntity() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerSummonedEntity.ID;
    }

    @Override
    public CriterionTriggerSummonedEntity.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "entity", lootdeserializationcontext);

        return new CriterionTriggerSummonedEntity.a(criterionconditionentity_b, criterionconditionentity_b1);
    }

    public void a(EntityPlayer entityplayer, Entity entity) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entity);

        this.a(entityplayer, (criteriontriggersummonedentity_a) -> {
            return criteriontriggersummonedentity_a.a(loottableinfo);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b entity;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerSummonedEntity.ID, criterionconditionentity_b);
            this.entity = criterionconditionentity_b1;
        }

        public static CriterionTriggerSummonedEntity.a a(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerSummonedEntity.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity_a.b()));
        }

        public boolean a(LootTableInfo loottableinfo) {
            return this.entity.a(loottableinfo);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("entity", this.entity.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
