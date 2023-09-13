package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerTamedAnimal extends CriterionTriggerAbstract<CriterionTriggerTamedAnimal.a> {

    static final MinecraftKey ID = new MinecraftKey("tame_animal");

    public CriterionTriggerTamedAnimal() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerTamedAnimal.ID;
    }

    @Override
    public CriterionTriggerTamedAnimal.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "entity", lootdeserializationcontext);

        return new CriterionTriggerTamedAnimal.a(criterionconditionentity_b, criterionconditionentity_b1);
    }

    public void trigger(EntityPlayer entityplayer, EntityAnimal entityanimal) {
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entityanimal);

        this.trigger(entityplayer, (criteriontriggertamedanimal_a) -> {
            return criteriontriggertamedanimal_a.matches(loottableinfo);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b entity;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerTamedAnimal.ID, criterionconditionentity_b);
            this.entity = criterionconditionentity_b1;
        }

        public static CriterionTriggerTamedAnimal.a tamedAnimal() {
            return new CriterionTriggerTamedAnimal.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY);
        }

        public static CriterionTriggerTamedAnimal.a tamedAnimal(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerTamedAnimal.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.wrap(criterionconditionentity));
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
