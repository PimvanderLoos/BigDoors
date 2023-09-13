package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.phys.Vec3D;

public class CriterionTriggerTargetHit extends CriterionTriggerAbstract<CriterionTriggerTargetHit.a> {

    private static final MinecraftKey a = new MinecraftKey("target_hit");

    public CriterionTriggerTargetHit() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerTargetHit.a;
    }

    @Override
    public CriterionTriggerTargetHit.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("signal_strength"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "projectile", lootdeserializationcontext);

        return new CriterionTriggerTargetHit.a(criterionconditionentity_b, criterionconditionvalue_integerrange, criterionconditionentity_b1);
    }

    public void a(EntityPlayer entityplayer, Entity entity, Vec3D vec3d, int i) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entity);

        this.a(entityplayer, (criteriontriggertargethit_a) -> {
            return criteriontriggertargethit_a.a(loottableinfo, vec3d, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionValue.IntegerRange a;
        private final CriterionConditionEntity.b b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerTargetHit.a, criterionconditionentity_b);
            this.a = criterionconditionvalue_integerrange;
            this.b = criterionconditionentity_b1;
        }

        public static CriterionTriggerTargetHit.a a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionEntity.b criterionconditionentity_b) {
            return new CriterionTriggerTargetHit.a(CriterionConditionEntity.b.a, criterionconditionvalue_integerrange, criterionconditionentity_b);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("signal_strength", this.a.d());
            jsonobject.add("projectile", this.b.a(lootserializationcontext));
            return jsonobject;
        }

        public boolean a(LootTableInfo loottableinfo, Vec3D vec3d, int i) {
            return !this.a.d(i) ? false : this.b.a(loottableinfo);
        }
    }
}
