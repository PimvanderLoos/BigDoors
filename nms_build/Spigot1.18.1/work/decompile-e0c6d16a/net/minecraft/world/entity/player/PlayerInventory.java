package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.INamableTileEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.IBlockData;

public class PlayerInventory implements IInventory, INamableTileEntity {

    public static final int POP_TIME_DURATION = 5;
    public static final int INVENTORY_SIZE = 36;
    private static final int SELECTION_SIZE = 9;
    public static final int SLOT_OFFHAND = 40;
    public static final int NOT_FOUND_INDEX = -1;
    public static final int[] ALL_ARMOR_SLOTS = new int[]{0, 1, 2, 3};
    public static final int[] HELMET_SLOT_ONLY = new int[]{3};
    public final NonNullList<ItemStack> items;
    public final NonNullList<ItemStack> armor;
    public final NonNullList<ItemStack> offhand;
    private final List<NonNullList<ItemStack>> compartments;
    public int selected;
    public final EntityHuman player;
    private int timesChanged;

    public PlayerInventory(EntityHuman entityhuman) {
        this.items = NonNullList.withSize(36, ItemStack.EMPTY);
        this.armor = NonNullList.withSize(4, ItemStack.EMPTY);
        this.offhand = NonNullList.withSize(1, ItemStack.EMPTY);
        this.compartments = ImmutableList.of(this.items, this.armor, this.offhand);
        this.player = entityhuman;
    }

    public ItemStack getSelected() {
        return isHotbarSlot(this.selected) ? (ItemStack) this.items.get(this.selected) : ItemStack.EMPTY;
    }

    public static int getSelectionSize() {
        return 9;
    }

    private boolean hasRemainingSpaceForItem(ItemStack itemstack, ItemStack itemstack1) {
        return !itemstack.isEmpty() && ItemStack.isSameItemSameTags(itemstack, itemstack1) && itemstack.isStackable() && itemstack.getCount() < itemstack.getMaxStackSize() && itemstack.getCount() < this.getMaxStackSize();
    }

