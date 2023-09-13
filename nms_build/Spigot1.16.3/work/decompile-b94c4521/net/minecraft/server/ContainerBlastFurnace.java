package net.minecraft.server;

public class ContainerBlastFurnace extends ContainerFurnace {

    public ContainerBlastFurnace(int i, PlayerInventory playerinventory) {
        super(Containers.BLAST_FURNACE, Recipes.BLASTING, RecipeBookType.BLAST_FURNACE, i, playerinventory);
    }

    public ContainerBlastFurnace(int i, PlayerInventory playerinventory, IInventory iinventory, IContainerProperties icontainerproperties) {
        super(Containers.BLAST_FURNACE, Recipes.BLASTING, RecipeBookType.BLAST_FURNACE, i, playerinventory, iinventory, icontainerproperties);
    }
}
