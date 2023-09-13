package net.minecraft.server;

import java.util.Random;

public class BlockMycel extends Block {

    public static final BlockStateBoolean SNOWY = BlockStateBoolean.of("snowy");

    protected BlockMycel() {
        super(Material.GRASS, MaterialMapColor.A);
        this.w(this.blockStateList.getBlockData().set(BlockMycel.SNOWY, Boolean.valueOf(false)));
        this.a(true);
        this.a(CreativeModeTab.b);
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = iblockaccess.getType(blockposition.up()).getBlock();

        return iblockdata.set(BlockMycel.SNOWY, Boolean.valueOf(block == Blocks.SNOW || block == Blocks.SNOW_LAYER));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            if (world.getLightLevel(blockposition.up()) < 4 && world.getType(blockposition.up()).c() > 2) {
                world.setTypeUpdate(blockposition, Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT));
            } else {
                if (world.getLightLevel(blockposition.up()) >= 9) {
                    for (int i = 0; i < 4; ++i) {
                        BlockPosition blockposition1 = blockposition.a(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                        IBlockData iblockdata1 = world.getType(blockposition1);
                        IBlockData iblockdata2 = world.getType(blockposition1.up());

                        if (iblockdata1.getBlock() == Blocks.DIRT && iblockdata1.get(BlockDirt.VARIANT) == BlockDirt.EnumDirtVariant.DIRT && world.getLightLevel(blockposition1.up()) >= 4 && iblockdata2.c() <= 2) {
                            world.setTypeUpdate(blockposition1, this.getBlockData());
                        }
                    }
                }

            }
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Blocks.DIRT.getDropType(Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT), random, i);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return 0;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockMycel.SNOWY});
    }
}
