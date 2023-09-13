package net.minecraft.server;

import java.util.Random;

public class BlockTallPlantFlower extends BlockTallPlant implements IBlockFragilePlantElement {

    public BlockTallPlantFlower(Block.Info block_info) {
        super(block_info);
    }

    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return false;
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        a(world, blockposition, new ItemStack(this));
    }
}
