package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class BlockStatePredicate implements Predicate<IBlockData> {

    public static final Predicate<IBlockData> a = new Predicate() {
        public boolean a(@Nullable IBlockData iblockdata) {
            return true;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((IBlockData) object);
        }
    };
    private final BlockStateList b;
    private final Map<IBlockState<?>, Predicate<?>> c = Maps.newHashMap();

    private BlockStatePredicate(BlockStateList blockstatelist) {
        this.b = blockstatelist;
    }

    public static BlockStatePredicate a(Block block) {
        return new BlockStatePredicate(block.s());
    }

    public boolean a(@Nullable IBlockData iblockdata) {
        if (iblockdata != null && iblockdata.getBlock().equals(this.b.getBlock())) {
            if (this.c.isEmpty()) {
                return true;
            } else {
                Iterator iterator = this.c.entrySet().iterator();

                Entry entry;

                do {
                    if (!iterator.hasNext()) {
                        return true;
                    }

                    entry = (Entry) iterator.next();
                } while (this.a(iblockdata, (IBlockState) entry.getKey(), (Predicate) entry.getValue()));

                return false;
            }
        } else {
            return false;
        }
    }

    protected <T extends Comparable<T>> boolean a(IBlockData iblockdata, IBlockState<T> iblockstate, Predicate<?> predicate) {
        return predicate.apply(iblockdata.get(iblockstate));
    }

    public <V extends Comparable<V>> BlockStatePredicate a(IBlockState<V> iblockstate, Predicate<? extends V> predicate) {
        if (!this.b.d().contains(iblockstate)) {
            throw new IllegalArgumentException(this.b + " cannot support property " + iblockstate);
        } else {
            this.c.put(iblockstate, predicate);
            return this;
        }
    }

    public boolean apply(@Nullable Object object) {
        return this.a((IBlockData) object);
    }
}
