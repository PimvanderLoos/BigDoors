package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;

public class CriterionTriggerConstructBeacon extends CriterionTriggerAbstract<CriterionTriggerConstructBeacon.a> {

    static final MinecraftKey ID = new MinecraftKey("construct_beacon");

    public CriterionTriggerConstructBeacon() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerConstructBeacon.ID;
    }

    @Override
    public CriterionTriggerConstructBeacon.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("level"));

        return new CriterionTriggerConstructBeacon.a(criterionconditionentity_b, criterionconditionvalue_integerrange);
    }

    public void trigger(EntityPlayer entityplayer, int i) {
        this.trigger(entityplayer, (criteriontriggerconstructbeacon_a) -> {
            return criteriontriggerconstructbeacon_a.matches(i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionValue.IntegerRange level;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerConstructBeacon.ID, criterionconditionentity_b);
            this.level = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerConstructBeacon.a constructedBeacon() {
            return new CriterionTriggerConstructBeacon.a(CriterionConditionEntity.b.ANY, CriterionConditionValue.IntegerRange.ANY);
        }

        public static CriterionTriggerConstructBeacon.a constructedBeacon(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return new CriterionTriggerConstructBeacon.a(CriterionConditionEntity.b.ANY, criterionconditionvalue_integerrange);
        }

        public boolean matches(int i) {
            return this.level.matches(i);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("level", this.level.serializeToJson());
            return jsonobject;
        }
    }
}
