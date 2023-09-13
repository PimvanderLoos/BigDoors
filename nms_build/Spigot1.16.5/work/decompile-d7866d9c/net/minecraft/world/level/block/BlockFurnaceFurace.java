package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.state.BlockBase;

public class BlockFurnaceFurace extends BlockFurnace {

    protected BlockFurnaceFurace(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityFurnaceFurnace();
    }

    @Override
    protected void a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityFurnaceFurnace) {
            entityhuman.openContainer((ITileInventory) tileentity);
            entityhuman.a(StatisticList.INTERACT_WITH_FURNACE);
        }

    }
}
