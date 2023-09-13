package net.minecraft.world.inventory;

import net.minecraft.recipebook.AutoRecipe;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.item.crafting.IRecipe;

public abstract class ContainerRecipeBook<C extends IInventory> extends Container {

    public ContainerRecipeBook(Containers<?> containers, int i) {
        super(containers, i);
    }

    public void handlePlacement(boolean flag, IRecipe<?> irecipe, EntityPlayer entityplayer) {
        (new AutoRecipe<>(this)).recipeClicked(entityplayer, irecipe, flag);
    }

    public abstract void fillCraftSlotsStackedContents(AutoRecipeStackManager autorecipestackmanager);

    public abstract void clearCraftingContent();

    public abstract boolean recipeMatches(IRecipe<? super C> irecipe);

    public abstract int getResultSlotIndex();

    public abstract int getGridWidth();

    public abstract int getGridHeight();

    public abstract int getSize();

    public abstract RecipeBookType getRecipeBookType();

    public abstract boolean shouldMoveToInventory(int i);
}
