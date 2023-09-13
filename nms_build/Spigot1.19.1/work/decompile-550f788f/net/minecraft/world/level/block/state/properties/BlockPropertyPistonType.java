package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyPistonType implements INamable {

    DEFAULT("normal"), STICKY("sticky");

    private final String name;

    private BlockPropertyPistonType(String s) {
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
