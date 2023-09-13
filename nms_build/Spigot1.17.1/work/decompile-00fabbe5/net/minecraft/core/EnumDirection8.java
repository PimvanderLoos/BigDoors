package net.minecraft.core;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum EnumDirection8 {

    NORTH(new EnumDirection[]{EnumDirection.NORTH}), NORTH_EAST(new EnumDirection[]{EnumDirection.NORTH, EnumDirection.EAST}), EAST(new EnumDirection[]{EnumDirection.EAST}), SOUTH_EAST(new EnumDirection[]{EnumDirection.SOUTH, EnumDirection.EAST}), SOUTH(new EnumDirection[]{EnumDirection.SOUTH}), SOUTH_WEST(new EnumDirection[]{EnumDirection.SOUTH, EnumDirection.WEST}), WEST(new EnumDirection[]{EnumDirection.WEST}), NORTH_WEST(new EnumDirection[]{EnumDirection.NORTH, EnumDirection.WEST});

    private final Set<EnumDirection> directions;

    private EnumDirection8(EnumDirection... aenumdirection) {
        this.directions = Sets.immutableEnumSet(Arrays.asList(aenumdirection));
    }

    public Set<EnumDirection> a() {
        return this.directions;
    }
}
