package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyAttachPosition implements INamable {

    FLOOR("floor"), WALL("wall"), CEILING("ceiling");

    private final String d;

    private BlockPropertyAttachPosition(String s) {
        this.d = s;
    }

    @Override
    public String getName() {
        return this.d;
    }
}
