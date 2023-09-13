package net.minecraft.world.entity;

import java.util.function.Predicate;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;

public interface SlotAccess {

    SlotAccess NULL = new SlotAccess() {
        @Override
        public ItemStack a() {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean a(ItemStack itemstack) {
            return false;
        }
    };

    static SlotAccess a(final IInventory iinventory, final int i, final Predicate<ItemStack> predicate) {
        return new SlotAccess() {
            @Override
            public ItemStack a() {
                return iinventory.getItem(i);
            }

            @Override
            public boolean a(ItemStack itemstack) {
                if (!predicate.test(itemstack)) {
                    return false;
                } else {
                    iinventory.setItem(i, itemstack);
                    return true;
                }
            }
        };
    }

    static SlotAccess a(IInventory iinventory, int i) {
        return a(iinventory, i, (itemstack) -> {
            return true;
        });
    }

    static SlotAccess a(final EntityLiving entityliving, final EnumItemSlot enumitemslot, final Predicate<ItemStack> predicate) {
        return new SlotAccess() {
            @Override
            public ItemStack a() {
                return entityliving.getEquipment(enumitemslot);
            }

            @Override
            public boolean a(ItemStack itemstack) {
                if (!predicate.test(itemstack)) {
                    return false;
                } else {
                    entityliving.setSlot(enumitemslot, itemstack);
                    return true;
                }
            }
        };
    }

    static SlotAccess a(EntityLiving entityliving, EnumItemSlot enumitemslot) {
        return a(entityliving, enumitemslot, (itemstack) -> {
            return true;
        });
    }

    ItemStack a();

    boolean a(ItemStack itemstack);
}
