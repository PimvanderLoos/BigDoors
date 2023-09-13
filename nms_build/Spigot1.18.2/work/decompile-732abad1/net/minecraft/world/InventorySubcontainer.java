package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.AutoRecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InventorySubcontainer implements IInventory, AutoRecipeOutput {

    private final int size;
    public final NonNullList<ItemStack> items;
    @Nullable
    private List<IInventoryListener> listeners;

    public InventorySubcontainer(int i) {
        this.size = i;
        this.items = NonNullList.withSize(i, ItemStack.EMPTY);
    }

    public InventorySubcontainer(ItemStack... aitemstack) {
        this.size = aitemstack.length;
        this.items = NonNullList.of(ItemStack.EMPTY, aitemstack);
    }

    public void addListener(IInventoryListener iinventorylistener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }

        this.listeners.add(iinventorylistener);
    }

    public void removeListener(IInventoryListener iinventorylistener) {
        if (this.listeners != null) {
            this.listeners.remove(iinventorylistener);
        }

    }

    @Override
    public ItemStack getItem(int i) {
        return i >= 0 && i < this.items.size() ? (ItemStack) this.items.get(i) : ItemStack.EMPTY;
    }

    public List<ItemStack> removeAllItems() {
        List<ItemStack> list = (List) this.items.stream().filter((itemstack) -> {
            return !itemstack.isEmpty();
        }).collect(Collectors.toList());

        this.clearContent();
        return list;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        ItemStack itemstack = ContainerUtil.removeItem(this.items, i, j);

        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    public ItemStack removeItemType(Item item, int i) {
        ItemStack itemstack = new ItemStack(item, 0);

        for (int j = this.size - 1; j >= 0; --j) {
            ItemStack itemstack1 = this.getItem(j);

            if (itemstack1.getItem().equals(item)) {
                int k = i - itemstack.getCount();
                ItemStack itemstack2 = itemstack1.split(k);

                itemstack.grow(itemstack2.getCount());
                if (itemstack.getCount() == i) {
                    break;
                }
            }
        }

        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    public ItemStack addItem(ItemStack itemstack) {
        ItemStack itemstack1 = itemstack.copy();

        this.moveItemToOccupiedSlotsWithSameType(itemstack1);
        if (itemstack1.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.moveItemToEmptySlots(itemstack1);
            return itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1;
        }
    }

    public boolean canAddItem(ItemStack itemstack) {
        boolean flag = false;
        Iterator iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack1 = (ItemStack) iterator.next();

            if (itemstack1.isEmpty() || ItemStack.isSameItemSameTags(itemstack1, itemstack) && itemstack1.getCount() < itemstack1.getMaxStackSize()) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        ItemStack itemstack = (ItemStack) this.items.get(i);

        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.items.set(i, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        this.items.set(i, itemstack);
        if (!itemstack.isEmpty() && itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    @Override
    public void setChanged() {
        if (this.listeners != null) {
            Iterator iterator = this.listeners.iterator();

            while (iterator.hasNext()) {
                IInventoryListener iinventorylistener = (IInventoryListener) iterator.next();

                iinventorylistener.containerChanged(this);
            }
        }

    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    @Override
    public void fillStackedContents(AutoRecipeStackManager autorecipestackmanager) {
        Iterator iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            autorecipestackmanager.accountStack(itemstack);
        }

    }

    public String toString() {
        return ((List) this.items.stream().filter((itemstack) -> {
            return !itemstack.isEmpty();
        }).collect(Collectors.toList())).toString();
    }

    private void moveItemToEmptySlots(ItemStack itemstack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemstack1 = this.getItem(i);

            if (itemstack1.isEmpty()) {
                this.setItem(i, itemstack.copy());
                itemstack.setCount(0);
                return;
            }
        }

    }

    private void moveItemToOccupiedSlotsWithSameType(ItemStack itemstack) {
        for (int i = 0; i < this.size; ++i) {
            ItemStack itemstack1 = this.getItem(i);

            if (ItemStack.isSameItemSameTags(itemstack1, itemstack)) {
                this.moveItemsBetweenStacks(itemstack, itemstack1);
                if (itemstack.isEmpty()) {
                    return;
                }
            }
        }

    }

    private void moveItemsBetweenStacks(ItemStack itemstack, ItemStack itemstack1) {
        int i = Math.min(this.getMaxStackSize(), itemstack1.getMaxStackSize());
        int j = Math.min(itemstack.getCount(), i - itemstack1.getCount());

        if (j > 0) {
            itemstack1.grow(j);
            itemstack.shrink(j);
            this.setChanged();
        }

    }

    public void fromTag(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = ItemStack.of(nbttaglist.getCompound(i));

            if (!itemstack.isEmpty()) {
                this.addItem(itemstack);
            }
        }

    }

    public NBTTagList createTag() {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);

            if (!itemstack.isEmpty()) {
                nbttaglist.add(itemstack.save(new NBTTagCompound()));
            }
        }

        return nbttaglist;
    }
}
