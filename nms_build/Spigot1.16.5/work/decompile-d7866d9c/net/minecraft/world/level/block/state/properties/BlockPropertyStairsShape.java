package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyStairsShape implements INamable {

    STRAIGHT("straight"), INNER_LEFT("inner_left"), INNER_RIGHT("inner_right"), OUTER_LEFT("outer_left"), OUTER_RIGHT("outer_right");

    private final String f;

    private BlockPropertyStairsShape(String s) {
        this.f = s;
    }

    public String toString() {
        return this.f;
    }

    @Override
    public String getName() {
        return this.f;
    }
}
