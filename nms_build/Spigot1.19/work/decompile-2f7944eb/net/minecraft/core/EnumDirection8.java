package net.minecraft.core;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum EnumDirection8 {

    NORTH(new EnumDirection[]{EnumDirection.NORTH}), NORTH_EAST(new EnumDirection[]{EnumDirection.NORTH, EnumDirection.EAST}), EAST(new EnumDirection[]{EnumDirection.EAST}), SOUTH_EAST(new EnumDirection[]{EnumDirection.SOUTH, EnumDirection.EAST}), SOUTH(new EnumDirection[]{EnumDirection.SOUTH}), SOUTH_WEST(new EnumDirection[]{EnumDirection.SOUTH, EnumDirection.WEST}), WEST(new EnumDirection[]{EnumDirection.WEST}), NORTH_WEST(new EnumDirection[]{EnumDirection.NORTH, EnumDirection.WEST});

    private final Set<EnumDirection> directions;
    private final BaseBlockPosition step;

    private EnumDirection8(EnumDirection... aenumdirection) {
        this.directions = Sets.immutableEnumSet(Arrays.asList(aenumdirection));
        this.step = new BaseBlockPosition(0, 0, 0);
        EnumDirection[] aenumdirection1 = aenumdirection;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection1[j];

            this.step.setX(this.step.getX() + enumdirection.getStepX()).setY(this.step.getY() + enumdirection.getStepY()).setZ(this.step.getZ() + enumdirection.getStepZ());
        }

    }

    public Set<EnumDirection> getDirections() {
        return this.directions;
    }

    public int getStepX() {
        return this.step.getX();
    }

    public int getStepZ() {
        return this.step.getZ();
    }
}
