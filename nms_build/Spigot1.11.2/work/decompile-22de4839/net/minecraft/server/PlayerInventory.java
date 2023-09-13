package net.minecraft.server;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class PlayerInventory implements IInventory {

    public final NonNullList<ItemStack> items;
    public final NonNullList<ItemStack> armor;
    public final NonNullList<ItemStack> extraSlots;
    private final List<NonNullList<ItemStack>> g;
    public int itemInHandIndex;
    public EntityHuman player;
    private ItemStack carried;
    public boolean f;

    public PlayerInventory(EntityHuman entityhuman) {
        this.items = NonNullList.a(36, ItemStack.a);
        this.armor = NonNullList.a(4, ItemStack.a);
        this.extraSlots = NonNullList.a(1, ItemStack.a);
        this.g = Arrays.asList(new NonNullList[] { this.items, this.armor, this.extraSlots});
        this.carried = ItemStack.a;
        this.player = entityhuman;
    }

    public ItemStack getItemInHand() {
        return e(this.itemInHandIndex) ? (ItemStack) this.items.get(this.itemInHandIndex) : ItemStack.a;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return !itemstack.isEmpty() && this.b(itemstack, itemstack1) && itemstack.isStackable() && itemstack.getCount() < itemstack.getMaxStackSize() && itemstack.getCount() < this.getMaxStackSize();
    }

    private boolean b(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.getItem() == itemstack1.getItem() && (!itemstack.usesData() || itemstack.getData() == itemstack1.getData()) && ItemStack.equals(itemstack, itemstack1);
    }

    public int getFirstEmptySlotIndex() {
        for (int i = 0; i < this.items.size(); ++i) {
            if (((ItemStack) this.items.get(i)).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public void d(int i) {
        this.itemInHandIndex = this.l();
        ItemStack itemstack = (ItemStack) this.items.get(this.itemInHandIndex);

        this.items.set(this.itemInHandIndex, this.items.get(i));
        this.items.set(i, itemstack);
    }

    public static boolean e(int i) {
        return i >= 0 && i < 9;
    }

    public int l() {
        int i;
        int j;

        for (i = 0; i < 9; ++i) {
            j = (this.itemInHandIndex + i) % 9;
            if (((ItemStack) this.items.get(j)).isEmpty()) {
                return j;
            }
        }

        for (i = 0; i < 9; ++i) {
            j = (this.itemInHandIndex + i) % 9;
            if (!((ItemStack) this.items.get(j)).hasEnchantments()) {
                return j;
            }
        }

        return this.itemInHandIndex;
    }

    public int a(@Nullable Item item, int i, int j, @Nullable NBTTagCompound nbttagcompound) {
        int k = 0;

        int l;

        for (l = 0; l < this.getSize(); ++l) {
            ItemStack itemstack = this.getItem(l);

            if (!itemstack.isEmpty() && (item == null || itemstack.getItem() == item) && (i <= -1 || itemstack.getData() == i) && (nbttagcompound == null || GameProfileSerializer.a(nbttagcompound, itemstack.getTag(), true))) {
                int i1 = j <= 0 ? itemstack.getCount() : Math.min(j - k, itemstack.getCount());

                k += i1;
                if (j != 0) {
                    itemstack.subtract(i1);
                    if (itemstack.isEmpty()) {
                        this.setItem(l, ItemStack.a);
                    }

                    if (j > 0 && k >= j) {
                        return k;
                    }
                }
            }
        }

        if (!this.carried.isEmpty()) {
            if (item != null && this.carried.getItem() != item) {
                return k;
            }

            if (i > -1 && this.carried.getData() != i) {
                return k;
            }

            if (nbttagcompound != null && !GameProfileSerializer.a(nbttagcompound, this.carried.getTag(), true)) {
                return k;
            }

            l = j <= 0 ? this.carried.getCount() : Math.min(j - k, this.carried.getCount());
            k += l;
            if (j != 0) {
                this.carried.subtract(l);
                if (this.carried.isEmpty()) {
                    this.carried = ItemStack.a;
                }

                if (j > 0 && k >= j) {
                    return k;
                }
            }
        }

        return k;
    }

    private int g(ItemStack itemstack) {
        Item item = itemstack.getItem();
        int i = itemstack.getCount();
        int j = this.firstPartial(itemstack);

        if (j == -1) {
            j = this.getFirstEmptySlotIndex();
        }

        if (j == -1) {
            return i;
        } else {
            ItemStack itemstack1 = this.getItem(j);

            if (itemstack1.isEmpty()) {
                itemstack1 = new ItemStack(item, 0, itemstack.getData());
                if (itemstack.hasTag()) {
                    itemstack1.setTag(itemstack.getTag().g());
                }

                this.setItem(j, itemstack1);
            }

            int k = i;

            if (i > itemstack1.getMaxStackSize() - itemstack1.getCount()) {
                k = itemstack1.getMaxStackSize() - itemstack1.getCount();
            }

            if (k > this.getMaxStackSize() - itemstack1.getCount()) {
                k = this.getMaxStackSize() - itemstack1.getCount();
            }

            if (k == 0) {
                return i;
            } else {
                i -= k;
                itemstack1.add(k);
                itemstack1.d(5);
                return i;
            }
        }
    }

    private int firstPartial(ItemStack itemstack) {
        if (this.a(this.getItem(this.itemInHandIndex), itemstack)) {
            return this.itemInHandIndex;
        } else if (this.a(this.getItem(40), itemstack)) {
            return 40;
        } else {
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.a((ItemStack) this.items.get(i), itemstack)) {
                    return i;
                }
            }

            return -1;
        }
    }

    public void n() {
        Iterator iterator = this.g.iterator();

        while (iterator.hasNext()) {
            NonNullList nonnulllist = (NonNullList) iterator.next();

            for (int i = 0; i < nonnulllist.size(); ++i) {
                if (!((ItemStack) nonnulllist.get(i)).isEmpty()) {
                    ((ItemStack) nonnulllist.get(i)).a(this.player.world, this.player, i, this.itemInHandIndex == i);
                }
            }
        }

    }

    public boolean pickup(final ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return false;
        } else {
            try {
                int i;

                if (itemstack.h()) {
                    i = this.getFirstEmptySlotIndex();
                    if (i >= 0) {
                        this.items.set(i, itemstack.cloneItemStack());
                        ((ItemStack) this.items.get(i)).d(5);
                        itemstack.setCount(0);
                        return true;
                    } else if (this.player.abilities.canInstantlyBuild) {
                        itemstack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    do {
                        i = itemstack.getCount();
                        itemstack.setCount(this.g(itemstack));
                    } while (!itemstack.isEmpty() && itemstack.getCount() < i);

                    if (itemstack.getCount() == i && this.player.abilities.canInstantlyBuild) {
                        itemstack.setCount(0);
                        return true;
                    } else {
                        return itemstack.getCount() < i;
                    }
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Adding item to inventory");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Item being added");

                crashreportsystemdetails.a("Item ID", (Object) Integer.valueOf(Item.getId(itemstack.getItem())));
                crashreportsystemdetails.a("Item data", (Object) Integer.valueOf(itemstack.getData()));
                crashreportsystemdetails.a("Item name", new CrashReportCallable() {
                    public String a() throws Exception {
                        return itemstack.getName();
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    public ItemStack splitStack(int i, int j) {
        NonNullList nonnulllist = null;

        NonNullList nonnulllist1;

        for (Iterator iterator = this.g.iterator(); iterator.hasNext(); i -= nonnulllist1.size()) {
            nonnulllist1 = (NonNullList) iterator.next();
            if (i < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
        }

        return nonnulllist != null && !((ItemStack) nonnulllist.get(i)).isEmpty() ? ContainerUtil.a(nonnulllist, i, j) : ItemStack.a;
    }

    public void d(ItemStack itemstack) {
        Iterator iterator = this.g.iterator();

        while (iterator.hasNext()) {
            NonNullList nonnulllist = (NonNullList) iterator.next();

            for (int i = 0; i < nonnulllist.size(); ++i) {
                if (nonnulllist.get(i) == itemstack) {
                    nonnulllist.set(i, ItemStack.a);
                    break;
                }
            }
        }

    }

    public ItemStack splitWithoutUpdate(int i) {
        NonNullList nonnulllist = null;

        NonNullList nonnulllist1;

        for (Iterator iterator = this.g.iterator(); iterator.hasNext(); i -= nonnulllist1.size()) {
            nonnulllist1 = (NonNullList) iterator.next();
            if (i < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
        }

        if (nonnulllist != null && !((ItemStack) nonnulllist.get(i)).isEmpty()) {
            ItemStack itemstack = (ItemStack) nonnulllist.get(i);

            nonnulllist.set(i, ItemStack.a);
            return itemstack;
        } else {
            return ItemStack.a;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        NonNullList nonnulllist = null;

        NonNullList nonnulllist1;

        for (Iterator iterator = this.g.iterator(); iterator.hasNext(); i -= nonnulllist1.size()) {
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
        float f = 1.0F;

        if (!((ItemStack) this.items.get(this.itemInHandIndex)).isEmpty()) {
            f *= ((ItemStack) this.items.get(this.itemInHandIndex)).a(iblockdata);
        }

        return f;
    }

    public NBTTagList a(NBTTagList nbttaglist) {
        int i;
        NBTTagCompound nbttagcompound;

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

        for (i = 0; i < this.extraSlots.size(); ++i) {
            if (!((ItemStack) this.extraSlots.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) (i + 150));
                ((ItemStack) this.extraSlots.get(i)).save(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }

        return nbttaglist;
    }

    public void b(NBTTagList nbttaglist) {
        this.items.clear();
        this.armor.clear();
        this.extraSlots.clear();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.get(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = new ItemStack(nbttagcompound);

            if (!itemstack.isEmpty()) {
                if (j >= 0 && j < this.items.size()) {
                    this.items.set(j, itemstack);
                } else if (j >= 100 && j < this.armor.size() + 100) {
                    this.armor.set(j - 100, itemstack);
                } else if (j >= 150 && j < this.extraSlots.size() + 150) {
                    this.extraSlots.set(j - 150, itemstack);
                }
            }
        }

    }

    public int getSize() {
        return this.items.size() + this.armor.size() + this.extraSlots.size();
    }

    public boolean w_() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                iterator = this.armor.iterator();

                do {
                    if (!iterator.hasNext()) {
                        iterator = this.extraSlots.iterator();

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

    public ItemStack getItem(int i) {
        NonNullList nonnulllist = null;

        NonNullList nonnulllist1;

        for (Iterator iterator = this.g.iterator(); iterator.hasNext(); i -= nonnulllist1.size()) {
            nonnulllist1 = (NonNullList) iterator.next();
            if (i < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
        }

        return nonnulllist == null ? ItemStack.a : (ItemStack) nonnulllist.get(i);
    }

    public String getName() {
        return "container.inventory";
    }

    public boolean hasCustomName() {
        return false;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return (IChatBaseComponent) (this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName(), new Object[0]));
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean b(IBlockData iblockdata) {
        if (iblockdata.getMaterial().isAlwaysDestroyable()) {
            return true;
        } else {
            ItemStack itemstack = this.getItem(this.itemInHandIndex);

            return !itemstack.isEmpty() ? itemstack.b(iblockdata) : false;
        }
    }

    public void a(float f) {
        f /= 4.0F;
        if (f < 1.0F) {
            f = 1.0F;
        }

        for (int i = 0; i < this.armor.size(); ++i) {
            ItemStack itemstack = (ItemStack) this.armor.get(i);

            if (itemstack.getItem() instanceof ItemArmor) {
                itemstack.damage((int) f, this.player);
            }
        }

    }

    public void o() {
        Iterator iterator = this.g.iterator();

        while (iterator.hasNext()) {
            List list = (List) iterator.next();

            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemstack = (ItemStack) list.get(i);

                if (!itemstack.isEmpty()) {
                    this.player.a(itemstack, true, false);
                    list.set(i, ItemStack.a);
                }
            }
        }

    }

    public void update() {
        this.f = true;
    }

    public void setCarried(ItemStack itemstack) {
        this.carried = itemstack;
    }

    public ItemStack getCarried() {
        return this.carried;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.player.dead ? false : entityhuman.h(this.player) <= 64.0D;
    }

    public boolean f(ItemStack itemstack) {
        Iterator iterator = this.g.iterator();

        while (iterator.hasNext()) {
            List list = (List) iterator.next();
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

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public void a(PlayerInventory playerinventory) {
        for (int i = 0; i < this.getSize(); ++i) {
            this.setItem(i, playerinventory.getItem(i));
        }

        this.itemInHandIndex = playerinventory.itemInHandIndex;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {}

    public int h() {
        return 0;
    }

    public void clear() {
        Iterator iterator = this.g.iterator();

        while (iterator.hasNext()) {
            List list = (List) iterator.next();

            list.clear();
        }

    }
}
