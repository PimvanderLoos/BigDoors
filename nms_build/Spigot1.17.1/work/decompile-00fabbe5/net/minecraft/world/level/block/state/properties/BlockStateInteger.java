package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class BlockStateInteger extends IBlockState<Integer> {

    private final ImmutableSet<Integer> values;

    protected BlockStateInteger(String s, int i, int j) {
        super(s, Integer.class);
        if (i < 0) {
            throw new IllegalArgumentException("Min value of " + s + " must be 0 or greater");
        } else if (j <= i) {
            throw new IllegalArgumentException("Max value of " + s + " must be greater than min (" + i + ")");
        } else {
            Set<Integer> set = Sets.newHashSet();

            for (int k = i; k <= j; ++k) {
                set.add(k);
            }

            this.values = ImmutableSet.copyOf(set);
        }
    }

    @Override
    public Collection<Integer> getValues() {
        return this.values;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof BlockStateInteger && super.equals(object)) {
            BlockStateInteger blockstateinteger = (BlockStateInteger) object;

            return this.values.equals(blockstateinteger.values);
        } else {
            return false;
        }
    }

    @Override
    public int b() {
        return 31 * super.b() + this.values.hashCode();
    }

    public static BlockStateInteger of(String s, int i, int j) {
        return new BlockStateInteger(s, i, j);
    }

    @Override
    public Optional<Integer> b(String s) {
        try {
            Integer integer = Integer.valueOf(s);

            return this.values.contains(integer) ? Optional.of(integer) : Optional.empty();
        } catch (NumberFormatException numberformatexception) {
            return Optional.empty();
        }
    }

    public String a(Integer integer) {
        return integer.toString();
    }
}
