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

    public void recipeClicked(EntityPlayer entityplayer, @Nullable IRecipe<C> irecipe, boolean flag) {
        if (irecipe != null && entityplayer.getRecipeBook().contains(irecipe)) {
            this.inventory = entityplayer.getInventory();
            if (this.testClearGrid() || entityplayer.isCreative()) {
                this.stackedContents.clear();
                entityplayer.getInventory().fillStackedContents(this.stackedContents);
                this.menu.fillCraftSlotsStackedContents(this.stackedContents);
                if (this.stackedContents.canCraft(irecipe, (IntList) null)) {
                    this.handleRecipeClicked(irecipe, flag);
                } else {
                    this.clearGrid(true);
                    entityplayer.connection.send(new PacketPlayOutAutoRecipe(entityplayer.containerMenu.containerId, irecipe));
                }

                entityplayer.getInventory().setChanged();
            }
        }
    }

    protected void clearGrid(boolean flag) {
        for (int i = 0; i < this.menu.getSize(); ++i) {
            if (this.menu.shouldMoveToInventory(i)) {
                ItemStack itemstack = this.menu.getSlot(i).getItem().copy();

                this.inventory.placeItemBackInInventory(itemstack, false);
                this.menu.getSlot(i).set(itemstack);
            }
        }

        this.menu.clearCraftingContent();
    }

    protected void handleRecipeClicked(IRecipe<C> irecipe, boolean flag) {
        boolean flag1 = this.menu.recipeMatches(irecipe);
        int i = this.stackedContents.getBiggestCraftableStack(irecipe, (IntList) null);
        int j;

        if (flag1) {
            for (j = 0; j < this.menu.getGridHeight() * this.menu.getGridWidth() + 1; ++j) {
                if (j != this.menu.getResultSlotIndex()) {
                    ItemStack itemstack = this.menu.getSlot(j).getItem();

                    if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                        return;
                    }
                }
            }
        }

        j = this.getStackSize(flag, i, flag1);
        IntArrayList intarraylist = new IntArrayList();

        if (this.stackedContents.canCraft(irecipe, intarraylist, j)) {
            int k = j;
            IntListIterator intlistiterator = intarraylist.iterator();

            while (intlistiterator.hasNext()) {
                int l = (Integer) intlistiterator.next();
                int i1 = AutoRecipeStackManager.fromStackingIndex(l).getMaxStackSize();

                if (i1 < k) {
                    k = i1;
                }
            }

            if (this.stackedContents.canCraft(irecipe, intarraylist, k)) {
                this.clearGrid(false);
                this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), irecipe, intarraylist.iterator(), k);
            }
        }

    }

    @Override
    public void addItemToSlot(Iterator<Integer> iterator, int i, int j, int k, int l) {
        Slot slot = this.menu.getSlot(i);
        ItemStack itemstack = AutoRecipeStackManager.fromStackingIndex((Integer) iterator.next());

        if (!itemstack.isEmpty()) {
            for (int i1 = 0; i1 < j; ++i1) {
                this.moveItemToGrid(slot, itemstack);
            }
        }

    }

    protected int getStackSize(boolean flag, int i, boolean flag1) {
        int j = 1;

        if (flag) {
            j = i;
        } else if (flag1) {
            j = 64;

            for (int k = 0; k < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++k) {
                if (k != this.menu.getResultSlotIndex()) {
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

    protected void moveItemToGrid(Slot slot, ItemStack itemstack) {
        int i = this.inventory.findSlotMatchingUnusedItem(itemstack);

        if (i != -1) {
            ItemStack itemstack1 = this.inventory.getItem(i).copy();

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getCount() > 1) {
                    this.inventory.removeItem(i, 1);
                } else {
                    this.inventory.removeItemNoUpdate(i);
                }

                itemstack1.setCount(1);
                if (slot.getItem().isEmpty()) {
                    slot.set(itemstack1);
                } else {
                    slot.getItem().grow(1);
                }

            }
        }
    }

    private boolean testClearGrid() {
        List<ItemStack> list = Lists.newArrayList();
        int i = this.getAmountOfFreeSlotsInInventory();

        for (int j = 0; j < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++j) {
            if (j != this.menu.getResultSlotIndex()) {
                ItemStack itemstack = this.menu.getSlot(j).getItem().copy();

                if (!itemstack.isEmpty()) {
                    int k = this.inventory.getSlotWithRemainingSpace(itemstack);

                    if (k == -1 && list.size() <= i) {
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext()) {
                            ItemStack itemstack1 = (ItemStack) iterator.next();

                            if (itemstack1.sameItem(itemstack) && itemstack1.getCount() != itemstack1.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                                itemstack1.grow(itemstack.getCount());
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

    private int getAmountOfFreeSlotsInInventory() {
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
