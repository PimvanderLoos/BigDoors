package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerSmoker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipes;

public class TileEntitySmoker extends TileEntityFurnace {

    public TileEntitySmoker() {
        super(TileEntityTypes.SMOKER, Recipes.SMOKING);
    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.smoker");
    }

    @Override
    protected int fuelTime(ItemStack itemstack) {
        return super.fuelTime(itemstack) / 2;
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerinventory) {
        return new ContainerSmoker(i, playerinventory, this, this.b);
    }
}
