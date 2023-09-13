package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerBlastFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityBlastFurnace extends TileEntityFurnace {

    public TileEntityBlastFurnace(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BLAST_FURNACE, blockposition, iblockdata, Recipes.BLASTING);
    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return IChatBaseComponent.translatable("container.blast_furnace");
    }

    @Override
    protected int getBurnDuration(ItemStack itemstack) {
        return super.getBurnDuration(itemstack) / 2;
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerinventory) {
        return new ContainerBlastFurnace(i, playerinventory, this, this.dataAccess);
    }
}
