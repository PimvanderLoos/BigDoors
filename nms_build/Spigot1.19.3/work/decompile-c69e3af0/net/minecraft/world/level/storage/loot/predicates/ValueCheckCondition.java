package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class ValueCheckCondition implements LootItemCondition {

    final NumberProvider provider;
    final IntRange range;

    ValueCheckCondition(NumberProvider numberprovider, IntRange intrange) {
        this.provider = numberprovider;
        this.range = intrange;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.VALUE_CHECK;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return Sets.union(this.provider.getReferencedContextParams(), this.range.getReferencedContextParams());
    }

    public boolean test(LootTableInfo loottableinfo) {
        return this.range.test(loottableinfo, this.provider.getInt(loottableinfo));
    }

    public static LootItemCondition.a hasValue(NumberProvider numberprovider, IntRange intrange) {
        return () -> {
            return new ValueCheckCondition(numberprovider, intrange);
        };
    }

    public static class a implements LootSerializer<ValueCheckCondition> {

        public a() {}

        public void serialize(JsonObject jsonobject, ValueCheckCondition valuecheckcondition, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("value", jsonserializationcontext.serialize(valuecheckcondition.provider));
            jsonobject.add("range", jsonserializationcontext.serialize(valuecheckcondition.range));
        }

        @Override
        public ValueCheckCondition deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "value", jsondeserializationcontext, NumberProvider.class);
            IntRange intrange = (IntRange) ChatDeserializer.getAsObject(jsonobject, "range", jsondeserializationcontext, IntRange.class);

            return new ValueCheckCondition(numberprovider, intrange);
        }
    }
}