    public int getFreeSlot() {
        for (int i = 0; i < this.items.size(); ++i) {
            if (((ItemStack) this.items.get(i)).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public void setPickedItem(ItemStack itemstack) {
        int i = this.findSlotMatchingItem(itemstack);

        if (isHotbarSlot(i)) {
            this.selected = i;
        } else {
            if (i == -1) {
                this.selected = this.getSuitableHotbarSlot();
                if (!((ItemStack) this.items.get(this.selected)).isEmpty()) {
                    int j = this.getFreeSlot();

                    if (j != -1) {
                        this.items.set(j, (ItemStack) this.items.get(this.selected));
                    }
                }

                this.items.set(this.selected, itemstack);
            } else {
                this.pickSlot(i);
            }

        }
    }

    public void pickSlot(int i) {
        this.selected = this.getSuitableHotbarSlot();
        ItemStack itemstack = (ItemStack) this.items.get(this.selected);

        this.items.set(this.selected, (ItemStack) this.items.get(i));
        this.items.set(i, itemstack);
    }

    public static boolean isHotbarSlot(int i) {
        return i >= 0 && i < 9;
    }

    public int findSlotMatchingItem(ItemStack itemstack) {
        for (int i = 0; i < this.items.size(); ++i) {
            if (!((ItemStack) this.items.get(i)).isEmpty() && ItemStack.isSameItemSameTags(itemstack, (ItemStack) this.items.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public int findSlotMatchingUnusedItem(ItemStack itemstack) {
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemstack1 = (ItemStack) this.items.get(i);

            if (!((ItemStack) this.items.get(i)).isEmpty() && ItemStack.isSameItemSameTags(itemstack, (ItemStack) this.items.get(i)) && !((ItemStack) this.items.get(i)).isDamaged() && !itemstack1.isEnchanted() && !itemstack1.hasCustomHoverName()) {
                return i;
            }
        }

        return -1;
    }

    public int getSuitableHotbarSlot() {
        int i;
        int j;

        for (j = 0; j < 9; ++j) {
            i = (this.selected + j) % 9;
            if (((ItemStack) this.items.get(i)).isEmpty()) {
                return i;
            }
        }

        for (j = 0; j < 9; ++j) {
            i = (this.selected + j) % 9;
            if (!((ItemStack) this.items.get(i)).isEnchanted()) {
                return i;
            }
        }

        return this.selected;
    }

    public void swapPaint(double d0) {
        if (d0 > 0.0D) {
            d0 = 1.0D;
        }

        if (d0 < 0.0D) {
            d0 = -1.0D;
        }

        for (this.selected = (int) ((double) this.selected - d0); this.selected < 0; this.selected += 9) {
            ;
        }

        while (this.selected >= 9) {
            this.selected -= 9;
        }

    }

    public int clearOrCountMatchingItems(Predicate<ItemStack> predicate, int i, IInventory iinventory) {
        byte b0 = 0;
        boolean flag = i == 0;
        int j = b0 + ContainerUtil.clearOrCountMatchingItems((IInventory) this, predicate, i - b0, flag);

        j += ContainerUtil.clearOrCountMatchingItems(iinventory, predicate, i - j, flag);
        ItemStack itemstack = this.player.containerMenu.getCarried();

        j += ContainerUtil.clearOrCountMatchingItems(itemstack, predicate, i - j, flag);
        if (itemstack.isEmpty()) {
            this.player.containerMenu.setCarried(ItemStack.EMPTY);
        }

        return j;
    }

    private int addResource(ItemStack itemstack) {
        int i = this.getSlotWithRemainingSpace(itemstack);

        if (i == -1) {
            i = this.getFreeSlot();
        }

        return i == -1 ? itemstack.getCount() : this.addResource(i, itemstack);
    }

    private int addResource(int i, ItemStack itemstack) {
        Item item = itemstack.getItem();
        int j = itemstack.getCount();
        ItemStack itemstack1 = this.getItem(i);

        if (itemstack1.isEmpty()) {
            itemstack1 = new ItemStack(item, 0);
            if (itemstack.hasTag()) {
                itemstack1.setTag(itemstack.getTag().copy());
            }

            this.setItem(i, itemstack1);
        }

        int k = j;

        if (j > itemstack1.getMaxStackSize() - itemstack1.getCount()) {
            k = itemstack1.getMaxStackSize() - itemstack1.getCount();
        }

        if (k > this.getMaxStackSize() - itemstack1.getCount()) {
            k = this.getMaxStackSize() - itemstack1.getCount();
        }

        if (k == 0) {
            return j;
        } else {
            j -= k;
            itemstack1.grow(k);
            itemstack1.setPopTime(5);
            return j;
        }
    }

    public int getSlotWithRemainingSpace(ItemStack itemstack) {
        if (this.hasRemainingSpaceForItem(this.getItem(this.selected), itemstack)) {
            return this.selected;
        } else if (this.hasRemainingSpaceForItem(this.getItem(40), itemstack)) {
            return 40;
        } else {
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.hasRemainingSpaceForItem((ItemStack) this.items.get(i), itemstack)) {
                    return i;
                }
            }

            return -1;
        }
    }

    public void tick() {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            NonNullList<ItemStack> nonnulllist = (NonNullList) iterator.next();

            for (int i = 0; i < nonnulllist.size(); ++i) {
                if (!((ItemStack) nonnulllist.get(i)).isEmpty()) {
                    ((ItemStack) nonnulllist.get(i)).inventoryTick(this.player.level, this.player, i, this.selected == i);
                }
            }
        }

    }

    public boolean add(ItemStack itemstack) {
        return this.add(-1, itemstack);
    }

    public boolean add(int i, ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return false;
        } else {
            try {
                if (itemstack.isDamaged()) {
                    if (i == -1) {
                        i = this.getFreeSlot();
                    }

                    if (i >= 0) {
                        this.items.set(i, itemstack.copy());
                        ((ItemStack) this.items.get(i)).setPopTime(5);
                        itemstack.setCount(0);
                        return true;
                    } else if (this.player.getAbilities().instabuild) {
                        itemstack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int j;

                    do {
                        j = itemstack.getCount();
                        if (i == -1) {
                            itemstack.setCount(this.addResource(itemstack));
                        } else {
                            itemstack.setCount(this.addResource(i, itemstack));
                        }
                    } while (!itemstack.isEmpty() && itemstack.getCount() < j);

                    if (itemstack.getCount() == j && this.player.getAbilities().instabuild) {
                        itemstack.setCount(0);
                        return true;
                    } else {
                        return itemstack.getCount() < j;
                    }
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Item being added");

                crashreportsystemdetails.setDetail("Item ID", (Object) Item.getId(itemstack.getItem()));
                crashreportsystemdetails.setDetail("Item data", (Object) itemstack.getDamageValue());
                crashreportsystemdetails.setDetail("Item name", () -> {
                    return itemstack.getHoverName().getString();
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    public void placeItemBackInInventory(ItemStack itemstack) {
        this.placeItemBackInInventory(itemstack, true);
    }

    public void placeItemBackInInventory(ItemStack itemstack, boolean flag) {
        while (true) {
            if (!itemstack.isEmpty()) {
                int i = this.getSlotWithRemainingSpace(itemstack);

                if (i == -1) {
                    i = this.getFreeSlot();
                }

                if (i != -1) {
                    int j = itemstack.getMaxStackSize() - this.getItem(i).getCount();

                    if (this.add(i, itemstack.split(j)) && flag && this.player instanceof EntityPlayer) {
                        ((EntityPlayer) this.player).connection.send(new PacketPlayOutSetSlot(-2, 0, i, this.getItem(i)));
                    }
                    continue;
                }

                this.player.drop(itemstack, false);
            }

            return;
        }
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        List<ItemStack> list = null;

        NonNullList nonnulllist;

        for (Iterator iterator = this.compartments.iterator(); iterator.hasNext(); i -= nonnulllist.size()) {
            nonnulllist = (NonNullList) iterator.next();
            if (i < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }
        }

        return list != null && !((ItemStack) list.get(i)).isEmpty() ? ContainerUtil.removeItem(list, i, j) : ItemStack.EMPTY;
    }

    public void removeItem(ItemStack itemstack) {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            NonNullList<ItemStack> nonnulllist = (NonNullList) iterator.next();

            for (int i = 0; i < nonnulllist.size(); ++i) {
                if (nonnulllist.get(i) == itemstack) {
                    nonnulllist.set(i, ItemStack.EMPTY);
                    break;
                }
            }
        }

    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        NonNullList<ItemStack> nonnulllist = null;

        NonNullList nonnulllist1;

        for (Iterator iterator = this.compartments.iterator(); iterator.hasNext(); i -= nonnulllist1.size()) {
            nonnulllist1 = (NonNullList) iterator.next();
            if (i < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
        }

        if (nonnulllist != null && !((ItemStack) nonnulllist.get(i)).isEmpty()) {
            ItemStack itemstack = (ItemStack) nonnulllist.get(i);

            nonnulllist.set(i, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        NonNullList<ItemStack> nonnulllist = null;

        NonNullList nonnulllist1;

        for (Iterator iterator = this.compartments.iterator(); iterator.hasNext(); i -= nonnulllist1.size()) {
            nonnulllist1 = (NonNullList) iterator.next();
            if (i < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
        }

        if (nonnulllist != null) {
            nonnulllist.set(i, itemstack);
        }

    }

    public float getDestroySpeed(IBlockData iblockdata) {
        return ((ItemStack) this.items.get(this.selected)).getDestroySpeed(iblockdata);
    }

    public NBTTagList save(NBTTagList nbttaglist) {
        NBTTagCompound nbttagcompound;
        int i;

        for (i = 0; i < this.items.size(); ++i) {
            if (!((ItemStack) this.items.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.putByte("Slot", (byte) i);
                ((ItemStack) this.items.get(i)).save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        for (i = 0; i < this.armor.size(); ++i) {
            if (!((ItemStack) this.armor.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.putByte("Slot", (byte) (i + 100));
                ((ItemStack) this.armor.get(i)).save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        for (i = 0; i < this.offhand.size(); ++i) {
            if (!((ItemStack) this.offhand.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.putByte("Slot", (byte) (i + 150));
                ((ItemStack) this.offhand.get(i)).save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    public void load(NBTTagList nbttaglist) {
        this.items.clear();
        this.armor.clear();
        this.offhand.clear();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.of(nbttagcompound);

            if (!itemstack.isEmpty()) {
                if (j >= 0 && j < this.items.size()) {
                    this.items.set(j, itemstack);
                } else if (j >= 100 && j < this.armor.size() + 100) {
                    this.armor.set(j - 100, itemstack);
                } else if (j >= 150 && j < this.offhand.size() + 150) {
                    this.offhand.set(j - 150, itemstack);
                }
            }
        }

    }

    @Override
    public int getContainerSize() {
        return this.items.size() + this.armor.size() + this.offhand.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                iterator = this.armor.iterator();

                do {
                    if (!iterator.hasNext()) {
                        iterator = this.offhand.iterator();

                        do {
                            if (!iterator.hasNext()) {
                                return true;
                            }

                            itemstack = (ItemStack) iterator.next();
                        } while (itemstack.isEmpty());

                        return false;
                    }

                    itemstack = (ItemStack) iterator.next();
                } while (itemstack.isEmpty());

                return false;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        List<ItemStack> list = null;

        NonNullList nonnulllist;

        for (Iterator iterator = this.compartments.iterator(); iterator.hasNext(); i -= nonnulllist.size()) {
            nonnulllist = (NonNullList) iterator.next();
            if (i < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }
        }

        return list == null ? ItemStack.EMPTY : (ItemStack) list.get(i);
    }

    @Override
    public IChatBaseComponent getName() {
        return new ChatMessage("container.inventory");
    }

    public ItemStack getArmor(int i) {
        return (ItemStack) this.armor.get(i);
    }

    public void hurtArmor(DamageSource damagesource, float f, int[] aint) {
        if (f > 0.0F) {
            f /= 4.0F;
            if (f < 1.0F) {
                f = 1.0F;
            }

            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];
                ItemStack itemstack = (ItemStack) this.armor.get(k);

                if ((!damagesource.isFire() || !itemstack.getItem().isFireResistant()) && itemstack.getItem() instanceof ItemArmor) {
                    itemstack.hurtAndBreak((int) f, this.player, (entityhuman) -> {
                        entityhuman.broadcastBreakEvent(EnumItemSlot.byTypeAndIndex(EnumItemSlot.Function.ARMOR, k));
                    });
                }
            }

        }
    }

    public void dropAll() {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();

            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemstack = (ItemStack) list.get(i);

                if (!itemstack.isEmpty()) {
                    this.player.drop(itemstack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
            }
        }

    }

    @Override
    public void setChanged() {
        ++this.timesChanged;
    }

    public int getTimesChanged() {
        return this.timesChanged;
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.player.isRemoved() ? false : entityhuman.distanceToSqr((Entity) this.player) <= 64.0D;
    }

    public boolean contains(ItemStack itemstack) {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                ItemStack itemstack1 = (ItemStack) iterator1.next();

                if (!itemstack1.isEmpty() && itemstack1.sameItem(itemstack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean contains(Tag<Item> tag) {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator1.next();

                if (!itemstack.isEmpty() && itemstack.is(tag)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void replaceWith(PlayerInventory playerinventory) {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, playerinventory.getItem(i));
        }

        this.selected = playerinventory.selected;
    }

    @Override
    public void clearContent() {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();

            list.clear();
        }

    }

    public void fillStackedContents(AutoRecipeStackManager autorecipestackmanager) {
        Iterator iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            autorecipestackmanager.accountSimpleStack(itemstack);
        }

    }

    public ItemStack removeFromSelected(boolean flag) {
        ItemStack itemstack = this.getSelected();

        return itemstack.isEmpty() ? ItemStack.EMPTY : this.removeItem(this.selected, flag ? itemstack.getCount() : 1);
    }
}
