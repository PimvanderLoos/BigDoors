package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyRedstoneSide implements INamable {

    UP("up"), SIDE("side"), NONE("none");

    private final String name;

    private BlockPropertyRedstoneSide(String s) {
        this.name = s;
    }

    public String toString() {
        return this.getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public boolean a() {
        return this != BlockPropertyRedstoneSide.NONE;
    }
}
