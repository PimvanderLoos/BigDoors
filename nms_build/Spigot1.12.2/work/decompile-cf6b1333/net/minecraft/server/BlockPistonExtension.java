package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockPistonExtension extends BlockDirectional {

    public static final BlockStateEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockStateEnum.of("type", BlockPistonExtension.EnumPistonType.class);
    public static final BlockStateBoolean SHORT = BlockStateBoolean.of("short");
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.75D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.25D, 1.0D, 1.0D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.0D, 0.0D, 0.75D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB B = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
    protected static final AxisAlignedBB C = new AxisAlignedBB(0.375D, -0.25D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB D = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.25D, 0.625D);
    protected static final AxisAlignedBB E = new AxisAlignedBB(0.375D, 0.375D, -0.25D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB F = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 1.25D);
    protected static final AxisAlignedBB G = new AxisAlignedBB(-0.25D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    protected static final AxisAlignedBB I = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 1.25D, 0.625D, 0.625D);
    protected static final AxisAlignedBB J = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB K = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.0D, 0.625D);
    protected static final AxisAlignedBB L = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB M = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 1.0D);
    protected static final AxisAlignedBB N = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    protected static final AxisAlignedBB O = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D);

    public BlockPistonExtension() {
        super(Material.PISTON);
        this.w(this.blockStateList.getBlockData().set(BlockPistonExtension.FACING, EnumDirection.NORTH).set(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.DEFAULT).set(BlockPistonExtension.SHORT, Boolean.valueOf(false)));
        this.a(SoundEffectType.d);
        this.c(0.5F);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)) {
        case DOWN:
        default:
            return BlockPistonExtension.B;

        case UP:
            return BlockPistonExtension.g;

        case NORTH:
            return BlockPistonExtension.f;

        case SOUTH:
            return BlockPistonExtension.e;

        case WEST:
            return BlockPistonExtension.d;

        case EAST:
            return BlockPistonExtension.c;
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        a(blockposition, axisalignedbb, list, iblockdata.e(world, blockposition));
        a(blockposition, axisalignedbb, list, this.x(iblockdata));
    }

    private AxisAlignedBB x(IBlockData iblockdata) {
        boolean flag = ((Boolean) iblockdata.get(BlockPistonExtension.SHORT)).booleanValue();

        switch ((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)) {
        case DOWN:
        default:
            return flag ? BlockPistonExtension.K : BlockPistonExtension.D;

        case UP:
            return flag ? BlockPistonExtension.J : BlockPistonExtension.C;

        case NORTH:
            return flag ? BlockPistonExtension.M : BlockPistonExtension.F;

        case SOUTH:
            return flag ? BlockPistonExtension.L : BlockPistonExtension.E;

        case WEST:
            return flag ? BlockPistonExtension.O : BlockPistonExtension.I;

        case EAST:
            return flag ? BlockPistonExtension.N : BlockPistonExtension.G;
        }
    }

    public boolean k(IBlockData iblockdata) {
        return iblockdata.get(BlockPistonExtension.FACING) == EnumDirection.UP;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (entityhuman.abilities.canInstantlyBuild) {
            BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite());
            Block block = world.getType(blockposition1).getBlock();

            if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
                world.setAir(blockposition1);
            }
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite();

        blockposition = blockposition.shift(enumdirection);
        IBlockData iblockdata1 = world.getType(blockposition);

        if ((iblockdata1.getBlock() == Blocks.PISTON || iblockdata1.getBlock() == Blocks.STICKY_PISTON) && ((Boolean) iblockdata1.get(BlockPiston.EXTENDED)).booleanValue()) {
            iblockdata1.getBlock().b(world, blockposition, iblockdata1, 0);
            world.setAir(blockposition);
        }

    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return false;
    }

    public int a(Random random) {
        return 0;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockPistonExtension.FACING);
        BlockPosition blockposition2 = blockposition.shift(enumdirection.opposite());
        IBlockData iblockdata1 = world.getType(blockposition2);

        if (iblockdata1.getBlock() != Blocks.PISTON && iblockdata1.getBlock() != Blocks.STICKY_PISTON) {
            world.setAir(blockposition);
        } else {
            iblockdata1.doPhysics(world, blockposition2, block, blockposition1);
        }

    }

    @Nullable
    public static EnumDirection b(int i) {
        int j = i & 7;

        return j > 5 ? null : EnumDirection.fromType1(j);
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(iblockdata.get(BlockPistonExtension.TYPE) == BlockPistonExtension.EnumPistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockPistonExtension.FACING, b(i)).set(BlockPistonExtension.TYPE, (i & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).a();

        if (iblockdata.get(BlockPistonExtension.TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
            i |= 8;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockPistonExtension.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockPistonExtension.FACING, BlockPistonExtension.TYPE, BlockPistonExtension.SHORT});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == iblockdata.get(BlockPistonExtension.FACING) ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }

    public static enum EnumPistonType implements INamable {

        DEFAULT("normal"), STICKY("sticky");

        private final String c;

        private EnumPistonType(String s) {
            this.c = s;
        }

        public String toString() {
            return this.c;
        }

        public String getName() {
            return this.c;
        }
    }
}
