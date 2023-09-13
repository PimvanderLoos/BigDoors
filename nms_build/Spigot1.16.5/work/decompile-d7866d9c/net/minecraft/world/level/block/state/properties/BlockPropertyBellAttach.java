package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyBellAttach implements INamable {

    FLOOR("floor"), CEILING("ceiling"), SINGLE_WALL("single_wall"), DOUBLE_WALL("double_wall");

    private final String e;

    private BlockPropertyBellAttach(String s) {
        this.e = s;
    }

    @Override
    public String getName() {
        return this.e;
    }
}
