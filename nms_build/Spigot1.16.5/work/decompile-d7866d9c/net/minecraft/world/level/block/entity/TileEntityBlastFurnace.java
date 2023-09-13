package net.minecraft.world.level.block.entity;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerBlastFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipes;

public class TileEntityBlastFurnace extends TileEntityFurnace {

    public TileEntityBlastFurnace() {
        super(TileEntityTypes.BLAST_FURNACE, Recipes.BLASTING);
    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.blast_furnace");
    }

    @Override
    protected int fuelTime(ItemStack itemstack) {
        return super.fuelTime(itemstack) / 2;
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerinventory) {
        return new ContainerBlastFurnace(i, playerinventory, this, this.b);
    }
}
