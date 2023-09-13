package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerFurnaceFurnace;
import net.minecraft.world.item.crafting.Recipes;

public class TileEntityFurnaceFurnace extends TileEntityFurnace {

    public TileEntityFurnaceFurnace() {
        super(TileEntityTypes.FURNACE, Recipes.SMELTING);
    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.furnace");
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerinventory) {
        return new ContainerFurnaceFurnace(i, playerinventory, this, this.b);
    }
}
