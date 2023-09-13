package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockThin extends Block {

    public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
    public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
    public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
    public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");
    protected static final AxisAlignedBB[] f = new AxisAlignedBB[] { new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5625D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5625D, 1.0D, 1.0D), new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
    private final boolean a;

    protected BlockThin(Material material, boolean flag) {
        super(material);
        this.w(this.blockStateList.getBlockData().set(BlockThin.NORTH, Boolean.valueOf(false)).set(BlockThin.EAST, Boolean.valueOf(false)).set(BlockThin.SOUTH, Boolean.valueOf(false)).set(BlockThin.WEST, Boolean.valueOf(false)));
        this.a = flag;
        this.a(CreativeModeTab.c);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        if (!flag) {
            iblockdata = this.updateState(iblockdata, world, blockposition);
        }

        a(blockposition, axisalignedbb, list, BlockThin.f[0]);
        if (((Boolean) iblockdata.get(BlockThin.NORTH)).booleanValue()) {
            a(blockposition, axisalignedbb, list, BlockThin.f[a(EnumDirection.NORTH)]);
        }

        if (((Boolean) iblockdata.get(BlockThin.SOUTH)).booleanValue()) {
            a(blockposition, axisalignedbb, list, BlockThin.f[a(EnumDirection.SOUTH)]);
        }

        if (((Boolean) iblockdata.get(BlockThin.EAST)).booleanValue()) {
            a(blockposition, axisalignedbb, list, BlockThin.f[a(EnumDirection.EAST)]);
        }

        if (((Boolean) iblockdata.get(BlockThin.WEST)).booleanValue()) {
            a(blockposition, axisalignedbb, list, BlockThin.f[a(EnumDirection.WEST)]);
        }

    }

    private static int a(EnumDirection enumdirection) {
        return 1 << enumdirection.get2DRotationValue();
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        iblockdata = this.updateState(iblockdata, iblockaccess, blockposition);
        return BlockThin.f[x(iblockdata)];
    }

    private static int x(IBlockData iblockdata) {
        int i = 0;

        if (((Boolean) iblockdata.get(BlockThin.NORTH)).booleanValue()) {
            i |= a(EnumDirection.NORTH);
        }

        if (((Boolean) iblockdata.get(BlockThin.EAST)).booleanValue()) {
            i |= a(EnumDirection.EAST);
        }

        if (((Boolean) iblockdata.get(BlockThin.SOUTH)).booleanValue()) {
            i |= a(EnumDirection.SOUTH);
        }

        if (((Boolean) iblockdata.get(BlockThin.WEST)).booleanValue()) {
            i |= a(EnumDirection.WEST);
        }

        return i;
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.set(BlockThin.NORTH, Boolean.valueOf(this.b(iblockaccess, iblockaccess.getType(blockposition.north()), blockposition.north(), EnumDirection.SOUTH))).set(BlockThin.SOUTH, Boolean.valueOf(this.b(iblockaccess, iblockaccess.getType(blockposition.south()), blockposition.south(), EnumDirection.NORTH))).set(BlockThin.WEST, Boolean.valueOf(this.b(iblockaccess, iblockaccess.getType(blockposition.west()), blockposition.west(), EnumDirection.EAST))).set(BlockThin.EAST, Boolean.valueOf(this.b(iblockaccess, iblockaccess.getType(blockposition.east()), blockposition.east(), EnumDirection.WEST)));
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return !this.a ? Items.a : super.getDropType(iblockdata, random, i);
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public final boolean b(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        Block block = iblockdata.getBlock();
        EnumBlockFaceShape enumblockfaceshape = iblockdata.d(iblockaccess, blockposition, enumdirection);

        return !e(block) && enumblockfaceshape == EnumBlockFaceShape.SOLID || enumblockfaceshape == EnumBlockFaceShape.MIDDLE_POLE_THIN;
    }

    protected static boolean e(Block block) {
        return block instanceof BlockShulkerBox || block instanceof BlockLeaves || block == Blocks.BEACON || block == Blocks.cauldron || block == Blocks.GLOWSTONE || block == Blocks.ICE || block == Blocks.SEA_LANTERN || block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD || block == Blocks.MELON_BLOCK || block == Blocks.PUMPKIN || block == Blocks.LIT_PUMPKIN || block == Blocks.BARRIER;
    }

    protected boolean n() {
        return true;
    }

    public int toLegacyData(IBlockData iblockdata) {
        return 0;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            return iblockdata.set(BlockThin.NORTH, iblockdata.get(BlockThin.SOUTH)).set(BlockThin.EAST, iblockdata.get(BlockThin.WEST)).set(BlockThin.SOUTH, iblockdata.get(BlockThin.NORTH)).set(BlockThin.WEST, iblockdata.get(BlockThin.EAST));

        case COUNTERCLOCKWISE_90:
            return iblockdata.set(BlockThin.NORTH, iblockdata.get(BlockThin.EAST)).set(BlockThin.EAST, iblockdata.get(BlockThin.SOUTH)).set(BlockThin.SOUTH, iblockdata.get(BlockThin.WEST)).set(BlockThin.WEST, iblockdata.get(BlockThin.NORTH));

        case CLOCKWISE_90:
            return iblockdata.set(BlockThin.NORTH, iblockdata.get(BlockThin.WEST)).set(BlockThin.EAST, iblockdata.get(BlockThin.NORTH)).set(BlockThin.SOUTH, iblockdata.get(BlockThin.EAST)).set(BlockThin.WEST, iblockdata.get(BlockThin.SOUTH));

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
        case LEFT_RIGHT:
            return iblockdata.set(BlockThin.NORTH, iblockdata.get(BlockThin.SOUTH)).set(BlockThin.SOUTH, iblockdata.get(BlockThin.NORTH));

        case FRONT_BACK:
            return iblockdata.set(BlockThin.EAST, iblockdata.get(BlockThin.WEST)).set(BlockThin.WEST, iblockdata.get(BlockThin.EAST));

        default:
            return super.a(iblockdata, enumblockmirror);
        }
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockThin.NORTH, BlockThin.EAST, BlockThin.WEST, BlockThin.SOUTH});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN ? EnumBlockFaceShape.MIDDLE_POLE_THIN : EnumBlockFaceShape.CENTER_SMALL;
    }
}
