package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyComparatorMode implements INamable {

    COMPARE("compare"), SUBTRACT("subtract");

    private final String c;

    private BlockPropertyComparatorMode(String s) {
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
