package net.minecraft.tags;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TagSet<T> implements Tag<T> {

    private final ImmutableList<T> valuesList;
    private final Set<T> values;
    @VisibleForTesting
    protected final Class<?> closestCommonSuperType;

    protected TagSet(Set<T> set, Class<?> oclass) {
        this.closestCommonSuperType = oclass;
        this.values = set;
        this.valuesList = ImmutableList.copyOf(set);
    }

    public static <T> TagSet<T> empty() {
        return new TagSet<>(ImmutableSet.of(), Void.class);
    }

    public static <T> TagSet<T> create(Set<T> set) {
        return new TagSet<>(set, findCommonSuperClass(set));
    }

    @Override
    public boolean contains(T t0) {
        return this.closestCommonSuperType.isInstance(t0) && this.values.contains(t0);
    }

    @Override
    public List<T> getValues() {
        return this.valuesList;
    }

    private static <T> Class<?> findCommonSuperClass(Set<T> set) {
        if (set.isEmpty()) {
            return Void.class;
        } else {
            Class<?> oclass = null;
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                T t0 = iterator.next();

                if (oclass == null) {
                    oclass = t0.getClass();
                } else {
                    oclass = findClosestAncestor(oclass, t0.getClass());
                }
            }

            return oclass;
        }
    }

    private static Class<?> findClosestAncestor(Class<?> oclass, Class<?> oclass1) {
        while (!oclass.isAssignableFrom(oclass1)) {
            oclass = oclass.getSuperclass();
        }

        return oclass;
    }
}
