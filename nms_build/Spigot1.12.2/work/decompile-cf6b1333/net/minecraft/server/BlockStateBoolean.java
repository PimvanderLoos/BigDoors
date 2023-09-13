package net.minecraft.server;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;

public class BlockStateBoolean extends BlockState<Boolean> {

    private final ImmutableSet<Boolean> a = ImmutableSet.of(Boolean.valueOf(true), Boolean.valueOf(false));

    protected BlockStateBoolean(String s) {
        super(s, Boolean.class);
    }

    public Collection<Boolean> c() {
        return this.a;
    }

    public static BlockStateBoolean of(String s) {
        return new BlockStateBoolean(s);
    }

    public Optional<Boolean> b(String s) {
        return !"true".equals(s) && !"false".equals(s) ? Optional.absent() : Optional.of(Boolean.valueOf(s));
    }

    public String a(Boolean obool) {
        return obool.toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof BlockStateBoolean && super.equals(object)) {
            BlockStateBoolean blockstateboolean = (BlockStateBoolean) object;

            return this.a.equals(blockstateboolean.a);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return 31 * super.hashCode() + this.a.hashCode();
    }
}
