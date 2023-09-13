package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerConstructBeacon extends CriterionTriggerAbstract<CriterionTriggerConstructBeacon.a> {

    private static final MinecraftKey a = new MinecraftKey("construct_beacon");

    public CriterionTriggerConstructBeacon() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerConstructBeacon.a;
    }

    @Override
    public CriterionTriggerConstructBeacon.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("level"));

        return new CriterionTriggerConstructBeacon.a(criterionconditionvalue_integerrange);
    }

    public void a(EntityPlayer entityplayer, TileEntityBeacon tileentitybeacon) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerconstructbeacon_a) -> {
            return criteriontriggerconstructbeacon_a.a(tileentitybeacon);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionValue.IntegerRange a;

        public a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerConstructBeacon.a);
            this.a = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerConstructBeacon.a a(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return new CriterionTriggerConstructBeacon.a(criterionconditionvalue_integerrange);
        }

        public boolean a(TileEntityBeacon tileentitybeacon) {
            return this.a.d(tileentitybeacon.h());
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("level", this.a.d());
            return jsonobject;
        }
    }
}
