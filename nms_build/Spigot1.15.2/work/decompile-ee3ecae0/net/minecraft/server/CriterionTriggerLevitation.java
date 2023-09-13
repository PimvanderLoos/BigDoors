package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerLevitation extends CriterionTriggerAbstract<CriterionTriggerLevitation.a> {

    private static final MinecraftKey a = new MinecraftKey("levitation");

    public CriterionTriggerLevitation() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerLevitation.a;
    }

    @Override
    public CriterionTriggerLevitation.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.a(jsonobject.get("distance"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("duration"));

        return new CriterionTriggerLevitation.a(criterionconditiondistance, criterionconditionvalue_integerrange);
    }

    public void a(EntityPlayer entityplayer, Vec3D vec3d, int i) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerlevitation_a) -> {
            return criteriontriggerlevitation_a.a(entityplayer, vec3d, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionDistance a;
        private final CriterionConditionValue.IntegerRange b;

        public a(CriterionConditionDistance criterionconditiondistance, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerLevitation.a);
            this.a = criterionconditiondistance;
            this.b = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerLevitation.a a(CriterionConditionDistance criterionconditiondistance) {
            return new CriterionTriggerLevitation.a(criterionconditiondistance, CriterionConditionValue.IntegerRange.e);
        }

        public boolean a(EntityPlayer entityplayer, Vec3D vec3d, int i) {
            return !this.a.a(vec3d.x, vec3d.y, vec3d.z, entityplayer.locX(), entityplayer.locY(), entityplayer.locZ()) ? false : this.b.d(i);
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("distance", this.a.a());
            jsonobject.add("duration", this.b.d());
            return jsonobject;
        }
    }
}
