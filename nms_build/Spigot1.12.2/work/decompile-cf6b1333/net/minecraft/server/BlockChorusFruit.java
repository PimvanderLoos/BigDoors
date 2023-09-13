package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockChorusFruit extends Block {

    public static final BlockStateBoolean a = BlockStateBoolean.of("north");
    public static final BlockStateBoolean b = BlockStateBoolean.of("east");
    public static final BlockStateBoolean c = BlockStateBoolean.of("south");
    public static final BlockStateBoolean d = BlockStateBoolean.of("west");
    public static final BlockStateBoolean e = BlockStateBoolean.of("up");
    public static final BlockStateBoolean f = BlockStateBoolean.of("down");

    protected BlockChorusFruit() {
        super(Material.PLANT, MaterialMapColor.A);
        this.a(CreativeModeTab.c);
        this.w(this.blockStateList.getBlockData().set(BlockChorusFruit.a, Boolean.valueOf(false)).set(BlockChorusFruit.b, Boolean.valueOf(false)).set(BlockChorusFruit.c, Boolean.valueOf(false)).set(BlockChorusFruit.d, Boolean.valueOf(false)).set(BlockChorusFruit.e, Boolean.valueOf(false)).set(BlockChorusFruit.f, Boolean.valueOf(false)));
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = iblockaccess.getType(blockposition.down()).getBlock();
        Block block1 = iblockaccess.getType(blockposition.up()).getBlock();
        Block block2 = iblockaccess.getType(blockposition.north()).getBlock();
        Block block3 = iblockaccess.getType(blockposition.east()).getBlock();
        Block block4 = iblockaccess.getType(blockposition.south()).getBlock();
        Block block5 = iblockaccess.getType(blockposition.west()).getBlock();

        return iblockdata.set(BlockChorusFruit.f, Boolean.valueOf(block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE)).set(BlockChorusFruit.e, Boolean.valueOf(block1 == this || block1 == Blocks.CHORUS_FLOWER)).set(BlockChorusFruit.a, Boolean.valueOf(block2 == this || block2 == Blocks.CHORUS_FLOWER)).set(BlockChorusFruit.b, Boolean.valueOf(block3 == this || block3 == Blocks.CHORUS_FLOWER)).set(BlockChorusFruit.c, Boolean.valueOf(block4 == this || block4 == Blocks.CHORUS_FLOWER)).set(BlockChorusFruit.d, Boolean.valueOf(block5 == this || block5 == Blocks.CHORUS_FLOWER));
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        iblockdata = iblockdata.c(iblockaccess, blockposition);
        float f = 0.1875F;
        float f1 = ((Boolean) iblockdata.get(BlockChorusFruit.d)).booleanValue() ? 0.0F : 0.1875F;
        float f2 = ((Boolean) iblockdata.get(BlockChorusFruit.f)).booleanValue() ? 0.0F : 0.1875F;
        float f3 = ((Boolean) iblockdata.get(BlockChorusFruit.a)).booleanValue() ? 0.0F : 0.1875F;
        float f4 = ((Boolean) iblockdata.get(BlockChorusFruit.b)).booleanValue() ? 1.0F : 0.8125F;
        float f5 = ((Boolean) iblockdata.get(BlockChorusFruit.e)).booleanValue() ? 1.0F : 0.8125F;
        float f6 = ((Boolean) iblockdata.get(BlockChorusFruit.c)).booleanValue() ? 1.0F : 0.8125F;

        return new AxisAlignedBB((double) f1, (double) f2, (double) f3, (double) f4, (double) f5, (double) f6);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        if (!flag) {
            iblockdata = iblockdata.c(world, blockposition);
        }

        float f = 0.1875F;
        float f1 = 0.8125F;

        a(blockposition, axisalignedbb, list, new AxisAlignedBB(0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.8125D, 0.8125D));
        if (((Boolean) iblockdata.get(BlockChorusFruit.d)).booleanValue()) {
            a(blockposition, axisalignedbb, list, new AxisAlignedBB(0.0D, 0.1875D, 0.1875D, 0.1875D, 0.8125D, 0.8125D));
        }

        if (((Boolean) iblockdata.get(BlockChorusFruit.b)).booleanValue()) {
            a(blockposition, axisalignedbb, list, new AxisAlignedBB(0.8125D, 0.1875D, 0.1875D, 1.0D, 0.8125D, 0.8125D));
        }

        if (((Boolean) iblockdata.get(BlockChorusFruit.e)).booleanValue()) {
            a(blockposition, axisalignedbb, list, new AxisAlignedBB(0.1875D, 0.8125D, 0.1875D, 0.8125D, 1.0D, 0.8125D));
        }

        if (((Boolean) iblockdata.get(BlockChorusFruit.f)).booleanValue()) {
            a(blockposition, axisalignedbb, list, new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.1875D, 0.8125D));
        }

        if (((Boolean) iblockdata.get(BlockChorusFruit.a)).booleanValue()) {
            a(blockposition, axisalignedbb, list, new AxisAlignedBB(0.1875D, 0.1875D, 0.0D, 0.8125D, 0.8125D, 0.1875D));
        }

        if (((Boolean) iblockdata.get(BlockChorusFruit.c)).booleanValue()) {
            a(blockposition, axisalignedbb, list, new AxisAlignedBB(0.1875D, 0.1875D, 0.8125D, 0.8125D, 0.8125D, 1.0D));
        }

    }

    public int toLegacyData(IBlockData iblockdata) {
        return 0;
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!this.b(world, blockposition)) {
            world.setAir(blockposition, true);
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.CHORUS_FRUIT;
    }

    public int a(Random random) {
        return random.nextInt(2);
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return super.canPlace(world, blockposition) ? this.b(world, blockposition) : false;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!this.b(world, blockposition)) {
            world.a(blockposition, (Block) this, 1);
        }

    }

    public boolean b(World world, BlockPosition blockposition) {
        boolean flag = world.isEmpty(blockposition.up());
        boolean flag1 = world.isEmpty(blockposition.down());
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        Block block;

        do {
            BlockPosition blockposition1;
            Block block1;

            do {
                if (!iterator.hasNext()) {
                    Block block2 = world.getType(blockposition.down()).getBlock();

                    return block2 == this || block2 == Blocks.END_STONE;
                }

                EnumDirection enumdirection = (EnumDirection) iterator.next();

                blockposition1 = blockposition.shift(enumdirection);
                block1 = world.getType(blockposition1).getBlock();
            } while (block1 != this);

            if (!flag && !flag1) {
                return false;
            }

            block = world.getType(blockposition1.down()).getBlock();
        } while (block != this && block != Blocks.END_STONE);

        return true;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockChorusFruit.a, BlockChorusFruit.b, BlockChorusFruit.c, BlockChorusFruit.d, BlockChorusFruit.e, BlockChorusFruit.f});
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
