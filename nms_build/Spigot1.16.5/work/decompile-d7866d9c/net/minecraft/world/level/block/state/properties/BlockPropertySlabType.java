package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertySlabType implements INamable {

    TOP("top"), BOTTOM("bottom"), DOUBLE("double");

    private final String d;

    private BlockPropertySlabType(String s) {
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
