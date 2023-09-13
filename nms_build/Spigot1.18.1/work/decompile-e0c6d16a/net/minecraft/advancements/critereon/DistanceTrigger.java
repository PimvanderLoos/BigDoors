package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.phys.Vec3D;

public class DistanceTrigger extends CriterionTriggerAbstract<DistanceTrigger.a> {

    final MinecraftKey id;

    public DistanceTrigger(MinecraftKey minecraftkey) {
        this.id = minecraftkey;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public DistanceTrigger.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.fromJson(jsonobject.get("start_position"));
        CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.fromJson(jsonobject.get("distance"));

        return new DistanceTrigger.a(this.id, criterionconditionentity_b, criterionconditionlocation, criterionconditiondistance);
    }

    public void trigger(EntityPlayer entityplayer, Vec3D vec3d) {
        Vec3D vec3d1 = entityplayer.position();

        this.trigger(entityplayer, (distancetrigger_a) -> {
            return distancetrigger_a.matches(entityplayer.getLevel(), vec3d, vec3d1);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionLocation startPosition;
        private final CriterionConditionDistance distance;

        public a(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionLocation criterionconditionlocation, CriterionConditionDistance criterionconditiondistance) {
            super(minecraftkey, criterionconditionentity_b);
            this.startPosition = criterionconditionlocation;
            this.distance = criterionconditiondistance;
        }

        public static DistanceTrigger.a fallFromHeight(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDistance criterionconditiondistance, CriterionConditionLocation criterionconditionlocation) {
            return new DistanceTrigger.a(CriterionTriggers.FALL_FROM_HEIGHT.id, CriterionConditionEntity.b.wrap(criterionconditionentity_a.build()), criterionconditionlocation, criterionconditiondistance);
        }

        public static DistanceTrigger.a rideEntityInLava(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDistance criterionconditiondistance) {
            return new DistanceTrigger.a(CriterionTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER.id, CriterionConditionEntity.b.wrap(criterionconditionentity_a.build()), CriterionConditionLocation.ANY, criterionconditiondistance);
        }

        public static DistanceTrigger.a travelledThroughNether(CriterionConditionDistance criterionconditiondistance) {
            return new DistanceTrigger.a(CriterionTriggers.NETHER_TRAVEL.id, CriterionConditionEntity.b.ANY, CriterionConditionLocation.ANY, criterionconditiondistance);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("start_position", this.startPosition.serializeToJson());
            jsonobject.add("distance", this.distance.serializeToJson());
            return jsonobject;
        }

        public boolean matches(WorldServer worldserver, Vec3D vec3d, Vec3D vec3d1) {
            return !this.startPosition.matches(worldserver, vec3d.x, vec3d.y, vec3d.z) ? false : this.distance.matches(vec3d.x, vec3d.y, vec3d.z, vec3d1.x, vec3d1.y, vec3d1.z);
        }
    }
}
