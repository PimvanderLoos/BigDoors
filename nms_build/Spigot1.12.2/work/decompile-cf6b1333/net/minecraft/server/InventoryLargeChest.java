package net.minecraft.server;

public class InventoryLargeChest implements ITileInventory {

    private final String a;
    public final ITileInventory left;
    public final ITileInventory right;

    public InventoryLargeChest(String s, ITileInventory itileinventory, ITileInventory itileinventory1) {
        this.a = s;
        if (itileinventory == null) {
            itileinventory = itileinventory1;
        }

        if (itileinventory1 == null) {
            itileinventory1 = itileinventory;
        }

        this.left = itileinventory;
        this.right = itileinventory1;
        if (itileinventory.isLocked()) {
            itileinventory1.setLock(itileinventory.getLock());
        } else if (itileinventory1.isLocked()) {
            itileinventory.setLock(itileinventory1.getLock());
        }

    }

    public int getSize() {
        return this.left.getSize() + this.right.getSize();
    }

    public boolean x_() {
        return this.left.x_() && this.right.x_();
    }

    public boolean a(IInventory iinventory) {
        return this.left == iinventory || this.right == iinventory;
    }

    public String getName() {
        return this.left.hasCustomName() ? this.left.getName() : (this.right.hasCustomName() ? this.right.getName() : this.a);
    }

    public boolean hasCustomName() {
        return this.left.hasCustomName() || this.right.hasCustomName();
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return (IChatBaseComponent) (this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName(), new Object[0]));
    }

    public ItemStack getItem(int i) {
        return i >= this.left.getSize() ? this.right.getItem(i - this.left.getSize()) : this.left.getItem(i);
    }

    public ItemStack splitStack(int i, int j) {
        return i >= this.left.getSize() ? this.right.splitStack(i - this.left.getSize(), j) : this.left.splitStack(i, j);
    }

    public ItemStack splitWithoutUpdate(int i) {
        return i >= this.left.getSize() ? this.right.splitWithoutUpdate(i - this.left.getSize()) : this.left.splitWithoutUpdate(i);
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i >= this.left.getSize()) {
            this.right.setItem(i - this.left.getSize(), itemstack);
        } else {
            this.left.setItem(i, itemstack);
        }

    }

    public int getMaxStackSize() {
        return this.left.getMaxStackSize();
    }

    public void update() {
        this.left.update();
        this.right.update();
    }

    public boolean a(EntityHuman entityhuman) {
        return this.left.a(entityhuman) && this.right.a(entityhuman);
    }

    public void startOpen(EntityHuman entityhuman) {
        this.left.startOpen(entityhuman);
        this.right.startOpen(entityhuman);
    }

    public void closeContainer(EntityHuman entityhuman) {
        this.left.closeContainer(entityhuman);
        this.right.closeContainer(entityhuman);
    }

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {}

    public int h() {
        return 0;
    }

    public boolean isLocked() {
        return this.left.isLocked() || this.right.isLocked();
    }

    public void setLock(ChestLock chestlock) {
        this.left.setLock(chestlock);
        this.right.setLock(chestlock);
    }

    public ChestLock getLock() {
        return this.left.getLock();
    }

    public String getContainerName() {
        return this.left.getContainerName();
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerChest(playerinventory, this, entityhuman);
    }

    public void clear() {
        this.left.clear();
        this.right.clear();
    }
}
