package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyBedPart implements INamable {

    HEAD("head"), FOOT("foot");

    private final String name;

    private BlockPropertyBedPart(String s) {
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
