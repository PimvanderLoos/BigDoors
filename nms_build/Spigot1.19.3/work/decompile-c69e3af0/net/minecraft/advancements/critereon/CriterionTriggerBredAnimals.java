package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerBredAnimals extends CriterionTriggerAbstract<CriterionTriggerBredAnimals.a> {

    static final MinecraftKey ID = new MinecraftKey("bred_animals");

    public CriterionTriggerBredAnimals() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerBredAnimals.ID;
    }

    @Override
    public CriterionTriggerBredAnimals.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "parent", lootdeserializationcontext);
        CriterionConditionEntity.b criterionconditionentity_b2 = CriterionConditionEntity.b.fromJson(jsonobject, "partner", lootdeserializationcontext);
        CriterionConditionEntity.b criterionconditionentity_b3 = CriterionConditionEntity.b.fromJson(jsonobject, "child", lootdeserializationcontext);

        return new CriterionTriggerBredAnimals.a(criterionconditionentity_b, criterionconditionentity_b1, criterionconditionentity_b2, criterionconditionentity_b3);
    }

    public void trigger(EntityPlayer entityplayer, EntityAnimal entityanimal, EntityAnimal entityanimal1, @Nullable EntityAgeable entityageable) {
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entityanimal);
        LootTableInfo loottableinfo1 = CriterionConditionEntity.createContext(entityplayer, entityanimal1);
        LootTableInfo loottableinfo2 = entityageable != null ? CriterionConditionEntity.createContext(entityplayer, entityageable) : null;

        this.trigger(entityplayer, (criteriontriggerbredanimals_a) -> {
            return criteriontriggerbredanimals_a.matches(loottableinfo, loottableinfo1, loottableinfo2);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b parent;
        private final CriterionConditionEntity.b partner;
        private final CriterionConditionEntity.b child;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1, CriterionConditionEntity.b criterionconditionentity_b2, CriterionConditionEntity.b criterionconditionentity_b3) {
            super(CriterionTriggerBredAnimals.ID, criterionconditionentity_b);
            this.parent = criterionconditionentity_b1;
            this.partner = criterionconditionentity_b2;
            this.child = criterionconditionentity_b3;
        }

        public static CriterionTriggerBredAnimals.a bredAnimals() {
            return new CriterionTriggerBredAnimals.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY);
        }

        public static CriterionTriggerBredAnimals.a bredAnimals(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerBredAnimals.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.wrap(criterionconditionentity_a.build()));
        }

        public static CriterionTriggerBredAnimals.a bredAnimals(CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1, CriterionConditionEntity criterionconditionentity2) {
            return new CriterionTriggerBredAnimals.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.wrap(criterionconditionentity), CriterionConditionEntity.b.wrap(criterionconditionentity1), CriterionConditionEntity.b.wrap(criterionconditionentity2));
        }

        public boolean matches(LootTableInfo loottableinfo, LootTableInfo loottableinfo1, @Nullable LootTableInfo loottableinfo2) {
            return this.child != CriterionConditionEntity.b.ANY && (loottableinfo2 == null || !this.child.matches(loottableinfo2)) ? false : this.parent.matches(loottableinfo) && this.partner.matches(loottableinfo1) || this.parent.matches(loottableinfo1) && this.partner.matches(loottableinfo);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("parent", this.parent.toJson(lootserializationcontext));
            jsonobject.add("partner", this.partner.toJson(lootserializationcontext));
            jsonobject.add("child", this.child.toJson(lootserializationcontext));
            return jsonobject;
        }
    }
}
