package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionUser;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootItemFunctionConditional implements LootItemFunction {

    protected final LootItemCondition[] predicates;
    private final Predicate<LootTableInfo> compositePredicates;

    protected LootItemFunctionConditional(LootItemCondition[] alootitemcondition) {
        this.predicates = alootitemcondition;
        this.compositePredicates = LootItemConditions.andConditions(alootitemcondition);
    }

    public final ItemStack apply(ItemStack itemstack, LootTableInfo loottableinfo) {
        return this.compositePredicates.test(loottableinfo) ? this.run(itemstack, loottableinfo) : itemstack;
    }

    protected abstract ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo);

    @Override
    public void validate(LootCollector lootcollector) {
        LootItemFunction.super.validate(lootcollector);

        for (int i = 0; i < this.predicates.length; ++i) {
            this.predicates[i].validate(lootcollector.forChild(".conditions[" + i + "]"));
        }

    }

    protected static LootItemFunctionConditional.a<?> simpleBuilder(Function<LootItemCondition[], LootItemFunction> function) {
        return new LootItemFunctionConditional.b(function);
    }

    private static final class b extends LootItemFunctionConditional.a<LootItemFunctionConditional.b> {

        private final Function<LootItemCondition[], LootItemFunction> constructor;

        public b(Function<LootItemCondition[], LootItemFunction> function) {
            this.constructor = function;
        }

        @Override
        protected LootItemFunctionConditional.b getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return (LootItemFunction) this.constructor.apply(this.getConditions());
        }
    }

    public abstract static class c<T extends LootItemFunctionConditional> implements LootSerializer<T> {

        public c() {}

        public void serialize(JsonObject jsonobject, T t0, JsonSerializationContext jsonserializationcontext) {
            if (!ArrayUtils.isEmpty(t0.predicates)) {
                jsonobject.add("conditions", jsonserializationcontext.serialize(t0.predicates));
            }

        }

        @Override
        public final T deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            LootItemCondition[] alootitemcondition = (LootItemCondition[]) ChatDeserializer.getAsObject(jsonobject, "conditions", new LootItemCondition[0], jsondeserializationcontext, LootItemCondition[].class);

            return this.deserialize(jsonobject, jsondeserializationcontext, alootitemcondition);
        }

        public abstract T deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition);
    }

    public abstract static class a<T extends LootItemFunctionConditional.a<T>> implements LootItemFunction.a, LootItemConditionUser<T> {

        private final List<LootItemCondition> conditions = Lists.newArrayList();

        public a() {}

        @Override
        public T when(LootItemCondition.a lootitemcondition_a) {
            this.conditions.add(lootitemcondition_a.build());
            return this.getThis();
        }

        @Override
        public final T unwrap() {
            return this.getThis();
        }

        protected abstract T getThis();

        protected LootItemCondition[] getConditions() {
            return (LootItemCondition[]) this.conditions.toArray(new LootItemCondition[0]);
        }
    }
}
