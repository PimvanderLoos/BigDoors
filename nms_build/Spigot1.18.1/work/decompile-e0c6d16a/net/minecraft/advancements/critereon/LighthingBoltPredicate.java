package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.phys.Vec3D;

public class LighthingBoltPredicate {

    public static final LighthingBoltPredicate ANY = new LighthingBoltPredicate(CriterionConditionValue.IntegerRange.ANY, CriterionConditionEntity.ANY);
    private static final String BLOCKS_SET_ON_FIRE_KEY = "blocks_set_on_fire";
    private static final String ENTITY_STRUCK_KEY = "entity_struck";
    private final CriterionConditionValue.IntegerRange blocksSetOnFire;
    private final CriterionConditionEntity entityStruck;

    private LighthingBoltPredicate(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionEntity criterionconditionentity) {
        this.blocksSetOnFire = criterionconditionvalue_integerrange;
        this.entityStruck = criterionconditionentity;
    }

    public static LighthingBoltPredicate blockSetOnFire(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
        return new LighthingBoltPredicate(criterionconditionvalue_integerrange, CriterionConditionEntity.ANY);
    }

    public static LighthingBoltPredicate fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "lightning");

            return new LighthingBoltPredicate(CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("blocks_set_on_fire")), CriterionConditionEntity.fromJson(jsonobject.get("entity_struck")));
        } else {
            return LighthingBoltPredicate.ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == LighthingBoltPredicate.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("blocks_set_on_fire", this.blocksSetOnFire.serializeToJson());
            jsonobject.add("entity_struck", this.entityStruck.serializeToJson());
            return jsonobject;
        }
    }

    public boolean matches(Entity entity, WorldServer worldserver, @Nullable Vec3D vec3d) {
        if (this == LighthingBoltPredicate.ANY) {
            return true;
        } else if (!(entity instanceof EntityLightning)) {
            return false;
        } else {
            EntityLightning entitylightning = (EntityLightning) entity;

            return this.blocksSetOnFire.matches(entitylightning.getBlocksSetOnFire()) && (this.entityStruck == CriterionConditionEntity.ANY || entitylightning.getHitEntities().anyMatch((entity1) -> {
                return this.entityStruck.matches(worldserver, vec3d, entity1);
            }));
        }
    }
}
