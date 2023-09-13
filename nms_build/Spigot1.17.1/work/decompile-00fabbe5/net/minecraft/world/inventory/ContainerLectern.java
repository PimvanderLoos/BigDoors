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
        a(iinventory, 1);
        a(icontainerproperties, 1);
        this.lectern = iinventory;
        this.lecternData = icontainerproperties;
        this.a(new Slot(iinventory, 0, 0, 0) {
            @Override
            public void d() {
                super.d();
                ContainerLectern.this.a(this.container);
            }
        });
        this.a(icontainerproperties);
    }

    @Override
    public boolean a(EntityHuman entityhuman, int i) {
        int j;

        if (i >= 100) {
            j = i - 100;
            this.setContainerData(0, j);
            return true;
        } else {
            switch (i) {
                case 1:
                    j = this.lecternData.getProperty(0);
                    this.setContainerData(0, j - 1);
                    return true;
                case 2:
                    j = this.lecternData.getProperty(0);
                    this.setContainerData(0, j + 1);
                    return true;
                case 3:
                    if (!entityhuman.fv()) {
                        return false;
                    }

                    ItemStack itemstack = this.lectern.splitWithoutUpdate(0);

                    this.lectern.update();
                    if (!entityhuman.getInventory().pickup(itemstack)) {
                        entityhuman.drop(itemstack, false);
                    }

                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public void setContainerData(int i, int j) {
        super.setContainerData(i, j);
        this.d();
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return this.lectern.a(entityhuman);
    }

    public ItemStack l() {
        return this.lectern.getItem(0);
    }

    public int m() {
        return this.lecternData.getProperty(0);
    }
}
