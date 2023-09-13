package net.minecraft.server;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public abstract class BlockDataAbstract implements IBlockData {

    private static final Joiner a = Joiner.on(',');
    private static final Function<Entry<IBlockState<?>, Comparable<?>>, String> b = new Function() {
        @Nullable
        public String a(@Nullable Entry<IBlockState<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            } else {
                IBlockState iblockstate = (IBlockState) entry.getKey();

                return iblockstate.a() + "=" + this.a(iblockstate, (Comparable) entry.getValue());
            }
        }

        private <T extends Comparable<T>> String a(IBlockState<T> iblockstate, Comparable<?> comparable) {
            return iblockstate.a(comparable);
        }

        @Nullable
        public Object apply(@Nullable Object object) {
            return this.a((Entry) object);
        }
    };

    public BlockDataAbstract() {}

    public <T extends Comparable<T>> IBlockData a(IBlockState<T> iblockstate) {
        return this.set(iblockstate, (Comparable) a(iblockstate.c(), (Object) this.get(iblockstate)));
    }

    protected static <T> T a(Collection<T> collection, T t0) {
        Iterator iterator = collection.iterator();

        do {
            if (!iterator.hasNext()) {
                return iterator.next();
            }
        } while (!iterator.next().equals(t0));

        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return collection.iterator().next();
        }
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(Block.REGISTRY.b(this.getBlock()));
        if (!this.t().isEmpty()) {
            stringbuilder.append("[");
            BlockDataAbstract.a.appendTo(stringbuilder, Iterables.transform(this.t().entrySet(), BlockDataAbstract.b));
            stringbuilder.append("]");
        }

        return stringbuilder.toString();
    }
}
