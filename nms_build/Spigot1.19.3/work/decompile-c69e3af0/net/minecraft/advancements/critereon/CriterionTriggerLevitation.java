package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.phys.Vec3D;

public class CriterionTriggerLevitation extends CriterionTriggerAbstract<CriterionTriggerLevitation.a> {

    static final MinecraftKey ID = new MinecraftKey("levitation");

    public CriterionTriggerLevitation() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerLevitation.ID;
    }

    @Override
    public CriterionTriggerLevitation.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.fromJson(jsonobject.get("distance"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("duration"));

        return new CriterionTriggerLevitation.a(criterionconditionentity_b, criterionconditiondistance, criterionconditionvalue_integerrange);
    }

    public void trigger(EntityPlayer entityplayer, Vec3D vec3d, int i) {
        this.trigger(entityplayer, (criteriontriggerlevitation_a) -> {
            return criteriontriggerlevitation_a.matches(entityplayer, vec3d, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionDistance distance;
        private final CriterionConditionValue.IntegerRange duration;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionDistance criterionconditiondistance, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerLevitation.ID, criterionconditionentity_b);
            this.distance = criterionconditiondistance;
            this.duration = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerLevitation.a levitated(CriterionConditionDistance criterionconditiondistance) {
            return new CriterionTriggerLevitation.a(CriterionConditionEntity.b.ANY, criterionconditiondistance, CriterionConditionValue.IntegerRange.ANY);
        }

        public boolean matches(EntityPlayer entityplayer, Vec3D vec3d, int i) {
            return !this.distance.matches(vec3d.x, vec3d.y, vec3d.z, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ()) ? false : this.duration.matches(i);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("distance", this.distance.serializeToJson());
            jsonobject.add("duration", this.duration.serializeToJson());
            return jsonobject;
        }
    }
}
