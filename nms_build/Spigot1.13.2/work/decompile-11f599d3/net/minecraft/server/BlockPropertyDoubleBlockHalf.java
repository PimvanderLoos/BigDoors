package net.minecraft.server;

public enum BlockPropertyDoubleBlockHalf implements INamable {

    UPPER, LOWER;

    private BlockPropertyDoubleBlockHalf() {}

    public String toString() {
        return this.getName();
    }

    public String getName() {
        return this == BlockPropertyDoubleBlockHalf.UPPER ? "upper" : "lower";
    }
}
