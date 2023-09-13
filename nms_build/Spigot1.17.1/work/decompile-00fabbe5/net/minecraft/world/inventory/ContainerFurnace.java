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
        a(iinventory, 3);
        a(icontainerproperties, 4);
        this.container = iinventory;
        this.data = icontainerproperties;
        this.level = playerinventory.player.level;
        this.a(new Slot(iinventory, 0, 56, 17));
        this.a((Slot) (new SlotFurnaceFuel(this, iinventory, 1, 56, 53)));
        this.a((Slot) (new SlotFurnaceResult(playerinventory.player, iinventory, 2, 116, 35)));

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.a(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

        this.a(icontainerproperties);
    }

    @Override
    public void a(AutoRecipeStackManager autorecipestackmanager) {
        if (this.container instanceof AutoRecipeOutput) {
            ((AutoRecipeOutput) this.container).a(autorecipestackmanager);
        }

    }

    @Override
    public void l() {
        this.getSlot(0).set(ItemStack.EMPTY);
        this.getSlot(2).set(ItemStack.EMPTY);
    }

    @Override
    public boolean a(IRecipe<? super IInventory> irecipe) {
        return irecipe.a(this.container, this.level);
    }

    @Override
    public int m() {
        return 2;
    }

    @Override
    public int n() {
        return 1;
    }

    @Override
    public int o() {
        return 1;
    }

    @Override
    public int p() {
        return 3;
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return this.container.a(entityhuman);
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 2) {
                if (!this.a(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.a(itemstack1, itemstack);
            } else if (i != 1 && i != 0) {
                if (this.c(itemstack1)) {
                    if (!this.a(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.d(itemstack1)) {
                    if (!this.a(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 3 && i < 30) {
                    if (!this.a(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i >= 30 && i < 39 && !this.a(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.a(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.d();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.a(entityhuman, itemstack1);
        }

        return itemstack;
    }

    protected boolean c(ItemStack itemstack) {
        return this.level.getCraftingManager().craft(this.recipeType, new InventorySubcontainer(new ItemStack[]{itemstack}), this.level).isPresent();
    }

    protected boolean d(ItemStack itemstack) {
        return TileEntityFurnace.isFuel(itemstack);
    }

    public int q() {
        int i = this.data.getProperty(2);
        int j = this.data.getProperty(3);

        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    public int r() {
        int i = this.data.getProperty(1);

        if (i == 0) {
            i = 200;
        }

        return this.data.getProperty(0) * 13 / i;
    }

    public boolean s() {
        return this.data.getProperty(0) > 0;
    }

    @Override
    public RecipeBookType t() {
        return this.recipeBookType;
    }

    @Override
    public boolean d(int i) {
        return i != 1;
    }
}
