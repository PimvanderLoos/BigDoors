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
    public LootItemConditionType a() {
        return LootItemConditions.REFERENCE;
    }

    @Override
    public void a(LootCollector lootcollector) {
        if (lootcollector.b(this.name)) {
            lootcollector.a("Condition " + this.name + " is recursively called");
        } else {
            LootItemCondition.super.a(lootcollector);
            LootItemCondition lootitemcondition = lootcollector.d(this.name);

            if (lootitemcondition == null) {
                lootcollector.a("Unknown condition table called " + this.name);
            } else {
                lootitemcondition.a(lootcollector.a(".{" + this.name + "}", this.name));
            }

        }
    }

    public boolean test(LootTableInfo loottableinfo) {
        LootItemCondition lootitemcondition = loottableinfo.b(this.name);

        if (loottableinfo.a(lootitemcondition)) {
            boolean flag;

            try {
                flag = lootitemcondition.test(loottableinfo);
            } finally {
                loottableinfo.b(lootitemcondition);
            }

            return flag;
        } else {
            LootItemConditionReference.LOGGER.warn("Detected infinite loop in loot tables");
            return false;
        }
    }

    public static LootItemCondition.a a(MinecraftKey minecraftkey) {
        return () -> {
            return new LootItemConditionReference(minecraftkey);
        };
    }

    public static class a implements LootSerializer<LootItemConditionReference> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionReference lootitemconditionreference, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("name", lootitemconditionreference.name.toString());
        }

        @Override
        public LootItemConditionReference a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "name"));

            return new LootItemConditionReference(minecraftkey);
        }
    }
}
