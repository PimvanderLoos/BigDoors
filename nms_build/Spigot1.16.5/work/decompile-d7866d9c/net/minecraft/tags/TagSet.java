package net.minecraft.tags;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TagSet<T> implements Tag<T> {

    private final ImmutableList<T> b;
    private final Set<T> c;
    @VisibleForTesting
    protected final Class<?> a;

    protected TagSet(Set<T> set, Class<?> oclass) {
        this.a = oclass;
        this.c = set;
        this.b = ImmutableList.copyOf(set);
    }

    public static <T> TagSet<T> a() {
        return new TagSet<>(ImmutableSet.of(), Void.class);
    }

    public static <T> TagSet<T> a(Set<T> set) {
        return new TagSet<>(set, c(set));
    }

    @Override
    public boolean isTagged(T t0) {
        return this.a.isInstance(t0) && this.c.contains(t0);
    }

    @Override
    public List<T> getTagged() {
        return this.b;
    }

    private static <T> Class<?> c(Set<T> set) {
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
                    oclass = a(oclass, t0.getClass());
                }
            }

            return oclass;
        }
    }

    private static Class<?> a(Class<?> oclass, Class<?> oclass1) {
        while (!oclass.isAssignableFrom(oclass1)) {
            oclass = oclass.getSuperclass();
        }

        return oclass;
    }
}
