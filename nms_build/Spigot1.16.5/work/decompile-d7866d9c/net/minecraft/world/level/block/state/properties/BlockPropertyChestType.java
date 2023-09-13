package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyChestType implements INamable {

    SINGLE("single", 0), LEFT("left", 2), RIGHT("right", 1);

    public static final BlockPropertyChestType[] d = values();
    private final String e;
    private final int f;

    private BlockPropertyChestType(String s, int i) {
        this.e = s;
        this.f = i;
    }

    @Override
    public String getName() {
        return this.e;
    }

    public BlockPropertyChestType b() {
        return BlockPropertyChestType.d[this.f];
    }
}
