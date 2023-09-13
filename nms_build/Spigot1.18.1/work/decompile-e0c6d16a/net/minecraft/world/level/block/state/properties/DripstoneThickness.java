package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum DripstoneThickness implements INamable {

    TIP_MERGE("tip_merge"), TIP("tip"), FRUSTUM("frustum"), MIDDLE("middle"), BASE("base");

    private final String name;

    private DripstoneThickness(String s) {
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
