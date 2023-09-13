package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyBambooSize implements INamable {

    NONE("none"), SMALL("small"), LARGE("large");

    private final String d;

    private BlockPropertyBambooSize(String s) {
        this.d = s;
    }

    public String toString() {
        return this.d;
    }

    @Override
    public String getName() {
        return this.d;
    }
}
