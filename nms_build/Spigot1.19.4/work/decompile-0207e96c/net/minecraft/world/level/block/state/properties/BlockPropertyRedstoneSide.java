package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyRedstoneSide implements INamable {

    UP("up"), SIDE("side"), NONE("none");

    private final String name;

    private BlockPropertyRedstoneSide(String s) {
        this.name = s;
    }

    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean isConnected() {
        return this != BlockPropertyRedstoneSide.NONE;
    }
}
