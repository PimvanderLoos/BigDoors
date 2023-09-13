package net.minecraft.world.level.storage.loot.functions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public interface LootItemFunctionUser<T extends LootItemFunctionUser<T>> {

    T apply(LootItemFunction.a lootitemfunction_a);

    default <E> T apply(Iterable<E> iterable, Function<E, LootItemFunction.a> function) {
        T t0 = this.unwrap();

        Object object;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); t0 = t0.apply((LootItemFunction.a) function.apply(object))) {
            object = iterator.next();
        }

        return t0;
    }

    default <E> T apply(E[] ae, Function<E, LootItemFunction.a> function) {
        return this.apply((Iterable) Arrays.asList(ae), function);
    }

    T unwrap();
}
