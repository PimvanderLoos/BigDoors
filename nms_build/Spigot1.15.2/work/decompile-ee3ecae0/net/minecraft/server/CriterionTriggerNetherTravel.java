package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerNetherTravel extends CriterionTriggerAbstract<CriterionTriggerNetherTravel.a> {

    private static final MinecraftKey a = new MinecraftKey("nether_travel");

    public CriterionTriggerNetherTravel() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerNetherTravel.a;
    }

    @Override
    public CriterionTriggerNetherTravel.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a(jsonobject.get("entered"));
        CriterionConditionLocation criterionconditionlocation1 = CriterionConditionLocation.a(jsonobject.get("exited"));
        CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.a(jsonobject.get("distance"));

        return new CriterionTriggerNetherTravel.a(criterionconditionlocation, criterionconditionlocation1, criterionconditiondistance);
    }

    public void a(EntityPlayer entityplayer, Vec3D vec3d) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggernethertravel_a) -> {
            return criteriontriggernethertravel_a.a(entityplayer.getWorldServer(), vec3d, entityplayer.locX(), entityplayer.locY(), entityplayer.locZ());
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionLocation a;
        private final CriterionConditionLocation b;
        private final CriterionConditionDistance c;

        public a(CriterionConditionLocation criterionconditionlocation, CriterionConditionLocation criterionconditionlocation1, CriterionConditionDistance criterionconditiondistance) {
            super(CriterionTriggerNetherTravel.a);
            this.a = criterionconditionlocation;
            this.b = criterionconditionlocation1;
            this.c = criterionconditiondistance;
        }

        public static CriterionTriggerNetherTravel.a a(CriterionConditionDistance criterionconditiondistance) {
            return new CriterionTriggerNetherTravel.a(CriterionConditionLocation.a, CriterionConditionLocation.a, criterionconditiondistance);
        }

        public boolean a(WorldServer worldserver, Vec3D vec3d, double d0, double d1, double d2) {
            return !this.a.a(worldserver, vec3d.x, vec3d.y, vec3d.z) ? false : (!this.b.a(worldserver, d0, d1, d2) ? false : this.c.a(vec3d.x, vec3d.y, vec3d.z, d0, d1, d2));
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("entered", this.a.a());
            jsonobject.add("exited", this.b.a());
            jsonobject.add("distance", this.c.a());
            return jsonobject;
        }
    }
}
