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
    public LootItemConditionType a() {
        return LootItemConditions.VALUE_CHECK;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return Sets.union(this.provider.b(), this.range.a());
    }

    public boolean test(LootTableInfo loottableinfo) {
        return this.range.b(loottableinfo, this.provider.a(loottableinfo));
    }

    public static LootItemCondition.a a(NumberProvider numberprovider, IntRange intrange) {
        return () -> {
            return new ValueCheckCondition(numberprovider, intrange);
        };
    }

    public static class a implements LootSerializer<ValueCheckCondition> {

        public a() {}

        public void a(JsonObject jsonobject, ValueCheckCondition valuecheckcondition, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("value", jsonserializationcontext.serialize(valuecheckcondition.provider));
            jsonobject.add("range", jsonserializationcontext.serialize(valuecheckcondition.range));
        }

        @Override
        public ValueCheckCondition a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.a(jsonobject, "value", jsondeserializationcontext, NumberProvider.class);
            IntRange intrange = (IntRange) ChatDeserializer.a(jsonobject, "range", jsondeserializationcontext, IntRange.class);

            return new ValueCheckCondition(numberprovider, intrange);
        }
    }
}
