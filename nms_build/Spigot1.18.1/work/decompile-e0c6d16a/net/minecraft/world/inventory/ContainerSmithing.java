package net.minecraft.world.inventory;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSmithing;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class ContainerSmithing extends ContainerAnvilAbstract {

    private final World level;
    @Nullable
    private RecipeSmithing selectedRecipe;
    private final List<RecipeSmithing> recipes;

    public ContainerSmithing(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.NULL);
    }

    public ContainerSmithing(int i, PlayerInventory playerinventory, ContainerAccess containeraccess) {
        super(Containers.SMITHING, i, playerinventory, containeraccess);
        this.level = playerinventory.player.level;
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(Recipes.SMITHING);
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
        List<RecipeSmithing> list = this.level.getRecipeManager().getRecipesFor(Recipes.SMITHING, this.inputSlots, this.level);

        if (list.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            this.selectedRecipe = (RecipeSmithing) list.get(0);
            ItemStack itemstack = this.selectedRecipe.assemble(this.inputSlots);

            this.resultSlots.setRecipeUsed(this.selectedRecipe);
            this.resultSlots.setItem(0, itemstack);
        }

    }

    @Override
    protected boolean shouldQuickMoveToAdditionalSlot(ItemStack itemstack) {
        return this.recipes.stream().anyMatch((recipesmithing) -> {
            return recipesmithing.isAdditionIngredient(itemstack);
        });
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemstack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(itemstack, slot);
    }
}
