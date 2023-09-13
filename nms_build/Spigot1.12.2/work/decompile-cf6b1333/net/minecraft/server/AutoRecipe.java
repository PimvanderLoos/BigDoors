package net.minecraft.server;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutoRecipe {

    private final Logger a = LogManager.getLogger();
    private final AutoRecipeStackManager b = new AutoRecipeStackManager();
    private EntityPlayer c;
    private IRecipe d;
    private boolean e;
    private InventoryCraftResult f;
    private InventoryCrafting g;
    private List<Slot> h;

    public AutoRecipe() {}

    public void a(EntityPlayer entityplayer, @Nullable IRecipe irecipe, boolean flag) {
        if (irecipe != null && entityplayer.F().b(irecipe)) {
            this.c = entityplayer;
            this.d = irecipe;
            this.e = flag;
            this.h = entityplayer.activeContainer.slots;
            Container container = entityplayer.activeContainer;

            this.f = null;
            this.g = null;
            if (container instanceof ContainerWorkbench) {
                this.f = ((ContainerWorkbench) container).resultInventory;
                this.g = ((ContainerWorkbench) container).craftInventory;
            } else if (container instanceof ContainerPlayer) {
                this.f = ((ContainerPlayer) container).resultInventory;
                this.g = ((ContainerPlayer) container).craftInventory;
            }

            if (this.f != null && this.g != null) {
                if (this.c() || entityplayer.z()) {
                    this.b.a();
                    entityplayer.inventory.a(this.b, false);
                    this.g.a(this.b);
                    if (this.b.a(irecipe, (IntList) null)) {
                        this.b();
                    } else {
                        this.a();
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutAutoRecipe(entityplayer.activeContainer.windowId, irecipe));
                    }

                    entityplayer.inventory.update();
                }
            }
        }
    }

    private void a() {
        PlayerInventory playerinventory = this.c.inventory;

        for (int i = 0; i < this.g.getSize(); ++i) {
            ItemStack itemstack = this.g.getItem(i);

            if (!itemstack.isEmpty()) {
                while (itemstack.getCount() > 0) {
                    int j = playerinventory.firstPartial(itemstack);

                    if (j == -1) {
                        j = playerinventory.getFirstEmptySlotIndex();
                    }

                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.setCount(1);
                    playerinventory.c(j, itemstack1);
                    this.g.splitStack(i, 1);
                }
            }
        }

        this.g.clear();
        this.f.clear();
    }

    private void b() {
        boolean flag = this.d.a(this.g, this.c.world);
        int i = this.b.b(this.d, (IntList) null);

        if (flag) {
            boolean flag1 = true;

            for (int j = 0; j < this.g.getSize(); ++j) {
                ItemStack itemstack = this.g.getItem(j);

                if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) > itemstack.getCount()) {
                    flag1 = false;
                }
            }

            if (flag1) {
                return;
            }
        }

        int k = this.a(i, flag);
        IntArrayList intarraylist = new IntArrayList();

        if (this.b.a(this.d, intarraylist, k)) {
            int l = k;
            IntListIterator intlistiterator = intarraylist.iterator();

            while (intlistiterator.hasNext()) {
                int i1 = ((Integer) intlistiterator.next()).intValue();
                int j1 = AutoRecipeStackManager.b(i1).getMaxStackSize();

                if (j1 < l) {
                    l = j1;
                }
            }

            if (this.b.a(this.d, intarraylist, l)) {
                this.a();
                this.a(l, intarraylist);
            }
        }
    }

    private int a(int i, boolean flag) {
        int j = 1;

        if (this.e) {
            j = i;
        } else if (flag) {
            j = 64;

            for (int k = 0; k < this.g.getSize(); ++k) {
                ItemStack itemstack = this.g.getItem(k);

                if (!itemstack.isEmpty() && j > itemstack.getCount()) {
                    j = itemstack.getCount();
                }
            }

            if (j < 64) {
                ++j;
            }
        }

        return j;
    }

    private void a(int i, IntList intlist) {
        int j = this.g.j();
        int k = this.g.i();

        if (this.d instanceof ShapedRecipes) {
            ShapedRecipes shapedrecipes = (ShapedRecipes) this.d;

            j = shapedrecipes.f();
            k = shapedrecipes.g();
        }

        int l = 1;
        IntListIterator intlistiterator = intlist.iterator();

        for (int i1 = 0; i1 < this.g.j() && k != i1; ++i1) {
            for (int j1 = 0; j1 < this.g.i(); ++j1) {
                if (j == j1 || !intlistiterator.hasNext()) {
                    l += this.g.j() - j1;
                    break;
                }

                Slot slot = (Slot) this.h.get(l);
                ItemStack itemstack = AutoRecipeStackManager.b(((Integer) intlistiterator.next()).intValue());

                if (itemstack.isEmpty()) {
                    ++l;
                } else {
                    for (int k1 = 0; k1 < i; ++k1) {
                        this.a(slot, itemstack);
                    }

                    ++l;
                }
            }

            if (!intlistiterator.hasNext()) {
                break;
            }
        }

    }

    private void a(Slot slot, ItemStack itemstack) {
        PlayerInventory playerinventory = this.c.inventory;
        int i = playerinventory.c(itemstack);

        if (i != -1) {
            ItemStack itemstack1 = playerinventory.getItem(i).cloneItemStack();

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getCount() > 1) {
                    playerinventory.splitStack(i, 1);
                } else {
                    playerinventory.splitWithoutUpdate(i);
                }

                itemstack1.setCount(1);
                if (slot.getItem().isEmpty()) {
                    slot.set(itemstack1);
                } else {
                    slot.getItem().add(1);
                }

            }
        }
    }

    private boolean c() {
        PlayerInventory playerinventory = this.c.inventory;

        for (int i = 0; i < this.g.getSize(); ++i) {
            ItemStack itemstack = this.g.getItem(i);

            if (!itemstack.isEmpty()) {
                int j = playerinventory.firstPartial(itemstack);

                if (j == -1) {
                    j = playerinventory.getFirstEmptySlotIndex();
                }

                if (j == -1) {
                    return false;
                }
            }
        }

        return true;
    }
}
