package net.minecraft.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public final class PredicateBlocks {

    public static <T> PredicateBlock<T> a(PredicateBlock<T> predicateblock) {
        return new PredicateBlocks.b(predicateblock);
    }

    public static <T> PredicateBlock<T> b(PredicateBlock<? super T>... apredicateblock) {
        return new PredicateBlocks.c(a((Object[]) apredicateblock), null);
    }

    private static <T> List<T> a(T... at) {
        return c(Arrays.asList(at));
    }

    private static <T> List<T> c(Iterable<T> iterable) {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            arraylist.add(Preconditions.checkNotNull(object));
        }

        return arraylist;
    }

    static class c<T> implements PredicateBlock<T> {

        private final List<? extends PredicateBlock<? super T>> a;

        private c(List<? extends PredicateBlock<? super T>> list) {
            this.a = list;
        }

        public boolean test(@Nullable T t0, IBlockAccess iblockaccess, BlockPosition blockposition) {
            for (int i = 0; i < this.a.size(); ++i) {
                if (((PredicateBlock) this.a.get(i)).test(t0, iblockaccess, blockposition)) {
                    return true;
                }
            }

            return false;
        }

        c(List list, Object object) {
            this(list);
        }
    }

    static class b<T> implements PredicateBlock<T> {

        private final PredicateBlock<T> a;

        b(PredicateBlock<T> predicateblock) {
            this.a = (PredicateBlock) Preconditions.checkNotNull(predicateblock);
        }

        public boolean test(@Nullable T t0, IBlockAccess iblockaccess, BlockPosition blockposition) {
            return !this.a.test(t0, iblockaccess, blockposition);
        }
    }
}
