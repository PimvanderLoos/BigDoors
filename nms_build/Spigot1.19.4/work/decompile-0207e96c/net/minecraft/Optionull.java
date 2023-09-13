package net.minecraft;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class Optionull {

    public Optionull() {}

    @Nullable
    public static <T, R> R map(@Nullable T t0, Function<T, R> function) {
        return t0 == null ? null : function.apply(t0);
    }

    public static <T, R> R mapOrDefault(@Nullable T t0, Function<T, R> function, R r0) {
        return t0 == null ? r0 : function.apply(t0);
    }

    public static <T, R> R mapOrElse(@Nullable T t0, Function<T, R> function, Supplier<R> supplier) {
        return t0 == null ? supplier.get() : function.apply(t0);
    }

    @Nullable
    public static <T> T first(Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();

        return iterator.hasNext() ? iterator.next() : null;
    }

    public static <T> T firstOrDefault(Collection<T> collection, T t0) {
        Iterator<T> iterator = collection.iterator();

        return iterator.hasNext() ? iterator.next() : t0;
    }

    public static <T> T firstOrElse(Collection<T> collection, Supplier<T> supplier) {
        Iterator<T> iterator = collection.iterator();

        return iterator.hasNext() ? iterator.next() : supplier.get();
    }

    public static <T> boolean isNullOrEmpty(@Nullable T[] at) {
        return at == null || at.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable boolean[] aboolean) {
        return aboolean == null || aboolean.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable byte[] abyte) {
        return abyte == null || abyte.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable char[] achar) {
        return achar == null || achar.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable short[] ashort) {
        return ashort == null || ashort.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable int[] aint) {
        return aint == null || aint.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable long[] along) {
        return along == null || along.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable float[] afloat) {
        return afloat == null || afloat.length == 0;
    }

    public static boolean isNullOrEmpty(@Nullable double[] adouble) {
        return adouble == null || adouble.length == 0;
    }
}
