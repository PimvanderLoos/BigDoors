package net.minecraft.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public final class PredicateBlocks {

    public static <T> PredicateBlock<T> a(PredicateBlock<T> predicateblock) {
        return new PredicateBlocks.b<>(predicateblock);
    }

    public static <T> PredicateBlock<T> b(PredicateBlock<? super T>... apredicateblock) {
        return new PredicateBlocks.c<>(a((Object[]) apredicateblock));
    }

    private static <T> List<T> a(T... at) {
        return c(Arrays.asList(at));
    }

    private static <T> List<T> c(Iterable<T> iterable) {
        List<T> list = Lists.newArrayList();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            T t0 = iterator.next();

            list.add(Preconditions.checkNotNull(t0));
        }

        return list;
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
