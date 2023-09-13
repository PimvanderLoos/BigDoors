package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockDeadBush extends BlockPlant {

    protected static final AxisAlignedBB a = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

    protected BlockDeadBush() {
        super(Material.REPLACEABLE_PLANT);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockDeadBush.a;
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return MaterialMapColor.p;
    }

    protected boolean x(IBlockData iblockdata) {
        return iblockdata.getBlock() == Blocks.SAND || iblockdata.getBlock() == Blocks.HARDENED_CLAY || iblockdata.getBlock() == Blocks.STAINED_HARDENED_CLAY || iblockdata.getBlock() == Blocks.DIRT;
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    public int a(Random random) {
        return random.nextInt(3);
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.STICK;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (!world.isClientSide && itemstack.getItem() == Items.SHEARS) {
            entityhuman.b(StatisticList.a((Block) this));
            a(world, blockposition, new ItemStack(Blocks.DEADBUSH, 1, 0));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        }

    }
}
