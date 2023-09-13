package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public class ByIdMap {

    public ByIdMap() {}

    private static <T> IntFunction<T> createMap(ToIntFunction<T> tointfunction, T[] at) {
        if (at.length == 0) {
            throw new IllegalArgumentException("Empty value list");
        } else {
            Int2ObjectMap<T> int2objectmap = new Int2ObjectOpenHashMap();
            Object[] aobject = at;
            int i = at.length;

            for (int j = 0; j < i; ++j) {
                T t0 = aobject[j];
                int k = tointfunction.applyAsInt(t0);
                T t1 = int2objectmap.put(k, t0);

                if (t1 != null) {
                    throw new IllegalArgumentException("Duplicate entry on id " + k + ": current=" + t0 + ", previous=" + t1);
                }
            }

            return int2objectmap;
        }
    }

    public static <T> IntFunction<T> sparse(ToIntFunction<T> tointfunction, T[] at, T t0) {
        IntFunction<T> intfunction = createMap(tointfunction, at);

        return (i) -> {
            return Objects.requireNonNullElse(intfunction.apply(i), t0);
        };
    }

    private static <T> T[] createSortedArray(ToIntFunction<T> tointfunction, T[] at) {
        int i = at.length;

        if (i == 0) {
            throw new IllegalArgumentException("Empty value list");
        } else {
            T[] at1 = (Object[]) at.clone();

            Arrays.fill(at1, (Object) null);
            Object[] aobject = at;
            int j = at.length;

            for (int k = 0; k < j; ++k) {
                T t0 = aobject[k];
                int l = tointfunction.applyAsInt(t0);

                if (l < 0 || l >= i) {
                    throw new IllegalArgumentException("Values are not continous, found index " + l + " for value " + t0);
                }

                T t1 = at1[l];

                if (t1 != null) {
                    throw new IllegalArgumentException("Duplicate entry on id " + l + ": current=" + t0 + ", previous=" + t1);
                }

                at1[l] = t0;
            }

            for (int i1 = 0; i1 < i; ++i1) {
                if (at1[i1] == null) {
                    throw new IllegalArgumentException("Missing value at index: " + i1);
                }
            }

            return at1;
        }
    }

    public static <T> IntFunction<T> continuous(ToIntFunction<T> tointfunction, T[] at, ByIdMap.a byidmap_a) {
        T[] at1 = createSortedArray(tointfunction, at);
        int i = at1.length;
        IntFunction intfunction;

        switch (byidmap_a) {
            case ZERO:
                T t0 = at1[0];

                intfunction = (j) -> {
                    return j >= 0 && j < i ? at1[j] : t0;
                };
                break;
            case WRAP:
                intfunction = (j) -> {
                    return at1[MathHelper.positiveModulo(j, i)];
                };
                break;
            case CLAMP:
                intfunction = (j) -> {
                    return at1[MathHelper.clamp(j, 0, i - 1)];
                };
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return intfunction;
    }

    public static enum a {

        ZERO, WRAP, CLAMP;

        private a() {}
    }
}
