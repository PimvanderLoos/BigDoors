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
    public MinecraftKey a() {
        return CriterionTriggerBredAnimals.ID;
    }

    @Override
    public CriterionTriggerBredAnimals.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "parent", lootdeserializationcontext);
        CriterionConditionEntity.b criterionconditionentity_b2 = CriterionConditionEntity.b.a(jsonobject, "partner", lootdeserializationcontext);
        CriterionConditionEntity.b criterionconditionentity_b3 = CriterionConditionEntity.b.a(jsonobject, "child", lootdeserializationcontext);

        return new CriterionTriggerBredAnimals.a(criterionconditionentity_b, criterionconditionentity_b1, criterionconditionentity_b2, criterionconditionentity_b3);
    }

    public void a(EntityPlayer entityplayer, EntityAnimal entityanimal, EntityAnimal entityanimal1, @Nullable EntityAgeable entityageable) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entityanimal);
        LootTableInfo loottableinfo1 = CriterionConditionEntity.b(entityplayer, entityanimal1);
        LootTableInfo loottableinfo2 = entityageable != null ? CriterionConditionEntity.b(entityplayer, entityageable) : null;

        this.a(entityplayer, (criteriontriggerbredanimals_a) -> {
            return criteriontriggerbredanimals_a.a(loottableinfo, loottableinfo1, loottableinfo2);
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

        public static CriterionTriggerBredAnimals.a c() {
            return new CriterionTriggerBredAnimals.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY);
        }

        public static CriterionTriggerBredAnimals.a a(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerBredAnimals.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity_a.b()));
        }

        public static CriterionTriggerBredAnimals.a a(CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1, CriterionConditionEntity criterionconditionentity2) {
            return new CriterionTriggerBredAnimals.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity), CriterionConditionEntity.b.a(criterionconditionentity1), CriterionConditionEntity.b.a(criterionconditionentity2));
        }

        public boolean a(LootTableInfo loottableinfo, LootTableInfo loottableinfo1, @Nullable LootTableInfo loottableinfo2) {
            return this.child != CriterionConditionEntity.b.ANY && (loottableinfo2 == null || !this.child.a(loottableinfo2)) ? false : this.parent.a(loottableinfo) && this.partner.a(loottableinfo1) || this.parent.a(loottableinfo1) && this.partner.a(loottableinfo);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("parent", this.parent.a(lootserializationcontext));
            jsonobject.add("partner", this.partner.a(lootserializationcontext));
            jsonobject.add("child", this.child.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
