package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;

public class BlockStateBoolean extends IBlockState<Boolean> {

    private final ImmutableSet<Boolean> values = ImmutableSet.of(true, false);

    protected BlockStateBoolean(String s) {
        super(s, Boolean.class);
    }

    @Override
    public Collection<Boolean> getPossibleValues() {
        return this.values;
    }

    public static BlockStateBoolean create(String s) {
        return new BlockStateBoolean(s);
    }

    @Override
    public Optional<Boolean> getValue(String s) {
        return !"true".equals(s) && !"false".equals(s) ? Optional.empty() : Optional.of(Boolean.valueOf(s));
    }

    public String getName(Boolean obool) {
        return obool.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof BlockStateBoolean && super.equals(object)) {
            BlockStateBoolean blockstateboolean = (BlockStateBoolean) object;

            return this.values.equals(blockstateboolean.values);
        } else {
            return false;
        }
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + this.values.hashCode();
    }
}
