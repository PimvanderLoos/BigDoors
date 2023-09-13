package net.minecraft.server;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;

public class BlockStateInteger extends BlockState<Integer> {

    private final ImmutableSet<Integer> a;

    protected BlockStateInteger(String s, int i, int j) {
        super(s, Integer.class);
        if (i < 0) {
            throw new IllegalArgumentException("Min value of " + s + " must be 0 or greater");
        } else if (j <= i) {
            throw new IllegalArgumentException("Max value of " + s + " must be greater than min (" + i + ")");
        } else {
            HashSet hashset = Sets.newHashSet();

            for (int k = i; k <= j; ++k) {
                hashset.add(Integer.valueOf(k));
            }

            this.a = ImmutableSet.copyOf(hashset);
        }
    }

    public Collection<Integer> c() {
        return this.a;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof BlockStateInteger && super.equals(object)) {
            BlockStateInteger blockstateinteger = (BlockStateInteger) object;

            return this.a.equals(blockstateinteger.a);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return 31 * super.hashCode() + this.a.hashCode();
    }

    public static BlockStateInteger of(String s, int i, int j) {
        return new BlockStateInteger(s, i, j);
    }

    public Optional<Integer> b(String s) {
        try {
            Integer integer = Integer.valueOf(s);

            return this.a.contains(integer) ? Optional.of(integer) : Optional.absent();
        } catch (NumberFormatException numberformatexception) {
            return Optional.absent();
        }
    }

    public String a(Integer integer) {
        return integer.toString();
    }
}
