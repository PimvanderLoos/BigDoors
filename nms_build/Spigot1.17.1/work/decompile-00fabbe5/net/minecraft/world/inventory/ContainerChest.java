package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;

public class ContainerChest extends Container {

    private static final int SLOTS_PER_ROW = 9;
    private final IInventory container;
    private final int containerRows;

    private ContainerChest(Containers<?> containers, int i, PlayerInventory playerinventory, int j) {
        this(containers, i, playerinventory, new InventorySubcontainer(9 * j), j);
    }

    public static ContainerChest a(int i, PlayerInventory playerinventory) {
        return new ContainerChest(Containers.GENERIC_9x1, i, playerinventory, 1);
    }

    public static ContainerChest b(int i, PlayerInventory playerinventory) {
        return new ContainerChest(Containers.GENERIC_9x2, i, playerinventory, 2);
    }

    public static ContainerChest c(int i, PlayerInventory playerinventory) {
        return new ContainerChest(Containers.GENERIC_9x3, i, playerinventory, 3);
    }

    public static ContainerChest d(int i, PlayerInventory playerinventory) {
        return new ContainerChest(Containers.GENERIC_9x4, i, playerinventory, 4);
    }

    public static ContainerChest e(int i, PlayerInventory playerinventory) {
        return new ContainerChest(Containers.GENERIC_9x5, i, playerinventory, 5);
    }

    public static ContainerChest f(int i, PlayerInventory playerinventory) {
        return new ContainerChest(Containers.GENERIC_9x6, i, playerinventory, 6);
    }

    public static ContainerChest a(int i, PlayerInventory playerinventory, IInventory iinventory) {
        return new ContainerChest(Containers.GENERIC_9x3, i, playerinventory, iinventory, 3);
    }

    public static ContainerChest b(int i, PlayerInventory playerinventory, IInventory iinventory) {
        return new ContainerChest(Containers.GENERIC_9x6, i, playerinventory, iinventory, 6);
    }

    public ContainerChest(Containers<?> containers, int i, PlayerInventory playerinventory, IInventory iinventory, int j) {
        super(containers, i);
        a(iinventory, j * 9);
        this.container = iinventory;
        this.containerRows = j;
        iinventory.startOpen(playerinventory.player);
        int k = (this.containerRows - 4) * 18;

        int l;
        int i1;

        for (l = 0; l < this.containerRows; ++l) {
            for (i1 = 0; i1 < 9; ++i1) {
                this.a(new Slot(iinventory, i1 + l * 9, 8 + i1 * 18, 18 + l * 18));
            }
        }

        for (l = 0; l < 3; ++l) {
            for (i1 = 0; i1 < 9; ++i1) {
                this.a(new Slot(playerinventory, i1 + l * 9 + 9, 8 + i1 * 18, 103 + l * 18 + k));
            }
        }

        for (l = 0; l < 9; ++l) {
            this.a(new Slot(playerinventory, l, 8 + l * 18, 161 + k));
        }

    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return this.container.a(entityhuman);
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i < this.containerRows * 9) {
                if (!this.a(itemstack1, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.a(itemstack1, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.d();
            }
        }

        return itemstack;
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.container.closeContainer(entityhuman);
    }

    public IInventory l() {
        return this.container;
    }

    public int m() {
        return this.containerRows;
    }
}
