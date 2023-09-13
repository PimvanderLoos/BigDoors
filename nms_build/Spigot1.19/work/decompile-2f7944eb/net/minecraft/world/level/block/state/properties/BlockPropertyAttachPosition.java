package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyAttachPosition implements INamable {

    FLOOR("floor"), WALL("wall"), CEILING("ceiling");

    private final String name;

    private BlockPropertyAttachPosition(String s) {
        this.name = s;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
