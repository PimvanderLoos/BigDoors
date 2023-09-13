package net.minecraft.server;

public interface ITileInventory extends IInventory, ITileEntityContainer {

    boolean isLocked();

    void a(ChestLock chestlock);

    ChestLock getLock();
}
