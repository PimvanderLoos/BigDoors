package net.minecraft.server;

public class WorldProviderNormal extends WorldProvider {

    public WorldProviderNormal() {}

    public DimensionManager getDimensionManager() {
        return DimensionManager.OVERWORLD;
    }

    public boolean c(int i, int j) {
        return !this.b.e(i, j);
    }
}
