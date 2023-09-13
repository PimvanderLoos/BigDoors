package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyBedPart implements INamable {

    HEAD("head"), FOOT("foot");

    private final String c;

    private BlockPropertyBedPart(String s) {
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
