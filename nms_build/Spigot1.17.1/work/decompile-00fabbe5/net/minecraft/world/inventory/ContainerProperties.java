package net.minecraft.world.inventory;

public class ContainerProperties implements IContainerProperties {

    private final int[] ints;

    public ContainerProperties(int i) {
        this.ints = new int[i];
    }

    @Override
    public int getProperty(int i) {
        return this.ints[i];
    }

    @Override
    public void setProperty(int i, int j) {
        this.ints[i] = j;
    }

    @Override
    public int a() {
        return this.ints.length;
    }
}
