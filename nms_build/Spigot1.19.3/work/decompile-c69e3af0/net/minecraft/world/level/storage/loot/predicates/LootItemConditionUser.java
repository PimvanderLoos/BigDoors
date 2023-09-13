package net.minecraft.world.level.storage.loot.predicates;

import java.util.Iterator;
import java.util.function.Function;

public interface LootItemConditionUser<T extends LootItemConditionUser<T>> {

    T when(LootItemCondition.a lootitemcondition_a);

    default <E> T when(Iterable<E> iterable, Function<E, LootItemCondition.a> function) {
        T t0 = this.unwrap();

        Object object;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); t0 = t0.when((LootItemCondition.a) function.apply(object))) {
            object = iterator.next();
        }

        return t0;
    }

    T unwrap();
}
