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
        this.items = NonNullList.a(36, ItemStack.EMPTY);
        this.armor = NonNullList.a(4, ItemStack.EMPTY);
        this.offhand = NonNullList.a(1, ItemStack.EMPTY);
        this.compartments = ImmutableList.of(this.items, this.armor, this.offhand);
        this.player = entityhuman;
    }

    public ItemStack getItemInHand() {
        return d(this.selected) ? (ItemStack) this.items.get(this.selected) : ItemStack.EMPTY;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private boolean isSimilarAndNotFull(ItemStack itemstack, ItemStack itemstack1) {
        return !itemstack.isEmpty() && ItemStack.e(itemstack, itemstack1) && itemstack.isStackable() && itemstack.getCount() < itemstack.getMaxStackSize() && itemstack.getCount() < this.getMaxStackSize();
    }

    public int getFirstEmptySlotIndex() {
        for (int i = 0; i < this.items.size(); ++i) {
            if (((ItemStack) this.items.get(i)).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public void a(ItemStack itemstack) {
        int i = this.b(itemstack);

        if (d(i)) {
            this.selected = i;
        } else {
            if (i == -1) {
                this.selected = this.i();
                if (!((ItemStack) this.items.get(this.selected)).isEmpty()) {
                    int j = this.getFirstEmptySlotIndex();

                    if (j != -1) {
                        this.items.set(j, (ItemStack) this.items.get(this.selected));
                    }
                }

                this.items.set(this.selected, itemstack);
            } else {
                this.c(i);
            }

        }
    }

    public void c(int i) {
        this.selected = this.i();
        ItemStack itemstack = (ItemStack) this.items.get(this.selected);

        this.items.set(this.selected, (ItemStack) this.items.get(i));
        this.items.set(i, itemstack);
    }

    public static boolean d(int i) {
        return i >= 0 && i < 9;
    }

    public int b(ItemStack itemstack) {
        for (int i = 0; i < this.items.size(); ++i) {
            if (!((ItemStack) this.items.get(i)).isEmpty() && ItemStack.e(itemstack, (ItemStack) this.items.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public int c(ItemStack itemstack) {
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemstack1 = (ItemStack) this.items.get(i);

            if (!((ItemStack) this.items.get(i)).isEmpty() && ItemStack.e(itemstack, (ItemStack) this.items.get(i)) && !((ItemStack) this.items.get(i)).g() && !itemstack1.hasEnchantments() && !itemstack1.hasName()) {
                return i;
            }
        }

        return -1;
    }

    public int i() {
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
            if (!((ItemStack) this.items.get(i)).hasEnchantments()) {
                return i;
            }
        }

        return this.selected;
    }

    public void a(double d0) {
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

    public int a(Predicate<ItemStack> predicate, int i, IInventory iinventory) {
        byte b0 = 0;
        boolean flag = i == 0;
        int j = b0 + ContainerUtil.a((IInventory) this, predicate, i - b0, flag);

        j += ContainerUtil.a(iinventory, predicate, i - j, flag);
        ItemStack itemstack = this.player.containerMenu.getCarried();

        j += ContainerUtil.a(itemstack, predicate, i - j, flag);
        if (itemstack.isEmpty()) {
            this.player.containerMenu.setCarried(ItemStack.EMPTY);
        }

        return j;
    }

    private int i(ItemStack itemstack) {
        int i = this.firstPartial(itemstack);

        if (i == -1) {
            i = this.getFirstEmptySlotIndex();
        }

        return i == -1 ? itemstack.getCount() : this.d(i, itemstack);
    }

    private int d(int i, ItemStack itemstack) {
        Item item = itemstack.getItem();
        int j = itemstack.getCount();
        ItemStack itemstack1 = this.getItem(i);

        if (itemstack1.isEmpty()) {
            itemstack1 = new ItemStack(item, 0);
            if (itemstack.hasTag()) {
                itemstack1.setTag(itemstack.getTag().clone());
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
            itemstack1.add(k);
            itemstack1.d(5);
            return j;
        }
    }

    public int firstPartial(ItemStack itemstack) {
        if (this.isSimilarAndNotFull(this.getItem(this.selected), itemstack)) {
            return this.selected;
        } else if (this.isSimilarAndNotFull(this.getItem(40), itemstack)) {
            return 40;
        } else {
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.isSimilarAndNotFull((ItemStack) this.items.get(i), itemstack)) {
                    return i;
                }
            }

            return -1;
        }
    }

    public void j() {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            NonNullList<ItemStack> nonnulllist = (NonNullList) iterator.next();

            for (int i = 0; i < nonnulllist.size(); ++i) {
                if (!((ItemStack) nonnulllist.get(i)).isEmpty()) {
                    ((ItemStack) nonnulllist.get(i)).a(this.player.level, this.player, i, this.selected == i);
                }
            }
        }

    }

    public boolean pickup(ItemStack itemstack) {
        return this.c(-1, itemstack);
    }

    public boolean c(int i, ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return false;
        } else {
            try {
                if (itemstack.g()) {
                    if (i == -1) {
                        i = this.getFirstEmptySlotIndex();
                    }

                    if (i >= 0) {
                        this.items.set(i, itemstack.cloneItemStack());
                        ((ItemStack) this.items.get(i)).d(5);
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
                            itemstack.setCount(this.i(itemstack));
                        } else {
                            itemstack.setCount(this.d(i, itemstack));
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
                CrashReport crashreport = CrashReport.a(throwable, "Adding item to inventory");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Item being added");

                crashreportsystemdetails.a("Item ID", (Object) Item.getId(itemstack.getItem()));
                crashreportsystemdetails.a("Item data", (Object) itemstack.getDamage());
                crashreportsystemdetails.a("Item name", () -> {
                    return itemstack.getName().getString();
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    public void f(ItemStack itemstack) {
        this.a(itemstack, true);
    }

    public void a(ItemStack itemstack, boolean flag) {
        while (true) {
            if (!itemstack.isEmpty()) {
                int i = this.firstPartial(itemstack);

                if (i == -1) {
                    i = this.getFirstEmptySlotIndex();
                }

                if (i != -1) {
                    int j = itemstack.getMaxStackSize() - this.getItem(i).getCount();

                    if (this.c(i, itemstack.cloneAndSubtract(j)) && flag && this.player instanceof EntityPlayer) {
                        ((EntityPlayer) this.player).connection.sendPacket(new PacketPlayOutSetSlot(-2, 0, i, this.getItem(i)));
                    }
                    continue;
                }

                this.player.drop(itemstack, false);
            }

            return;
        }
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        List<ItemStack> list = null;

        NonNullList nonnulllist;

        for (Iterator iterator = this.compartments.iterator(); iterator.hasNext(); i -= nonnulllist.size()) {
            nonnulllist = (NonNullList) iterator.next();
            if (i < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }
        }

        return list != null && !((ItemStack) list.get(i)).isEmpty() ? ContainerUtil.a(list, i, j) : ItemStack.EMPTY;
    }

    public void g(ItemStack itemstack) {
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
    public ItemStack splitWithoutUpdate(int i) {
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

    public float a(IBlockData iblockdata) {
        return ((ItemStack) this.items.get(this.selected)).a(iblockdata);
    }

    public NBTTagList a(NBTTagList nbttaglist) {
        NBTTagCompound nbttagcompound;
        int i;

        for (i = 0; i < this.items.size(); ++i) {
            if (!((ItemStack) this.items.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                ((ItemStack) this.items.get(i)).save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        for (i = 0; i < this.armor.size(); ++i) {
            if (!((ItemStack) this.armor.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) (i + 100));
                ((ItemStack) this.armor.get(i)).save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        for (i = 0; i < this.offhand.size(); ++i) {
            if (!((ItemStack) this.offhand.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) (i + 150));
                ((ItemStack) this.offhand.get(i)).save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    public void b(NBTTagList nbttaglist) {
        this.items.clear();
        this.armor.clear();
        this.offhand.clear();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.a(nbttagcompound);

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
    public int getSize() {
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
    public IChatBaseComponent getDisplayName() {
        return new ChatMessage("container.inventory");
    }

    public ItemStack e(int i) {
        return (ItemStack) this.armor.get(i);
    }

    public void a(DamageSource damagesource, float f, int[] aint) {
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

                if ((!damagesource.isFire() || !itemstack.getItem().w()) && itemstack.getItem() instanceof ItemArmor) {
                    itemstack.damage((int) f, this.player, (entityhuman) -> {
                        entityhuman.broadcastItemBreak(EnumItemSlot.a(EnumItemSlot.Function.ARMOR, k));
                    });
                }
            }

        }
    }

    public void dropContents() {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();

            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemstack = (ItemStack) list.get(i);

                if (!itemstack.isEmpty()) {
                    this.player.a(itemstack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
            }
        }

    }

    @Override
    public void update() {
        ++this.timesChanged;
    }

    public int l() {
        return this.timesChanged;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return this.player.isRemoved() ? false : entityhuman.f((Entity) this.player) <= 64.0D;
    }

    public boolean h(ItemStack itemstack) {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                ItemStack itemstack1 = (ItemStack) iterator1.next();

                if (!itemstack1.isEmpty() && itemstack1.doMaterialsMatch(itemstack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean a(Tag<Item> tag) {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator1.next();

                if (!itemstack.isEmpty() && itemstack.a(tag)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void a(PlayerInventory playerinventory) {
        for (int i = 0; i < this.getSize(); ++i) {
            this.setItem(i, playerinventory.getItem(i));
        }

        this.selected = playerinventory.selected;
    }

    @Override
    public void clear() {
        Iterator iterator = this.compartments.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();

            list.clear();
        }

    }

    public void a(AutoRecipeStackManager autorecipestackmanager) {
        Iterator iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            autorecipestackmanager.a(itemstack);
        }

    }

    public ItemStack a(boolean flag) {
        ItemStack itemstack = this.getItemInHand();

        return itemstack.isEmpty() ? ItemStack.EMPTY : this.splitStack(this.selected, flag ? itemstack.getCount() : 1);
    }
}
