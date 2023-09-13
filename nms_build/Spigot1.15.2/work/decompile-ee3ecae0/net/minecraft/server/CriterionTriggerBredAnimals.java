package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class CriterionTriggerBredAnimals extends CriterionTriggerAbstract<CriterionTriggerBredAnimals.a> {

    private static final MinecraftKey a = new MinecraftKey("bred_animals");

    public CriterionTriggerBredAnimals() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerBredAnimals.a;
    }

    @Override
    public CriterionTriggerBredAnimals.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("parent"));
        CriterionConditionEntity criterionconditionentity1 = CriterionConditionEntity.a(jsonobject.get("partner"));
        CriterionConditionEntity criterionconditionentity2 = CriterionConditionEntity.a(jsonobject.get("child"));

        return new CriterionTriggerBredAnimals.a(criterionconditionentity, criterionconditionentity1, criterionconditionentity2);
    }

    public void a(EntityPlayer entityplayer, EntityAnimal entityanimal, @Nullable EntityAnimal entityanimal1, @Nullable EntityAgeable entityageable) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerbredanimals_a) -> {
            return criteriontriggerbredanimals_a.a(entityplayer, entityanimal, entityanimal1, entityageable);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity a;
        private final CriterionConditionEntity b;
        private final CriterionConditionEntity c;

        public a(CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1, CriterionConditionEntity criterionconditionentity2) {
            super(CriterionTriggerBredAnimals.a);
            this.a = criterionconditionentity;
            this.b = criterionconditionentity1;
            this.c = criterionconditionentity2;
        }

        public static CriterionTriggerBredAnimals.a c() {
            return new CriterionTriggerBredAnimals.a(CriterionConditionEntity.a, CriterionConditionEntity.a, CriterionConditionEntity.a);
        }

        public static CriterionTriggerBredAnimals.a a(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerBredAnimals.a(criterionconditionentity_a.b(), CriterionConditionEntity.a, CriterionConditionEntity.a);
        }

        public boolean a(EntityPlayer entityplayer, EntityAnimal entityanimal, @Nullable EntityAnimal entityanimal1, @Nullable EntityAgeable entityageable) {
            return !this.c.a(entityplayer, entityageable) ? false : this.a.a(entityplayer, entityanimal) && this.b.a(entityplayer, entityanimal1) || this.a.a(entityplayer, entityanimal1) && this.b.a(entityplayer, entityanimal);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("parent", this.a.a());
            jsonobject.add("partner", this.b.a());
            jsonobject.add("child", this.c.a());
            return jsonobject;
        }
    }
}
