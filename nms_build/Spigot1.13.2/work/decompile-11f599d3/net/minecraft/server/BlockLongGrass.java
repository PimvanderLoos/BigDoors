package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockLongGrass extends BlockPlant implements IBlockFragilePlantElement {

    protected static final VoxelShape a = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    protected BlockLongGrass(Block.Info block_info) {
        super(block_info);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockLongGrass.a;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return world.random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.AIR;
    }

    public int getDropCount(IBlockData iblockdata, int i, World world, BlockPosition blockposition, Random random) {
        return 1 + random.nextInt(i * 2 + 1);
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (!world.isClientSide && itemstack.getItem() == Items.SHEARS) {
            entityhuman.b(StatisticList.BLOCK_MINED.b(this));
            entityhuman.applyExhaustion(0.005F);
            a(world, blockposition, new ItemStack(this));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        }

    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockTallPlant blocktallplant = (BlockTallPlant) ((BlockTallPlant) (this == Blocks.FERN ? Blocks.LARGE_FERN : Blocks.TALL_GRASS));

        if (blocktallplant.getBlockData().canPlace(world, blockposition) && world.isEmpty(blockposition.up())) {
            blocktallplant.a(world, blockposition, 2);
        }

    }

    public Block.EnumRandomOffset q() {
        return Block.EnumRandomOffset.XYZ;
    }
}
