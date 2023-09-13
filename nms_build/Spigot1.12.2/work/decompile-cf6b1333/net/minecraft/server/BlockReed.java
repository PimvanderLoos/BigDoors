package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockReed extends Block {

    public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 15);
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    protected BlockReed() {
        super(Material.PLANT);
        this.w(this.blockStateList.getBlockData().set(BlockReed.AGE, Integer.valueOf(0)));
        this.a(true);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockReed.b;
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (world.getType(blockposition.down()).getBlock() == Blocks.REEDS || this.e(world, blockposition, iblockdata)) {
            if (world.isEmpty(blockposition.up())) {
                int i;

                for (i = 1; world.getType(blockposition.down(i)).getBlock() == this; ++i) {
                    ;
                }

                if (i < 3) {
                    int j = ((Integer) iblockdata.get(BlockReed.AGE)).intValue();

                    if (j == 15) {
                        world.setTypeUpdate(blockposition.up(), this.getBlockData());
                        world.setTypeAndData(blockposition, iblockdata.set(BlockReed.AGE, Integer.valueOf(0)), 4);
                    } else {
                        world.setTypeAndData(blockposition, iblockdata.set(BlockReed.AGE, Integer.valueOf(j + 1)), 4);
                    }
                }
            }

        }
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        Block block = world.getType(blockposition.down()).getBlock();

        if (block == this) {
            return true;
        } else if (block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.SAND) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.down();
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            IBlockData iblockdata;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                EnumDirection enumdirection = (EnumDirection) iterator.next();

                iblockdata = world.getType(blockposition1.shift(enumdirection));
            } while (iblockdata.getMaterial() != Material.WATER && iblockdata.getBlock() != Blocks.FROSTED_ICE);

            return true;
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        this.e(world, blockposition, iblockdata);
    }

    protected final boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.b(world, blockposition)) {
            return true;
        } else {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            return false;
        }
    }

    public boolean b(World world, BlockPosition blockposition) {
        return this.canPlace(world, blockposition);
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockReed.k;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.REEDS;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.REEDS);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockReed.AGE, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockReed.AGE)).intValue();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockReed.AGE});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
