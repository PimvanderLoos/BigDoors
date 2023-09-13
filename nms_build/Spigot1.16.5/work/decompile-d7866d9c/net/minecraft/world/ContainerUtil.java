package net.minecraft.world;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.item.ItemStack;

public class ContainerUtil {

    public static ItemStack a(List<ItemStack> list, int i, int j) {
        return i >= 0 && i < list.size() && !((ItemStack) list.get(i)).isEmpty() && j > 0 ? ((ItemStack) list.get(i)).cloneAndSubtract(j) : ItemStack.b;
    }

    public static ItemStack a(List<ItemStack> list, int i) {
        return i >= 0 && i < list.size() ? (ItemStack) list.set(i, ItemStack.b) : ItemStack.b;
    }

    public static NBTTagCompound a(NBTTagCompound nbttagcompound, NonNullList<ItemStack> nonnulllist) {
        return a(nbttagcompound, nonnulllist, true);
    }

    public static NBTTagCompound a(NBTTagCompound nbttagcompound, NonNullList<ItemStack> nonnulllist, boolean flag) {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = (ItemStack) nonnulllist.get(i);

            if (!itemstack.isEmpty()) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setByte("Slot", (byte) i);
                itemstack.save(nbttagcompound1);
                nbttaglist.add(nbttagcompound1);
            }
        }

        if (!nbttaglist.isEmpty() || flag) {
            nbttagcompound.set("Items", nbttaglist);
        }

        return nbttagcompound;
    }

    public static void b(NBTTagCompound nbttagcompound, NonNullList<ItemStack> nonnulllist) {
        NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < nonnulllist.size()) {
                nonnulllist.set(j, ItemStack.a(nbttagcompound1));
            }
        }

    }

    public static int a(IInventory iinventory, Predicate<ItemStack> predicate, int i, boolean flag) {
        int j = 0;

        for (int k = 0; k < iinventory.getSize(); ++k) {
            ItemStack itemstack = iinventory.getItem(k);
            int l = a(itemstack, predicate, i - j, flag);

            if (l > 0 && !flag && itemstack.isEmpty()) {
                iinventory.setItem(k, ItemStack.b);
            }

            j += l;
        }

        return j;
    }

    public static int a(ItemStack itemstack, Predicate<ItemStack> predicate, int i, boolean flag) {
        if (!itemstack.isEmpty() && predicate.test(itemstack)) {
            if (flag) {
                return itemstack.getCount();
            } else {
                int j = i < 0 ? itemstack.getCount() : Math.min(i, itemstack.getCount());

                itemstack.subtract(j);
                return j;
            }
        } else {
            return 0;
        }
    }
}
