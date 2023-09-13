package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;

public class ContainerLectern extends Container {

    private static final int DATA_COUNT = 1;
    private static final int SLOT_COUNT = 1;
    public static final int BUTTON_PREV_PAGE = 1;
    public static final int BUTTON_NEXT_PAGE = 2;
    public static final int BUTTON_TAKE_BOOK = 3;
    public static final int BUTTON_PAGE_JUMP_RANGE_START = 100;
    private final IInventory lectern;
    private final IContainerProperties lecternData;

    public ContainerLectern(int i) {
        this(i, new InventorySubcontainer(1), new ContainerProperties(1));
    }

    public ContainerLectern(int i, IInventory iinventory, IContainerProperties icontainerproperties) {
        super(Containers.LECTERN, i);
        checkContainerSize(iinventory, 1);
        checkContainerDataCount(icontainerproperties, 1);
        this.lectern = iinventory;
        this.lecternData = icontainerproperties;
        this.addSlot(new Slot(iinventory, 0, 0, 0) {
            @Override
            public void setChanged() {
                super.setChanged();
                ContainerLectern.this.slotsChanged(this.container);
            }
        });
        this.addDataSlots(icontainerproperties);
    }

    @Override
    public boolean clickMenuButton(EntityHuman entityhuman, int i) {
        int j;

        if (i >= 100) {
            j = i - 100;
            this.setData(0, j);
            return true;
        } else {
            switch (i) {
                case 1:
                    j = this.lecternData.get(0);
                    this.setData(0, j - 1);
                    return true;
                case 2:
                    j = this.lecternData.get(0);
                    this.setData(0, j + 1);
                    return true;
                case 3:
                    if (!entityhuman.mayBuild()) {
                        return false;
                    }

                    ItemStack itemstack = this.lectern.removeItemNoUpdate(0);

                    this.lectern.setChanged();
                    if (!entityhuman.getInventory().add(itemstack)) {
                        entityhuman.drop(itemstack, false);
                    }

                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setData(int i, int j) {
        super.setData(i, j);
        this.broadcastChanges();
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.lectern.stillValid(entityhuman);
    }

    public ItemStack getBook() {
        return this.lectern.getItem(0);
    }

    public int getPage() {
        return this.lecternData.get(0);
    }
}
