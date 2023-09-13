package net.minecraft.world.entity;

import java.util.function.Predicate;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;

public interface SlotAccess {

    SlotAccess NULL = new SlotAccess() {
        @Override
        public ItemStack get() {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean set(ItemStack itemstack) {
            return false;
        }
    };

    static SlotAccess forContainer(final IInventory iinventory, final int i, final Predicate<ItemStack> predicate) {
        return new SlotAccess() {
            @Override
            public ItemStack get() {
                return iinventory.getItem(i);
            }

            @Override
            public boolean set(ItemStack itemstack) {
                if (!predicate.test(itemstack)) {
                    return false;
                } else {
                    iinventory.setItem(i, itemstack);
                    return true;
                }
            }
        };
    }

    static SlotAccess forContainer(IInventory iinventory, int i) {
        return forContainer(iinventory, i, (itemstack) -> {
            return true;
        });
    }

    static SlotAccess forEquipmentSlot(final EntityLiving entityliving, final EnumItemSlot enumitemslot, final Predicate<ItemStack> predicate) {
        return new SlotAccess() {
            @Override
            public ItemStack get() {
                return entityliving.getItemBySlot(enumitemslot);
            }

            @Override
            public boolean set(ItemStack itemstack) {
                if (!predicate.test(itemstack)) {
                    return false;
                } else {
                    entityliving.setItemSlot(enumitemslot, itemstack);
                    return true;
                }
            }
        };
    }

    static SlotAccess forEquipmentSlot(EntityLiving entityliving, EnumItemSlot enumitemslot) {
        return forEquipmentSlot(entityliving, enumitemslot, (itemstack) -> {
            return true;
        });
    }

    ItemStack get();

    boolean set(ItemStack itemstack);
}
