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
    public LootItemConditionType a() {
        return LootItemConditions.LOCATION_CHECK;
    }

    public boolean test(LootTableInfo loottableinfo) {
        Vec3D vec3d = (Vec3D) loottableinfo.getContextParameter(LootContextParameters.ORIGIN);

        return vec3d != null && this.predicate.a(loottableinfo.getWorld(), vec3d.getX() + (double) this.offset.getX(), vec3d.getY() + (double) this.offset.getY(), vec3d.getZ() + (double) this.offset.getZ());
    }

    public static LootItemCondition.a a(CriterionConditionLocation.a criterionconditionlocation_a) {
        return () -> {
            return new LootItemConditionLocationCheck(criterionconditionlocation_a.b(), BlockPosition.ZERO);
        };
    }

    public static LootItemCondition.a a(CriterionConditionLocation.a criterionconditionlocation_a, BlockPosition blockposition) {
        return () -> {
            return new LootItemConditionLocationCheck(criterionconditionlocation_a.b(), blockposition);
        };
    }

    public static class a implements LootSerializer<LootItemConditionLocationCheck> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionLocationCheck lootitemconditionlocationcheck, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("predicate", lootitemconditionlocationcheck.predicate.a());
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
        public LootItemConditionLocationCheck a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a(jsonobject.get("predicate"));
            int i = ChatDeserializer.a(jsonobject, "offsetX", (int) 0);
            int j = ChatDeserializer.a(jsonobject, "offsetY", (int) 0);
            int k = ChatDeserializer.a(jsonobject, "offsetZ", (int) 0);

            return new LootItemConditionLocationCheck(criterionconditionlocation, new BlockPosition(i, j, k));
        }
    }
}
