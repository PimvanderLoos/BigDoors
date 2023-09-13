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
        this.compositeCondition = LootItemConditions.a((Predicate[]) alootitemcondition);
    }

    public void a(LootCollector lootcollector) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].a(lootcollector.b(".condition[" + i + "]"));
        }

    }

    protected final boolean a(LootTableInfo loottableinfo) {
        return this.compositeCondition.test(loottableinfo);
    }

    public abstract LootEntryType a();

    public abstract static class Serializer<T extends LootEntryAbstract> implements LootSerializer<T> {

        public Serializer() {}

        public final void b(JsonObject jsonobject, T t0, JsonSerializationContext jsonserializationcontext) {
            if (!ArrayUtils.isEmpty(t0.conditions)) {
                jsonobject.add("conditions", jsonserializationcontext.serialize(t0.conditions));
            }

            this.serializeType(jsonobject, t0, jsonserializationcontext);
        }

        @Override
        public final T a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            LootItemCondition[] alootitemcondition = (LootItemCondition[]) ChatDeserializer.a(jsonobject, "conditions", new LootItemCondition[0], jsondeserializationcontext, LootItemCondition[].class);

            return this.deserializeType(jsonobject, jsondeserializationcontext, alootitemcondition);
        }

        public abstract void serializeType(JsonObject jsonobject, T t0, JsonSerializationContext jsonserializationcontext);

        public abstract T deserializeType(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition);
    }

    public abstract static class a<T extends LootEntryAbstract.a<T>> implements LootItemConditionUser<T> {

        private final List<LootItemCondition> conditions = Lists.newArrayList();

        public a() {}

        protected abstract T d();

        @Override
        public T b(LootItemCondition.a lootitemcondition_a) {
            this.conditions.add(lootitemcondition_a.build());
            return this.d();
        }

        @Override
        public final T c() {
            return this.d();
        }

        protected LootItemCondition[] f() {
            return (LootItemCondition[]) this.conditions.toArray(new LootItemCondition[0]);
        }

        public LootEntryAlternatives.a a(LootEntryAbstract.a<?> lootentryabstract_a) {
            return new LootEntryAlternatives.a(new LootEntryAbstract.a[]{this, lootentryabstract_a});
        }

        public LootEntryGroup.a b(LootEntryAbstract.a<?> lootentryabstract_a) {
            return new LootEntryGroup.a(new LootEntryAbstract.a[]{this, lootentryabstract_a});
        }

        public LootEntrySequence.a c(LootEntryAbstract.a<?> lootentryabstract_a) {
            return new LootEntrySequence.a(new LootEntryAbstract.a[]{this, lootentryabstract_a});
        }

        public abstract LootEntryAbstract b();
    }
}
