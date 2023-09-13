package net.minecraft.world.inventory;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

/** @deprecated */
@Deprecated(forRemoval = true)
public class LegacySmithingMenu extends ContainerAnvilAbstract {

    private final World level;
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INPUT_SLOT_X_PLACEMENT = 27;
    private static final int ADDITIONAL_SLOT_X_PLACEMENT = 76;
    private static final int RESULT_SLOT_X_PLACEMENT = 134;
    private static final int SLOT_Y_PLACEMENT = 47;
    @Nullable
    private LegacyUpgradeRecipe selectedRecipe;
    private final List<LegacyUpgradeRecipe> recipes;

    public LegacySmithingMenu(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public LegacySmithingMenu(int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(Containers.LEGACY_SMITHING, i, playerinventory, containeraccess);
        this.level = playerinventory.player.level;
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(Recipes.SMITHING).stream().filter((smithingrecipe) -> {
            return smithingrecipe instanceof LegacyUpgradeRecipe;
        }).map((smithingrecipe) -> {
            return (LegacyUpgradeRecipe) smithingrecipe;
        }).toList();
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 27, 47, (itemstack) -> {
            return true;
        }).withSlot(1, 76, 47, (itemstack) -> {
            return true;
        }).withResultSlot(2, 134, 47).build();
    }

    @Override
    protected boolean isValidBlock(IBlockData iblockdata) {
        return iblockdata.is(Blocks.SMITHING_TABLE);
    }

    @Override
    protected boolean mayPickup(EntityHuman entityhuman, boolean flag) {
        return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
    }

    @Override
    protected void onTake(EntityHuman entityhuman, ItemStack itemstack) {
        itemstack.onCraftedBy(entityhuman.level, entityhuman, itemstack.getCount());
        this.resultSlots.awardUsedRecipes(entityhuman);
        this.shrinkStackInSlot(0);
        this.shrinkStackInSlot(1);
        this.access.execute((world, blockposition) -> {
            world.levelEvent(1044, blockposition, 0);
        });
    }

    private void shrinkStackInSlot(int i) {
        ItemStack itemstack = this.inputSlots.getItem(i);

        itemstack.shrink(1);
        this.inputSlots.setItem(i, itemstack);
    }

    @Override
    public void createResult() {
        List<LegacyUpgradeRecipe> list = this.level.getRecipeManager().getRecipesFor(Recipes.SMITHING, this.inputSlots, this.level).stream().filter((smithingrecipe) -> {
            return smithingrecipe instanceof LegacyUpgradeRecipe;
        }).map((smithingrecipe) -> {
            return (LegacyUpgradeRecipe) smithingrecipe;
        }).toList();

        if (list.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            LegacyUpgradeRecipe legacyupgraderecipe = (LegacyUpgradeRecipe) list.get(0);
            ItemStack itemstack = legacyupgraderecipe.assemble(this.inputSlots, this.level.registryAccess());

            if (itemstack.isItemEnabled(this.level.enabledFeatures())) {
                this.selectedRecipe = legacyupgraderecipe;
                this.resultSlots.setRecipeUsed(legacyupgraderecipe);
                this.resultSlots.setItem(0, itemstack);
            }
        }

    }

    @Override
    public int getSlotToQuickMoveTo(ItemStack itemstack) {
        return this.shouldQuickMoveToAdditionalSlot(itemstack) ? 1 : 0;
    }

    protected boolean shouldQuickMoveToAdditionalSlot(ItemStack itemstack) {
        return this.recipes.stream().anyMatch((legacyupgraderecipe) -> {
            return legacyupgraderecipe.isAdditionIngredient(itemstack);
        });
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemstack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(itemstack, slot);
    }
}
