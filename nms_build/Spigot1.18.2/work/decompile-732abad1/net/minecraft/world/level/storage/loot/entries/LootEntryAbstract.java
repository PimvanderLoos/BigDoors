package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionUser;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootEntryAbstract implements LootEntryChildren {

    protected final LootItemCondition[] conditions;
    private final Predicate<LootTableInfo> compositeCondition;

    protected LootEntryAbstract(LootItemCondition[] alootitemcondition) {
        this.conditions = alootitemcondition;
        this.compositeCondition = LootItemConditions.andConditions(alootitemcondition);
    }

    public void validate(LootCollector lootcollector) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(lootcollector.forChild(".condition[" + i + "]"));
        }

    }

    protected final boolean canRun(LootTableInfo loottableinfo) {
        return this.compositeCondition.test(loottableinfo);
    }

    public abstract LootEntryType getType();

    public abstract static class Serializer<T extends LootEntryAbstract> implements LootSerializer<T> {

        public Serializer() {}

        public final void serialize(JsonObject jsonobject, T t0, JsonSerializationContext jsonserializationcontext) {
            if (!ArrayUtils.isEmpty(t0.conditions)) {
                jsonobject.add("conditions", jsonserializationcontext.serialize(t0.conditions));
            }

            this.serializeCustom(jsonobject, t0, jsonserializationcontext);
        }

        @Override
        public final T deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            LootItemCondition[] alootitemcondition = (LootItemCondition[]) ChatDeserializer.getAsObject(jsonobject, "conditions", new LootItemCondition[0], jsondeserializationcontext, LootItemCondition[].class);

            return this.deserializeCustom(jsonobject, jsondeserializationcontext, alootitemcondition);
        }

        public abstract void serializeCustom(JsonObject jsonobject, T t0, JsonSerializationContext jsonserializationcontext);

        public abstract T deserializeCustom(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition);
    }

    public abstract static class a<T extends LootEntryAbstract.a<T>> implements LootItemConditionUser<T> {

        private final List<LootItemCondition> conditions = Lists.newArrayList();

        public a() {}

        protected abstract T getThis();

        @Override
        public T when(LootItemCondition.a lootitemcondition_a) {
            this.conditions.add(lootitemcondition_a.build());
            return this.getThis();
        }

        @Override
        public final T unwrap() {
            return this.getThis();
        }

        protected LootItemCondition[] getConditions() {
            return (LootItemCondition[]) this.conditions.toArray(new LootItemCondition[0]);
        }

        public LootEntryAlternatives.a otherwise(LootEntryAbstract.a<?> lootentryabstract_a) {
            return new LootEntryAlternatives.a(new LootEntryAbstract.a[]{this, lootentryabstract_a});
        }

        public LootEntryGroup.a append(LootEntryAbstract.a<?> lootentryabstract_a) {
            return new LootEntryGroup.a(new LootEntryAbstract.a[]{this, lootentryabstract_a});
        }

        public LootEntrySequence.a then(LootEntryAbstract.a<?> lootentryabstract_a) {
            return new LootEntrySequence.a(new LootEntryAbstract.a[]{this, lootentryabstract_a});
        }

        public abstract LootEntryAbstract build();
    }
}
