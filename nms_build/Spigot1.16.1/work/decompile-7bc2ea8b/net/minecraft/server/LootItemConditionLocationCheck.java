package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class LootItemConditionLocationCheck implements LootItemCondition {

    private final CriterionConditionLocation a;
    private final BlockPosition b;

    private LootItemConditionLocationCheck(CriterionConditionLocation criterionconditionlocation, BlockPosition blockposition) {
        this.a = criterionconditionlocation;
        this.b = blockposition;
    }

    @Override
    public LootItemConditionType b() {
        return LootItemConditions.m;
    }

    public boolean test(LootTableInfo loottableinfo) {
        BlockPosition blockposition = (BlockPosition) loottableinfo.getContextParameter(LootContextParameters.POSITION);

        return blockposition != null && this.a.a(loottableinfo.getWorld(), (float) (blockposition.getX() + this.b.getX()), (float) (blockposition.getY() + this.b.getY()), (float) (blockposition.getZ() + this.b.getZ()));
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
            jsonobject.add("predicate", lootitemconditionlocationcheck.a.a());
            if (lootitemconditionlocationcheck.b.getX() != 0) {
                jsonobject.addProperty("offsetX", lootitemconditionlocationcheck.b.getX());
            }

            if (lootitemconditionlocationcheck.b.getY() != 0) {
                jsonobject.addProperty("offsetY", lootitemconditionlocationcheck.b.getY());
            }

            if (lootitemconditionlocationcheck.b.getZ() != 0) {
                jsonobject.addProperty("offsetZ", lootitemconditionlocationcheck.b.getZ());
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
