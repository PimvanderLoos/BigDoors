package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EntityFishingHook;
import net.minecraft.world.phys.Vec3D;

public class CriterionConditionInOpenWater implements EntitySubPredicate {

    public static final CriterionConditionInOpenWater ANY = new CriterionConditionInOpenWater(false);
    private static final String IN_OPEN_WATER_KEY = "in_open_water";
    private final boolean inOpenWater;

    private CriterionConditionInOpenWater(boolean flag) {
        this.inOpenWater = flag;
    }

    public static CriterionConditionInOpenWater inOpenWater(boolean flag) {
        return new CriterionConditionInOpenWater(flag);
    }

    public static CriterionConditionInOpenWater fromJson(JsonObject jsonobject) {
        JsonElement jsonelement = jsonobject.get("in_open_water");

        return jsonelement != null ? new CriterionConditionInOpenWater(ChatDeserializer.convertToBoolean(jsonelement, "in_open_water")) : CriterionConditionInOpenWater.ANY;
    }

    @Override
    public JsonObject serializeCustomData() {
        if (this == CriterionConditionInOpenWater.ANY) {
            return new JsonObject();
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("in_open_water", new JsonPrimitive(this.inOpenWater));
            return jsonobject;
        }
    }

    @Override
    public EntitySubPredicate.a type() {
        return EntitySubPredicate.b.FISHING_HOOK;
    }

    @Override
    public boolean matches(Entity entity, WorldServer worldserver, @Nullable Vec3D vec3d) {
        if (this == CriterionConditionInOpenWater.ANY) {
            return true;
        } else if (!(entity instanceof EntityFishingHook)) {
            return false;
        } else {
            EntityFishingHook entityfishinghook = (EntityFishingHook) entity;

            return this.inOpenWater == entityfishinghook.isOpenWaterFishing();
        }
    }
}
