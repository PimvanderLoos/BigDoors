package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

public class BlockFence extends Block {

    public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
    public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
    public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
    public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");
    protected static final AxisAlignedBB[] e = new AxisAlignedBB[] { new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
    public static final AxisAlignedBB f = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.5D, 0.625D);
    public static final AxisAlignedBB g = new AxisAlignedBB(0.375D, 0.0D, 0.625D, 0.625D, 1.5D, 1.0D);
    public static final AxisAlignedBB B = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.375D, 1.5D, 0.625D);
    public static final AxisAlignedBB C = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 0.375D);
    public static final AxisAlignedBB D = new AxisAlignedBB(0.625D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D);

    public BlockFence(Material material, MaterialMapColor materialmapcolor) {
        super(material, materialmapcolor);
        this.w(this.blockStateList.getBlockData().set(BlockFence.NORTH, Boolean.valueOf(false)).set(BlockFence.EAST, Boolean.valueOf(false)).set(BlockFence.SOUTH, Boolean.valueOf(false)).set(BlockFence.WEST, Boolean.valueOf(false)));
        this.a(CreativeModeTab.c);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        if (!flag) {
            iblockdata = iblockdata.c(world, blockposition);
        }

        a(blockposition, axisalignedbb, list, BlockFence.f);
        if (((Boolean) iblockdata.get(BlockFence.NORTH)).booleanValue()) {
            a(blockposition, axisalignedbb, list, BlockFence.C);
        }

        if (((Boolean) iblockdata.get(BlockFence.EAST)).booleanValue()) {
            a(blockposition, axisalignedbb, list, BlockFence.D);
        }

        if (((Boolean) iblockdata.get(BlockFence.SOUTH)).booleanValue()) {
            a(blockposition, axisalignedbb, list, BlockFence.g);
        }

        if (((Boolean) iblockdata.get(BlockFence.WEST)).booleanValue()) {
            a(blockposition, axisalignedbb, list, BlockFence.B);
        }

    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        iblockdata = this.updateState(iblockdata, iblockaccess, blockposition);
        return BlockFence.e[x(iblockdata)];
    }

    private static int x(IBlockData iblockdata) {
        int i = 0;

        if (((Boolean) iblockdata.get(BlockFence.NORTH)).booleanValue()) {
            i |= 1 << EnumDirection.NORTH.get2DRotationValue();
        }

        if (((Boolean) iblockdata.get(BlockFence.EAST)).booleanValue()) {
            i |= 1 << EnumDirection.EAST.get2DRotationValue();
        }

        if (((Boolean) iblockdata.get(BlockFence.SOUTH)).booleanValue()) {
            i |= 1 << EnumDirection.SOUTH.get2DRotationValue();
        }

        if (((Boolean) iblockdata.get(BlockFence.WEST)).booleanValue()) {
            i |= 1 << EnumDirection.WEST.get2DRotationValue();
        }

        return i;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        EnumBlockFaceShape enumblockfaceshape = iblockdata.d(iblockaccess, blockposition, enumdirection);
        Block block = iblockdata.getBlock();
        boolean flag = enumblockfaceshape == EnumBlockFaceShape.MIDDLE_POLE && (iblockdata.getMaterial() == this.material || block instanceof BlockFenceGate);

        return !e(block) && enumblockfaceshape == EnumBlockFaceShape.SOLID || flag;
    }

    protected static boolean e(Block block) {
        return Block.c(block) || block == Blocks.BARRIER || block == Blocks.MELON_BLOCK || block == Blocks.PUMPKIN || block == Blocks.LIT_PUMPKIN;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!world.isClientSide) {
            return ItemLeash.a(entityhuman, world, blockposition);
        } else {
            ItemStack itemstack = entityhuman.b(enumhand);

            return itemstack.getItem() == Items.LEAD || itemstack.isEmpty();
        }
    }

    public int toLegacyData(IBlockData iblockdata) {
        return 0;
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.set(BlockFence.NORTH, Boolean.valueOf(this.a(iblockaccess, blockposition.north(), EnumDirection.SOUTH))).set(BlockFence.EAST, Boolean.valueOf(this.a(iblockaccess, blockposition.east(), EnumDirection.WEST))).set(BlockFence.SOUTH, Boolean.valueOf(this.a(iblockaccess, blockposition.south(), EnumDirection.NORTH))).set(BlockFence.WEST, Boolean.valueOf(this.a(iblockaccess, blockposition.west(), EnumDirection.EAST)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            return iblockdata.set(BlockFence.NORTH, iblockdata.get(BlockFence.SOUTH)).set(BlockFence.EAST, iblockdata.get(BlockFence.WEST)).set(BlockFence.SOUTH, iblockdata.get(BlockFence.NORTH)).set(BlockFence.WEST, iblockdata.get(BlockFence.EAST));

        case COUNTERCLOCKWISE_90:
            return iblockdata.set(BlockFence.NORTH, iblockdata.get(BlockFence.EAST)).set(BlockFence.EAST, iblockdata.get(BlockFence.SOUTH)).set(BlockFence.SOUTH, iblockdata.get(BlockFence.WEST)).set(BlockFence.WEST, iblockdata.get(BlockFence.NORTH));

        case CLOCKWISE_90:
            return iblockdata.set(BlockFence.NORTH, iblockdata.get(BlockFence.WEST)).set(BlockFence.EAST, iblockdata.get(BlockFence.NORTH)).set(BlockFence.SOUTH, iblockdata.get(BlockFence.EAST)).set(BlockFence.WEST, iblockdata.get(BlockFence.SOUTH));

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
        case LEFT_RIGHT:
            return iblockdata.set(BlockFence.NORTH, iblockdata.get(BlockFence.SOUTH)).set(BlockFence.SOUTH, iblockdata.get(BlockFence.NORTH));

        case FRONT_BACK:
            return iblockdata.set(BlockFence.EAST, iblockdata.get(BlockFence.WEST)).set(BlockFence.WEST, iblockdata.get(BlockFence.EAST));

        default:
            return super.a(iblockdata, enumblockmirror);
        }
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockFence.NORTH, BlockFence.EAST, BlockFence.WEST, BlockFence.SOUTH});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN ? EnumBlockFaceShape.MIDDLE_POLE : EnumBlockFaceShape.CENTER;
    }
}
