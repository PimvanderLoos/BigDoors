package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerSmoker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntitySmoker extends TileEntityFurnace {

    public TileEntitySmoker(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.SMOKER, blockposition, iblockdata, Recipes.SMOKING);
    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return IChatBaseComponent.translatable("container.smoker");
    }

    @Override
    protected int getBurnDuration(ItemStack itemstack) {
        return super.getBurnDuration(itemstack) / 2;
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerinventory) {
        return new ContainerSmoker(i, playerinventory, this, this.dataAccess);
    }
}
