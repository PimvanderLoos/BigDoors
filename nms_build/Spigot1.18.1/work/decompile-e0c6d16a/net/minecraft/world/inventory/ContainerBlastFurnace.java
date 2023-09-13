package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.crafting.Recipes;

public class ContainerBlastFurnace extends ContainerFurnace {

    public ContainerBlastFurnace(int i, PlayerInventory playerinventory) {
        super(Containers.BLAST_FURNACE, Recipes.BLASTING, RecipeBookType.BLAST_FURNACE, i, playerinventory);
    }

    public ContainerBlastFurnace(int i, PlayerInventory playerinventory, IInventory iinventory, IContainerProperties icontainerproperties) {
        super(Containers.BLAST_FURNACE, Recipes.BLASTING, RecipeBookType.BLAST_FURNACE, i, playerinventory, iinventory, icontainerproperties);
    }
}
