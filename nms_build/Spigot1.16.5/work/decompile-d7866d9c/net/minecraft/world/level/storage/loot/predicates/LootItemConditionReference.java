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
    private final MinecraftKey b;

    private LootItemConditionReference(MinecraftKey minecraftkey) {
        this.b = minecraftkey;
    }

    @Override
    public LootItemConditionType b() {
        return LootItemConditions.o;
    }

    @Override
    public void a(LootCollector lootcollector) {
        if (lootcollector.b(this.b)) {
            lootcollector.a("Condition " + this.b + " is recursively called");
        } else {
            LootItemCondition.super.a(lootcollector);
            LootItemCondition lootitemcondition = lootcollector.d(this.b);

            if (lootitemcondition == null) {
                lootcollector.a("Unknown condition table called " + this.b);
            } else {
                lootitemcondition.a(lootcollector.a(".{" + this.b + "}", this.b));
            }

        }
    }

    public boolean test(LootTableInfo loottableinfo) {
        LootItemCondition lootitemcondition = loottableinfo.b(this.b);

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

    public static class a implements LootSerializer<LootItemConditionReference> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionReference lootitemconditionreference, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("name", lootitemconditionreference.b.toString());
        }

        @Override
        public LootItemConditionReference a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "name"));

            return new LootItemConditionReference(minecraftkey);
        }
    }
}
