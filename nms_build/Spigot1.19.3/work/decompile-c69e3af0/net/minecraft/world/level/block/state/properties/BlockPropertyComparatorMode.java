package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyComparatorMode implements INamable {

    COMPARE("compare"), SUBTRACT("subtract");

    private final String name;

    private BlockPropertyComparatorMode(String s) {
        this.name = s;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
