package net.minecraft.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.PacketPlayOutAutoRecipe;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.ContainerRecipeBook;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.IRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutoRecipe<C extends IInventory> implements AutoRecipeAbstract<Integer> {

    protected static final Logger LOGGER = LogManager.getLogger();
    protected final AutoRecipeStackManager stackedContents = new AutoRecipeStackManager();
    protected PlayerInventory inventory;
    protected ContainerRecipeBook<C> menu;

    public AutoRecipe(ContainerRecipeBook<C> containerrecipebook) {
        this.menu = containerrecipebook;
    }

    public void a(EntityPlayer entityplayer, @Nullable IRecipe<C> irecipe, boolean flag) {
        if (irecipe != null && entityplayer.getRecipeBook().b(irecipe)) {
            this.inventory = entityplayer.getInventory();
            if (this.a() || entityplayer.isCreative()) {
                this.stackedContents.a();
                entityplayer.getInventory().a(this.stackedContents);
                this.menu.a(this.stackedContents);
                if (this.stackedContents.a(irecipe, (IntList) null)) {
                    this.a(irecipe, flag);
                } else {
                    this.a(true);
                    entityplayer.connection.sendPacket(new PacketPlayOutAutoRecipe(entityplayer.containerMenu.containerId, irecipe));
                }

                entityplayer.getInventory().update();
            }
        }
    }

    protected void a(boolean flag) {
        for (int i = 0; i < this.menu.p(); ++i) {
            if (this.menu.d(i)) {
                ItemStack itemstack = this.menu.getSlot(i).getItem().cloneItemStack();

                this.inventory.a(itemstack, false);
                this.menu.getSlot(i).set(itemstack);
            }
        }

        this.menu.l();
    }

    protected void a(IRecipe<C> irecipe, boolean flag) {
        boolean flag1 = this.menu.a(irecipe);
        int i = this.stackedContents.b(irecipe, (IntList) null);
        int j;

        if (flag1) {
            for (j = 0; j < this.menu.o() * this.menu.n() + 1; ++j) {
                if (j != this.menu.m()) {
                    ItemStack itemstack = this.menu.getSlot(j).getItem();

                    if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                        return;
                    }
                }
            }
        }

        j = this.a(flag, i, flag1);
        IntArrayList intarraylist = new IntArrayList();

        if (this.stackedContents.a(irecipe, intarraylist, j)) {
            int k = j;
            IntListIterator intlistiterator = intarraylist.iterator();

            while (intlistiterator.hasNext()) {
                int l = (Integer) intlistiterator.next();
                int i1 = AutoRecipeStackManager.a(l).getMaxStackSize();

                if (i1 < k) {
                    k = i1;
                }
            }

            if (this.stackedContents.a(irecipe, intarraylist, k)) {
                this.a(false);
                this.a(this.menu.n(), this.menu.o(), this.menu.m(), irecipe, intarraylist.iterator(), k);
            }
        }

    }

    @Override
    public void a(Iterator<Integer> iterator, int i, int j, int k, int l) {
        Slot slot = this.menu.getSlot(i);
        ItemStack itemstack = AutoRecipeStackManager.a((Integer) iterator.next());

        if (!itemstack.isEmpty()) {
            for (int i1 = 0; i1 < j; ++i1) {
                this.a(slot, itemstack);
            }
        }

    }

    protected int a(boolean flag, int i, boolean flag1) {
        int j = 1;

        if (flag) {
            j = i;
        } else if (flag1) {
            j = 64;

            for (int k = 0; k < this.menu.n() * this.menu.o() + 1; ++k) {
                if (k != this.menu.m()) {
                    ItemStack itemstack = this.menu.getSlot(k).getItem();

                    if (!itemstack.isEmpty() && j > itemstack.getCount()) {
                        j = itemstack.getCount();
                    }
                }
            }

            if (j < 64) {
                ++j;
            }
        }

        return j;
    }

    protected void a(Slot slot, ItemStack itemstack) {
        int i = this.inventory.c(itemstack);

        if (i != -1) {
            ItemStack itemstack1 = this.inventory.getItem(i).cloneItemStack();

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getCount() > 1) {
                    this.inventory.splitStack(i, 1);
                } else {
                    this.inventory.splitWithoutUpdate(i);
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

    private boolean a() {
        List<ItemStack> list = Lists.newArrayList();
        int i = this.b();

        for (int j = 0; j < this.menu.n() * this.menu.o() + 1; ++j) {
            if (j != this.menu.m()) {
                ItemStack itemstack = this.menu.getSlot(j).getItem().cloneItemStack();

                if (!itemstack.isEmpty()) {
                    int k = this.inventory.firstPartial(itemstack);

                    if (k == -1 && list.size() <= i) {
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext()) {
                            ItemStack itemstack1 = (ItemStack) iterator.next();

                            if (itemstack1.doMaterialsMatch(itemstack) && itemstack1.getCount() != itemstack1.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                                itemstack1.add(itemstack.getCount());
                                itemstack.setCount(0);
                                break;
                            }
                        }

                        if (!itemstack.isEmpty()) {
                            if (list.size() >= i) {
                                return false;
                            }

                            list.add(itemstack);
                        }
                    } else if (k == -1) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private int b() {
        int i = 0;
        Iterator iterator = this.inventory.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            if (itemstack.isEmpty()) {
                ++i;
            }
        }

        return i;
    }
}
