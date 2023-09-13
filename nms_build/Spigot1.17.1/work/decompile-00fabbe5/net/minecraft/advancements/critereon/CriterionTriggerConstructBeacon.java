package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;

public class CriterionTriggerConstructBeacon extends CriterionTriggerAbstract<CriterionTriggerConstructBeacon.a> {

    static final MinecraftKey ID = new MinecraftKey("construct_beacon");

    public CriterionTriggerConstructBeacon() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerConstructBeacon.ID;
    }

    @Override
    public CriterionTriggerConstructBeacon.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("level"));

        return new CriterionTriggerConstructBeacon.a(criterionconditionentity_b, criterionconditionvalue_integerrange);
    }

    public void a(EntityPlayer entityplayer, int i) {
        this.a(entityplayer, (criteriontriggerconstructbeacon_a) -> {
            return criteriontriggerconstructbeacon_a.a(i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionValue.IntegerRange level;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerConstructBeacon.ID, criterionconditionentity_b);
            this.level = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerConstructBeacon.a c() {
            return new CriterionTriggerConstructBeacon.a(CriterionConditionEntity.b.ANY, CriterionConditionValue.IntegerRange.ANY);
        }

        public static CriterionTriggerConstructBeacon.a a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return new CriterionTriggerConstructBeacon.a(CriterionConditionEntity.b.ANY, criterionconditionvalue_integerrange);
        }

        public boolean a(int i) {
            return this.level.d(i);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("level", this.level.d());
            return jsonobject;
        }
    }
}
