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
    public MinecraftKey getId() {
        return CriterionTriggerSummonedEntity.ID;
    }

    @Override
    public CriterionTriggerSummonedEntity.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "entity", lootdeserializationcontext);

        return new CriterionTriggerSummonedEntity.a(criterionconditionentity_b, criterionconditionentity_b1);
    }

    public void trigger(EntityPlayer entityplayer, Entity entity) {
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entity);

        this.trigger(entityplayer, (criteriontriggersummonedentity_a) -> {
            return criteriontriggersummonedentity_a.matches(loottableinfo);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b entity;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerSummonedEntity.ID, criterionconditionentity_b);
            this.entity = criterionconditionentity_b1;
        }

        public static CriterionTriggerSummonedEntity.a summonedEntity(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerSummonedEntity.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.wrap(criterionconditionentity_a.build()));
        }

        public boolean matches(LootTableInfo loottableinfo) {
            return this.entity.matches(loottableinfo);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("entity", this.entity.toJson(lootserializationcontext));
            return jsonobject;
        }
    }
}
