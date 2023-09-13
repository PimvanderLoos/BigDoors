package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerTamedAnimal extends CriterionTriggerAbstract<CriterionTriggerTamedAnimal.a> {

    private static final MinecraftKey a = new MinecraftKey("tame_animal");

    public CriterionTriggerTamedAnimal() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerTamedAnimal.a;
    }

    @Override
    public CriterionTriggerTamedAnimal.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("entity"));

        return new CriterionTriggerTamedAnimal.a(criterionconditionentity);
    }

    public void a(EntityPlayer entityplayer, EntityAnimal entityanimal) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggertamedanimal_a) -> {
            return criteriontriggertamedanimal_a.a(entityplayer, entityanimal);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity a;

        public a(CriterionConditionEntity criterionconditionentity) {
            super(CriterionTriggerTamedAnimal.a);
            this.a = criterionconditionentity;
        }

        public static CriterionTriggerTamedAnimal.a c() {
            return new CriterionTriggerTamedAnimal.a(CriterionConditionEntity.a);
        }

        public static CriterionTriggerTamedAnimal.a a(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerTamedAnimal.a(criterionconditionentity);
        }

        public boolean a(EntityPlayer entityplayer, EntityAnimal entityanimal) {
            return this.a.a(entityplayer, entityanimal);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("entity", this.a.a());
            return jsonobject;
        }
    }
}
