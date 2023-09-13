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
        this.recipes = this.level.getCraftingManager().a(Recipes.SMITHING);
    }

    @Override
    protected boolean a(IBlockData iblockdata) {
        return iblockdata.a(Blocks.SMITHING_TABLE);
    }

    @Override
    protected boolean a(EntityHuman entityhuman, boolean flag) {
        return this.selectedRecipe != null && this.selectedRecipe.a(this.inputSlots, this.level);
    }

    @Override
    protected void a(EntityHuman entityhuman, ItemStack itemstack) {
        itemstack.a(entityhuman.level, entityhuman, itemstack.getCount());
        this.resultSlots.awardUsedRecipes(entityhuman);
        this.d(0);
        this.d(1);
        this.access.a((world, blockposition) -> {
            world.triggerEffect(1044, blockposition, 0);
        });
    }

    private void d(int i) {
        ItemStack itemstack = this.inputSlots.getItem(i);

        itemstack.subtract(1);
        this.inputSlots.setItem(i, itemstack);
    }

    @Override
    public void l() {
        List<RecipeSmithing> list = this.level.getCraftingManager().b(Recipes.SMITHING, this.inputSlots, this.level);

        if (list.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            this.selectedRecipe = (RecipeSmithing) list.get(0);
            ItemStack itemstack = this.selectedRecipe.a(this.inputSlots);

            this.resultSlots.setRecipeUsed(this.selectedRecipe);
            this.resultSlots.setItem(0, itemstack);
        }

    }

    @Override
    protected boolean c(ItemStack itemstack) {
        return this.recipes.stream().anyMatch((recipesmithing) -> {
            return recipesmithing.a(itemstack);
        });
    }

    @Override
    public boolean a(ItemStack itemstack, Slot slot) {
        return slot.container != this.resultSlots && super.a(itemstack, slot);
    }
}
