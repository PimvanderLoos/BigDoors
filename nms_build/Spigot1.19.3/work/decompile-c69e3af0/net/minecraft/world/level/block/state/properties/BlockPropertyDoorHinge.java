package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyDoorHinge implements INamable {

    LEFT, RIGHT;

    private BlockPropertyDoorHinge() {}

    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this == BlockPropertyDoorHinge.LEFT ? "left" : "right";
    }
}
