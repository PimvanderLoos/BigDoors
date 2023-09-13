package net.minecraft.world;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.item.ItemStack;

public class ContainerUtil {

    public ContainerUtil() {}

    public static ItemStack removeItem(List<ItemStack> list, int i, int j) {
        return i >= 0 && i < list.size() && !((ItemStack) list.get(i)).isEmpty() && j > 0 ? ((ItemStack) list.get(i)).split(j) : ItemStack.EMPTY;
    }

    public static ItemStack takeItem(List<ItemStack> list, int i) {
        return i >= 0 && i < list.size() ? (ItemStack) list.set(i, ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public static NBTTagCompound saveAllItems(NBTTagCompound nbttagcompound, NonNullList<ItemStack> nonnulllist) {
        return saveAllItems(nbttagcompound, nonnulllist, true);
    }

    public static NBTTagCompound saveAllItems(NBTTagCompound nbttagcompound, NonNullList<ItemStack> nonnulllist, boolean flag) {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = (ItemStack) nonnulllist.get(i);

            if (!itemstack.isEmpty()) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.putByte("Slot", (byte) i);
                itemstack.save(nbttagcompound1);
                nbttaglist.add(nbttagcompound1);
            }
        }

        if (!nbttaglist.isEmpty() || flag) {
            nbttagcompound.put("Items", nbttaglist);
        }

        return nbttagcompound;
    }

    public static void loadAllItems(NBTTagCompound nbttagcompound, NonNullList<ItemStack> nonnulllist) {
        NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < nonnulllist.size()) {
                nonnulllist.set(j, ItemStack.of(nbttagcompound1));
            }
        }

    }

    public static int clearOrCountMatchingItems(IInventory iinventory, Predicate<ItemStack> predicate, int i, boolean flag) {
        int j = 0;

        for (int k = 0; k < iinventory.getContainerSize(); ++k) {
            ItemStack itemstack = iinventory.getItem(k);
            int l = clearOrCountMatchingItems(itemstack, predicate, i - j, flag);

            if (l > 0 && !flag && itemstack.isEmpty()) {
                iinventory.setItem(k, ItemStack.EMPTY);
            }

            j += l;
        }

        return j;
    }

    public static int clearOrCountMatchingItems(ItemStack itemstack, Predicate<ItemStack> predicate, int i, boolean flag) {
        if (!itemstack.isEmpty() && predicate.test(itemstack)) {
            if (flag) {
                return itemstack.getCount();
            } else {
                int j = i < 0 ? itemstack.getCount() : Math.min(i, itemstack.getCount());

                itemstack.shrink(j);
                return j;
            }
        } else {
            return 0;
        }
    }
}
