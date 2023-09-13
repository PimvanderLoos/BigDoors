package net.minecraft.world.inventory;

import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.crafting.Recipes;

public class ContainerFurnaceFurnace extends ContainerFurnace {

    public ContainerFurnaceFurnace(int i, PlayerInventory playerinventory) {
        super(Containers.FURNACE, Recipes.SMELTING, RecipeBookType.FURNACE, i, playerinventory);
    }

    public ContainerFurnaceFurnace(int i, PlayerInventory playerinventory, IInventory iinventory, IContainerProperties icontainerproperties) {
        super(Containers.FURNACE, Recipes.SMELTING, RecipeBookType.FURNACE, i, playerinventory, iinventory, icontainerproperties);
    }
}
