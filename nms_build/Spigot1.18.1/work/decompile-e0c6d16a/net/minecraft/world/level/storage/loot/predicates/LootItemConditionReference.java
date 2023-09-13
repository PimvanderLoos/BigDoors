package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemConditionReference implements LootItemCondition {

    private static final Logger LOGGER = LogManager.getLogger();
    final MinecraftKey name;

    LootItemConditionReference(MinecraftKey minecraftkey) {
        this.name = minecraftkey;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.REFERENCE;
    }

    @Override
    public void validate(LootCollector lootcollector) {
        if (lootcollector.hasVisitedCondition(this.name)) {
            lootcollector.reportProblem("Condition " + this.name + " is recursively called");
        } else {
            LootItemCondition.super.validate(lootcollector);
            LootItemCondition lootitemcondition = lootcollector.resolveCondition(this.name);

            if (lootitemcondition == null) {
                lootcollector.reportProblem("Unknown condition table called " + this.name);
            } else {
                lootitemcondition.validate(lootcollector.enterTable(".{" + this.name + "}", this.name));
            }

        }
    }

    public boolean test(LootTableInfo loottableinfo) {
        LootItemCondition lootitemcondition = loottableinfo.getCondition(this.name);

        if (loottableinfo.addVisitedCondition(lootitemcondition)) {
            boolean flag;

            try {
                flag = lootitemcondition.test(loottableinfo);
            } finally {
                loottableinfo.removeVisitedCondition(lootitemcondition);
            }

            return flag;
        } else {
            LootItemConditionReference.LOGGER.warn("Detected infinite loop in loot tables");
            return false;
        }
    }

    public static LootItemCondition.a conditionReference(MinecraftKey minecraftkey) {
        return () -> {
            return new LootItemConditionReference(minecraftkey);
        };
    }

    public static class a implements LootSerializer<LootItemConditionReference> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemConditionReference lootitemconditionreference, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("name", lootitemconditionreference.name.toString());
        }

        @Override
        public LootItemConditionReference deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "name"));

            return new LootItemConditionReference(minecraftkey);
        }
    }
}
