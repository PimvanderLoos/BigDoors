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

    final LootItemCondition term;

    LootItemConditionInverted(LootItemCondition lootitemcondition) {
        this.term = lootitemcondition;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.INVERTED;
    }

    public final boolean test(LootTableInfo loottableinfo) {
        return !this.term.test(loottableinfo);
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return this.term.getReferencedContextParams();
    }

    @Override
    public void validate(LootCollector lootcollector) {
        LootItemCondition.super.validate(lootcollector);
        this.term.validate(lootcollector);
    }

    public static LootItemCondition.a invert(LootItemCondition.a lootitemcondition_a) {
        LootItemConditionInverted lootitemconditioninverted = new LootItemConditionInverted(lootitemcondition_a.build());

        return () -> {
            return lootitemconditioninverted;
        };
    }

    public static class a implements LootSerializer<LootItemConditionInverted> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemConditionInverted lootitemconditioninverted, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("term", jsonserializationcontext.serialize(lootitemconditioninverted.term));
        }

        @Override
        public LootItemConditionInverted deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            LootItemCondition lootitemcondition = (LootItemCondition) ChatDeserializer.getAsObject(jsonobject, "term", jsondeserializationcontext, LootItemCondition.class);

            return new LootItemConditionInverted(lootitemcondition);
        }
    }
}
