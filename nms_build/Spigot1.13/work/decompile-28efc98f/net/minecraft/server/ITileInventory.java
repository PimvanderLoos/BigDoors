package net.minecraft.server;

public interface ITileInventory extends IInventory, ITileEntityContainer {

    boolean isLocked();

    void setLock(ChestLock chestlock);

    ChestLock getLock();
}
