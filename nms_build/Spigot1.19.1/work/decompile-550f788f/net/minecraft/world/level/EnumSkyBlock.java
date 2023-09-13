package net.minecraft.world.level;

public enum EnumSkyBlock {

    SKY(15), BLOCK(0);

    public final int surrounding;

    private EnumSkyBlock(int i) {
        this.surrounding = i;
    }
}
