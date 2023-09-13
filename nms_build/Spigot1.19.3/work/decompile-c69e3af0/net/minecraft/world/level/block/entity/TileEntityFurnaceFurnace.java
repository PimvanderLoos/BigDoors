package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerFurnaceFurnace;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityFurnaceFurnace extends TileEntityFurnace {

    public TileEntityFurnaceFurnace(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.FURNACE, blockposition, iblockdata, Recipes.SMELTING);
    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return IChatBaseComponent.translatable("container.furnace");
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerinventory) {
        return new ContainerFurnaceFurnace(i, playerinventory, this, this.dataAccess);
    }
}
