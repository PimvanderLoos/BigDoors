package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public class LootItemConditionInverted implements LootItemCondition {

    private final LootItemCondition a;

    private LootItemConditionInverted(LootItemCondition lootitemcondition) {
        this.a = lootitemcondition;
    }

    @Override
    public LootItemConditionType b() {
        return LootItemConditions.a;
    }

    public final boolean test(LootTableInfo loottableinfo) {
        return !this.a.test(loottableinfo);
    }

    @Override
    public Set<LootContextParameter<?>> a() {
        return this.a.a();
    }

    @Override
    public void a(LootCollector lootcollector) {
        LootItemCondition.super.a(lootcollector);
        this.a.a(lootcollector);
    }

    public static LootItemCondition.a a(LootItemCondition.a lootitemcondition_a) {
        LootItemConditionInverted lootitemconditioninverted = new LootItemConditionInverted(lootitemcondition_a.build());

        return () -> {
            return lootitemconditioninverted;
        };
    }

    public static class a implements LootSerializer<LootItemConditionInverted> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionInverted lootitemconditioninverted, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("term", jsonserializationcontext.serialize(lootitemconditioninverted.a));
        }

        @Override
        public LootItemConditionInverted a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            LootItemCondition lootitemcondition = (LootItemCondition) ChatDeserializer.a(jsonobject, "term", jsondeserializationcontext, LootItemCondition.class);

            return new LootItemConditionInverted(lootitemcondition);
        }
    }
}
