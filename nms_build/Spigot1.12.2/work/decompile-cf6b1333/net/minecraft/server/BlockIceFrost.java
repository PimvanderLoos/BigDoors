package net.minecraft.server;

import java.util.Random;

public class BlockIceFrost extends BlockIce {

    public static final BlockStateInteger a = BlockStateInteger.of("age", 0, 3);

    public BlockIceFrost() {
        this.w(this.blockStateList.getBlockData().set(BlockIceFrost.a, Integer.valueOf(0)));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockIceFrost.a)).intValue();
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockIceFrost.a, Integer.valueOf(MathHelper.clamp(i, 0, 3)));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if ((random.nextInt(3) == 0 || this.c(world, blockposition) < 4) && world.getLightLevel(blockposition) > 11 - ((Integer) iblockdata.get(BlockIceFrost.a)).intValue() - iblockdata.c()) {
            this.a(world, blockposition, iblockdata, random, true);
        } else {
            world.a(blockposition, (Block) this, MathHelper.nextInt(random, 20, 40));
        }

    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (block == this) {
            int i = this.c(world, blockposition);

            if (i < 2) {
                this.b(world, blockposition);
            }
        }

    }

    private int c(World world, BlockPosition blockposition) {
        int i = 0;
        EnumDirection[] aenumdirection = EnumDirection.values();
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];

            if (world.getType(blockposition.shift(enumdirection)).getBlock() == this) {
                ++i;
                if (i >= 4) {
                    return i;
                }
            }
        }

        return i;
    }

    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random, boolean flag) {
        int i = ((Integer) iblockdata.get(BlockIceFrost.a)).intValue();

        if (i < 3) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockIceFrost.a, Integer.valueOf(i + 1)), 2);
            world.a(blockposition, (Block) this, MathHelper.nextInt(random, 20, 40));
        } else {
            this.b(world, blockposition);
            if (flag) {
                EnumDirection[] aenumdirection = EnumDirection.values();
                int j = aenumdirection.length;

                for (int k = 0; k < j; ++k) {
                    EnumDirection enumdirection = aenumdirection[k];
                    BlockPosition blockposition1 = blockposition.shift(enumdirection);
                    IBlockData iblockdata1 = world.getType(blockposition1);

                    if (iblockdata1.getBlock() == this) {
                        this.a(world, blockposition1, iblockdata1, random, false);
                    }
                }
            }
        }

    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockIceFrost.a});
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.a;
    }
}
