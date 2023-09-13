package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockTallPlantFlower extends BlockTallPlant implements IBlockFragilePlantElement {

    public BlockTallPlantFlower(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return false;
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        a((World) worldserver, blockposition, new ItemStack(this));
    }
}
