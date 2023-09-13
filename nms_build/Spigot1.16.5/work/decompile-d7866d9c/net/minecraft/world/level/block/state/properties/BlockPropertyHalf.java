package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyHalf implements INamable {

    TOP("top"), BOTTOM("bottom");

    private final String c;

    private BlockPropertyHalf(String s) {
        this.c = s;
    }

    public String toString() {
        return this.c;
    }

    @Override
    public String getName() {
        return this.c;
    }
}
