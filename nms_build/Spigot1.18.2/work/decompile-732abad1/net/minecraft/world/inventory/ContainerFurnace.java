package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeCooking;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntityFurnace;

public abstract class ContainerFurnace extends ContainerRecipeBook<IInventory> {

    public static final int INGREDIENT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    public static final int SLOT_COUNT = 3;
    public static final int DATA_COUNT = 4;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final IInventory container;
    private final IContainerProperties data;
    protected final World level;
    private final Recipes<? extends RecipeCooking> recipeType;
    private final RecipeBookType recipeBookType;

    protected ContainerFurnace(Containers<?> containers, Recipes<? extends RecipeCooking> recipes, RecipeBookType recipebooktype, int i, PlayerInventory playerinventory) {
        this(containers, recipes, recipebooktype, i, playerinventory, new InventorySubcontainer(3), new ContainerProperties(4));
    }

    protected ContainerFurnace(Containers<?> containers, Recipes<? extends RecipeCooking> recipes, RecipeBookType recipebooktype, int i, PlayerInventory playerinventory, IInventory iinventory, IContainerProperties icontainerproperties) {
        super(containers, i);
        this.recipeType = recipes;
        this.recipeBookType = recipebooktype;
        checkContainerSize(iinventory, 3);
        checkContainerDataCount(icontainerproperties, 4);
        this.container = iinventory;
        this.data = icontainerproperties;
        this.level = playerinventory.player.level;
        this.addSlot(new Slot(iinventory, 0, 56, 17));
        this.addSlot(new SlotFurnaceFuel(this, iinventory, 1, 56, 53));
        this.addSlot(new SlotFurnaceResult(playerinventory.player, iinventory, 2, 116, 35));

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

        this.addDataSlots(icontainerproperties);
    }

    @Override
    public void fillCraftSlotsStackedContents(AutoRecipeStackManager autorecipestackmanager) {
        if (this.container instanceof AutoRecipeOutput) {
            ((AutoRecipeOutput) this.container).fillStackedContents(autorecipestackmanager);
        }

    }

    @Override
    public void clearCraftingContent() {
        this.getSlot(0).set(ItemStack.EMPTY);
        this.getSlot(2).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(IRecipe<? super IInventory> irecipe) {
        return irecipe.matches(this.container, this.level);
    }

    @Override
    public int getResultSlotIndex() {
        return 2;
    }

    @Override
    public int getGridWidth() {
        return 1;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public boolean stillValid(EntityHuman entityhuman) {
        return this.container.stillValid(entityhuman);
    }

    @Override
    public ItemStack quickMoveStack(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.copy();
            if (i == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (i != 1 && i != 0) {
                if (this.canSmelt(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 3 && i < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 30 && i < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(entityhuman, itemstack1);
        }

        return itemstack;
    }

    protected boolean canSmelt(ItemStack itemstack) {
        return this.level.getRecipeManager().getRecipeFor(this.recipeType, new InventorySubcontainer(new ItemStack[]{itemstack}), this.level).isPresent();
    }

    protected boolean isFuel(ItemStack itemstack) {
        return TileEntityFurnace.isFuel(itemstack);
    }

    public int getBurnProgress() {
        int i = this.data.get(2);
        int j = this.data.get(3);

        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    public int getLitProgress() {
        int i = this.data.get(1);

        if (i == 0) {
            i = 200;
        }

        return this.data.get(0) * 13 / i;
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return this.recipeBookType;
    }

    @Override
    public boolean shouldMoveToInventory(int i) {
        return i != 1;
    }
}
