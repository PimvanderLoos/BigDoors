package net.minecraft.server;

public enum BlockPropertyWallHeight implements INamable {

    NONE("none"), LOW("low"), TALL("tall");

    private final String d;

    private BlockPropertyWallHeight(String s) {
        this.d = s;
    }

    public String toString() {
        return this.getName();
    }

    @Override
    public String getName() {
        return this.d;
    }
}
