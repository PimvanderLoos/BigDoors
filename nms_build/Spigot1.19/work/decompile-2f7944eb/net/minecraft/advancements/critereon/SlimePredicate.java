package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.phys.Vec3D;

public class SlimePredicate implements EntitySubPredicate {

    private final CriterionConditionValue.IntegerRange size;

    private SlimePredicate(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
        this.size = criterionconditionvalue_integerrange;
    }

    public static SlimePredicate sized(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
        return new SlimePredicate(criterionconditionvalue_integerrange);
    }

    public static SlimePredicate fromJson(JsonObject jsonobject) {
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("size"));

        return new SlimePredicate(criterionconditionvalue_integerrange);
    }

    @Override
    public JsonObject serializeCustomData() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.add("size", this.size.serializeToJson());
        return jsonobject;
    }

    @Override
    public boolean matches(Entity entity, WorldServer worldserver, @Nullable Vec3D vec3d) {
        if (entity instanceof EntitySlime) {
            EntitySlime entityslime = (EntitySlime) entity;

            return this.size.matches(entityslime.getSize());
        } else {
            return false;
        }
    }

    @Override
    public EntitySubPredicate.a type() {
        return EntitySubPredicate.b.SLIME;
    }
}
