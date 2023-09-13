package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.phys.Vec3D;

public class CriterionTriggerNetherTravel extends CriterionTriggerAbstract<CriterionTriggerNetherTravel.a> {

    static final MinecraftKey ID = new MinecraftKey("nether_travel");

    public CriterionTriggerNetherTravel() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerNetherTravel.ID;
    }

    @Override
    public CriterionTriggerNetherTravel.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a(jsonobject.get("entered"));
        CriterionConditionLocation criterionconditionlocation1 = CriterionConditionLocation.a(jsonobject.get("exited"));
        CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.a(jsonobject.get("distance"));

        return new CriterionTriggerNetherTravel.a(criterionconditionentity_b, criterionconditionlocation, criterionconditionlocation1, criterionconditiondistance);
    }

    public void a(EntityPlayer entityplayer, Vec3D vec3d) {
        this.a(entityplayer, (criteriontriggernethertravel_a) -> {
            return criteriontriggernethertravel_a.a(entityplayer.getWorldServer(), vec3d, entityplayer.locX(), entityplayer.locY(), entityplayer.locZ());
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionLocation entered;
        private final CriterionConditionLocation exited;
        private final CriterionConditionDistance distance;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionLocation criterionconditionlocation, CriterionConditionLocation criterionconditionlocation1, CriterionConditionDistance criterionconditiondistance) {
            super(CriterionTriggerNetherTravel.ID, criterionconditionentity_b);
            this.entered = criterionconditionlocation;
            this.exited = criterionconditionlocation1;
            this.distance = criterionconditiondistance;
        }

        public static CriterionTriggerNetherTravel.a a(CriterionConditionDistance criterionconditiondistance) {
            return new CriterionTriggerNetherTravel.a(CriterionConditionEntity.b.ANY, CriterionConditionLocation.ANY, CriterionConditionLocation.ANY, criterionconditiondistance);
        }

        public boolean a(WorldServer worldserver, Vec3D vec3d, double d0, double d1, double d2) {
            return !this.entered.a(worldserver, vec3d.x, vec3d.y, vec3d.z) ? false : (!this.exited.a(worldserver, d0, d1, d2) ? false : this.distance.a(vec3d.x, vec3d.y, vec3d.z, d0, d1, d2));
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("entered", this.entered.a());
            jsonobject.add("exited", this.exited.a());
            jsonobject.add("distance", this.distance.a());
            return jsonobject;
        }
    }
}
