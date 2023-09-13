package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.CriterionConditionLocation;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.Vec3D;

public class LootItemConditionLocationCheck implements LootItemCondition {

    final CriterionConditionLocation predicate;
    final BlockPosition offset;

    LootItemConditionLocationCheck(CriterionConditionLocation criterionconditionlocation, BlockPosition blockposition) {
        this.predicate = criterionconditionlocation;
        this.offset = blockposition;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.LOCATION_CHECK;
    }

    public boolean test(LootTableInfo loottableinfo) {
        Vec3D vec3d = (Vec3D) loottableinfo.getParamOrNull(LootContextParameters.ORIGIN);

        return vec3d != null && this.predicate.matches(loottableinfo.getLevel(), vec3d.x() + (double) this.offset.getX(), vec3d.y() + (double) this.offset.getY(), vec3d.z() + (double) this.offset.getZ());
    }

    public static LootItemCondition.a checkLocation(CriterionConditionLocation.a criterionconditionlocation_a) {
        return () -> {
            return new LootItemConditionLocationCheck(criterionconditionlocation_a.build(), BlockPosition.ZERO);
        };
    }

    public static LootItemCondition.a checkLocation(CriterionConditionLocation.a criterionconditionlocation_a, BlockPosition blockposition) {
        return () -> {
            return new LootItemConditionLocationCheck(criterionconditionlocation_a.build(), blockposition);
        };
    }

    public static class a implements LootSerializer<LootItemConditionLocationCheck> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemConditionLocationCheck lootitemconditionlocationcheck, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("predicate", lootitemconditionlocationcheck.predicate.serializeToJson());
            if (lootitemconditionlocationcheck.offset.getX() != 0) {
                jsonobject.addProperty("offsetX", lootitemconditionlocationcheck.offset.getX());
            }

            if (lootitemconditionlocationcheck.offset.getY() != 0) {
                jsonobject.addProperty("offsetY", lootitemconditionlocationcheck.offset.getY());
            }

            if (lootitemconditionlocationcheck.offset.getZ() != 0) {
                jsonobject.addProperty("offsetZ", lootitemconditionlocationcheck.offset.getZ());
            }

        }

        @Override
        public LootItemConditionLocationCheck deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.fromJson(jsonobject.get("predicate"));
            int i = ChatDeserializer.getAsInt(jsonobject, "offsetX", 0);
            int j = ChatDeserializer.getAsInt(jsonobject, "offsetY", 0);
            int k = ChatDeserializer.getAsInt(jsonobject, "offsetZ", 0);

            return new LootItemConditionLocationCheck(criterionconditionlocation, new BlockPosition(i, j, k));
        }
    }
}
