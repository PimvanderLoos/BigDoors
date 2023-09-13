package net.minecraft.world.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;

public class ContainerStonecutter extends Container {

    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int USE_ROW_SLOT_START = 29;
    private static final int USE_ROW_SLOT_END = 38;
    private final ContainerAccess access;
    private final ContainerProperty selectedRecipeIndex;
    private final World level;
    private List<RecipeStonecutting> recipes;
    private ItemStack input;
    long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    Runnable slotUpdateListener;
    public final IInventory container;
    final InventoryCraftResult resultContainer;

    public ContainerStonecutter(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerStonecutter(int i, PlayerInventory playerinventory, final ContainerAccess containeraccess) {
        super(Containers.STONECUTTER, i);
        this.selectedRecipeIndex = ContainerProperty.standalone();
        this.recipes = Lists.newArrayList();
        this.input = ItemStack.EMPTY;
        this.slotUpdateListener = () -> {
        };
        this.container = new InventorySubcontainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                ContainerStonecutter.this.slotsChanged(this);
                ContainerStonecutter.this.slotUpdateListener.run();
            }
        };
        this.resultContainer = new InventoryCraftResult();
        this.access = containeraccess;
        this.level = playerinventory.player.level;
        this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack itemstack) {
                return false;
            }

            @Override
            public void onTake(EntityHuman entityhuman, ItemStack itemstack) {
                itemstack.onCraftedBy(entityhuman.level, entityhuman, itemstack.getCount());
                ContainerStonecutter.this.resultContainer.awardUsedRecipes(entityhuman);
                ItemStack itemstack1 = ContainerStonecutter.this.inputSlot.remove(1);

                if (!itemstack1.isEmpty()) {
                    ContainerStonecutter.this.setupResultSlot();
                }

                containeraccess.execute((world, blockposition) -> {
                    long j = world.getGameTime();

                    if (ContainerStonecutter.this.lastSoundTime != j) {
                        world.playSound((EntityHuman) null, blockposition, SoundEffects.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        ContainerStonecutter.this.lastSoundTime = j;
                    }

                });
                super.onTake(entityhuman, itemstack);
            }
        });

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

        this.addDataSlot(this.selectedRecipeIndex);
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<RecipeStonecutting> getRecipes() {
        return this.recipes;
    }

    public int getNumRecipes() {
        return this.recipes.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return stillValid(this.access, entityhuman, Blocks.STONECUTTER);
    }

    @Override
    public boolean clickMenuButton(EntityHuman entityhuman, int i) {
        if (this.isValidRecipeIndex(i)) {
            this.selectedRecipeIndex.set(i);
            this.setupResultSlot();
        }

        return true;
    }

    private boolean isValidRecipeIndex(int i) {
        return i >= 0 && i < this.recipes.size();
    }

    @Override
    public void slotsChanged(IInventory iinventory) {
        ItemStack itemstack = this.inputSlot.getItem();

        if (!itemstack.is(this.input.getItem())) {
            this.input = itemstack.copy();
            this.setupRecipeList(iinventory, itemstack);
        }

    }

    private void setupRecipeList(IInventory iinventory, ItemStack itemstack) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!itemstack.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(Recipes.STONECUTTING, iinventory, this.level);
        }

    }

    void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            RecipeStonecutting recipestonecutting = (RecipeStonecutting) this.recipes.get(this.selectedRecipeIndex.get());

            this.resultContainer.setRecipeUsed(recipestonecutting);
            this.resultSlot.set(recipestonecutting.assemble(this.container));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    @Override
    public Containers<?> getType() {
        return Containers.STONECUTTER;
    }

    public void registerUpdateListener(Runnable runnable) {
        this.slotUpdateListener = runnable;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemstack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(itemstack, slot);
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();

            itemstack = itemstack1.copy();
            if (i == 1) {
                item.onCraftedBy(itemstack1, entityhuman.level, entityhuman);
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (i == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getRecipeManager().getRecipeFor(Recipes.STONECUTTING, new InventorySubcontainer(new ItemStack[]{itemstack1}), this.level).isPresent()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= 2 && i < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (i >= 29 && i < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(entityhuman, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    @Override
    public void removed(EntityHuman entityhuman) {
        super.removed(entityhuman);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((world, blockposition) -> {
            this.clearContainer(entityhuman, this.container);
        });
    }
}
