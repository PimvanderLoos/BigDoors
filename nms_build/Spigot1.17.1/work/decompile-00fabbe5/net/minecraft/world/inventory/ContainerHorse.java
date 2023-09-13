package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.animal.horse.EntityHorseChestedAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ContainerHorse extends Container {

    private final IInventory horseContainer;
    private final EntityHorseAbstract horse;

    public ContainerHorse(int i, PlayerInventory playerinventory, IInventory iinventory, final EntityHorseAbstract entityhorseabstract) {
        super((Containers) null, i);
        this.horseContainer = iinventory;
        this.horse = entityhorseabstract;
        boolean flag = true;

        iinventory.startOpen(playerinventory.player);
        boolean flag1 = true;

        this.a(new Slot(iinventory, 0, 8, 18) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.a(Items.SADDLE) && !this.hasItem() && entityhorseabstract.canSaddle();
            }

            @Override
            public boolean b() {
                return entityhorseabstract.canSaddle();
            }
        });
        this.a(new Slot(iinventory, 1, 8, 36) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return entityhorseabstract.m(itemstack);
            }

            @Override
            public boolean b() {
                return entityhorseabstract.gc();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        int j;
        int k;

        if (this.a(entityhorseabstract)) {
            for (j = 0; j < 3; ++j) {
                for (k = 0; k < ((EntityHorseChestedAbstract) entityhorseabstract).fE(); ++k) {
                    this.a(new Slot(iinventory, 2 + k + j * ((EntityHorseChestedAbstract) entityhorseabstract).fE(), 80 + k * 18, 18 + j * 18));
                }
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.a(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 102 + j * 18 + -18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return !this.horse.b(this.horseContainer) && this.horseContainer.a(entityhuman) && this.horse.isAlive() && this.horse.e((Entity) entityhuman) < 8.0F;
    }

    private boolean a(EntityHorseAbstract entityhorseabstract) {
        return entityhorseabstract instanceof EntityHorseChestedAbstract && ((EntityHorseChestedAbstract) entityhorseabstract).isCarryingChest();
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            int j = this.horseContainer.getSize();

            if (i < j) {
                if (!this.a(itemstack1, j, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).isAllowed(itemstack1) && !this.getSlot(1).hasItem()) {
                if (!this.a(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).isAllowed(itemstack1)) {
                if (!this.a(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (j <= 2 || !this.a(itemstack1, 2, j, false)) {
                int k = j + 27;
                int l = k + 9;

                if (i >= k && i < l) {
                    if (!this.a(itemstack1, j, k, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= j && i < k) {
                    if (!this.a(itemstack1, k, l, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.a(itemstack1, k, k, false)) {
                    return ItemStack.EMPTY;
                }

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
        this.horseContainer.closeContainer(entityhuman);
    }
}
