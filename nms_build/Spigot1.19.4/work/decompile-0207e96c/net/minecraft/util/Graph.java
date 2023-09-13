package net.minecraft.util;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class Graph {

    private Graph() {}

    public static <T> boolean depthFirstSearch(Map<T, Set<T>> map, Set<T> set, Set<T> set1, Consumer<T> consumer, T t0) {
        if (set.contains(t0)) {
            return false;
        } else if (set1.contains(t0)) {
            return true;
        } else {
            set1.add(t0);
            Iterator iterator = ((Set) map.getOrDefault(t0, ImmutableSet.of())).iterator();

            Object object;

            do {
                if (!iterator.hasNext()) {
                    set1.remove(t0);
                    set.add(t0);
                    consumer.accept(t0);
                    return false;
                }

                object = iterator.next();
            } while (!depthFirstSearch(map, set, set1, consumer, object));

            return true;
        }
    }
}
