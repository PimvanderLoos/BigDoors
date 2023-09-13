package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyRedstoneSide implements INamable {

    UP("up"), SIDE("side"), NONE("none");

    private final String d;

    private BlockPropertyRedstoneSide(String s) {
        this.d = s;
    }

    public String toString() {
        return this.getName();
    }

    @Override
    public String getName() {
        return this.d;
    }

    public boolean b() {
        return this != BlockPropertyRedstoneSide.NONE;
    }
}
