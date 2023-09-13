package net.minecraft.server;

import java.util.Random;

public class BlockNetherWart extends BlockPlant {

    public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 3);
    private static final AxisAlignedBB[] c = new AxisAlignedBB[] { new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.6875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D)};

    protected BlockNetherWart() {
        super(Material.PLANT, MaterialMapColor.E);
        this.w(this.blockStateList.getBlockData().set(BlockNetherWart.AGE, Integer.valueOf(0)));
        this.a(true);
        this.a((CreativeModeTab) null);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockNetherWart.c[((Integer) iblockdata.get(BlockNetherWart.AGE)).intValue()];
    }

    protected boolean x(IBlockData iblockdata) {
        return iblockdata.getBlock() == Blocks.SOUL_SAND;
    }

    public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return this.x(world.getType(blockposition.down()));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        int i = ((Integer) iblockdata.get(BlockNetherWart.AGE)).intValue();

        if (i < 3 && random.nextInt(10) == 0) {
            iblockdata = iblockdata.set(BlockNetherWart.AGE, Integer.valueOf(i + 1));
            world.setTypeAndData(blockposition, iblockdata, 2);
        }

        super.b(world, blockposition, iblockdata, random);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        if (!world.isClientSide) {
            int j = 1;

            if (((Integer) iblockdata.get(BlockNetherWart.AGE)).intValue() >= 3) {
                j = 2 + world.random.nextInt(3);
                if (i > 0) {
                    j += world.random.nextInt(i + 1);
                }
            }

            for (int k = 0; k < j; ++k) {
                a(world, blockposition, new ItemStack(Items.NETHER_WART));
            }

        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.a;
    }

    public int a(Random random) {
        return 0;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.NETHER_WART);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockNetherWart.AGE, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockNetherWart.AGE)).intValue();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockNetherWart.AGE});
    }
}
