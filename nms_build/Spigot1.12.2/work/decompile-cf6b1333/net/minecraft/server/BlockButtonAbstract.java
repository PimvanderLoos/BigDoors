package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class BlockButtonAbstract extends BlockDirectional {

    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.3125D, 0.875D, 0.375D, 0.6875D, 1.0D, 0.625D);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.3125D, 0.0D, 0.375D, 0.6875D, 0.125D, 0.625D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.3125D, 0.375D, 0.875D, 0.6875D, 0.625D, 1.0D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.3125D, 0.375D, 0.0D, 0.6875D, 0.625D, 0.125D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.875D, 0.375D, 0.3125D, 1.0D, 0.625D, 0.6875D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.375D, 0.3125D, 0.125D, 0.625D, 0.6875D);
    protected static final AxisAlignedBB B = new AxisAlignedBB(0.3125D, 0.9375D, 0.375D, 0.6875D, 1.0D, 0.625D);
    protected static final AxisAlignedBB C = new AxisAlignedBB(0.3125D, 0.0D, 0.375D, 0.6875D, 0.0625D, 0.625D);
    protected static final AxisAlignedBB D = new AxisAlignedBB(0.3125D, 0.375D, 0.9375D, 0.6875D, 0.625D, 1.0D);
    protected static final AxisAlignedBB E = new AxisAlignedBB(0.3125D, 0.375D, 0.0D, 0.6875D, 0.625D, 0.0625D);
    protected static final AxisAlignedBB F = new AxisAlignedBB(0.9375D, 0.375D, 0.3125D, 1.0D, 0.625D, 0.6875D);
    protected static final AxisAlignedBB G = new AxisAlignedBB(0.0D, 0.375D, 0.3125D, 0.0625D, 0.625D, 0.6875D);
    private final boolean I;

    protected BlockButtonAbstract(boolean flag) {
        super(Material.ORIENTABLE);
        this.w(this.blockStateList.getBlockData().set(BlockButtonAbstract.FACING, EnumDirection.NORTH).set(BlockButtonAbstract.POWERED, Boolean.valueOf(false)));
        this.a(true);
        this.a(CreativeModeTab.d);
        this.I = flag;
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockButtonAbstract.k;
    }

    public int a(World world) {
        return this.I ? 30 : 20;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return a(world, blockposition, enumdirection);
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (a(world, blockposition, enumdirection)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
        IBlockData iblockdata = world.getType(blockposition1);
        boolean flag = iblockdata.d(world, blockposition1, enumdirection) == EnumBlockFaceShape.SOLID;
        Block block = iblockdata.getBlock();

        return enumdirection == EnumDirection.UP ? block == Blocks.HOPPER || !b(block) && flag : !c(block) && flag;
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return a(world, blockposition, enumdirection) ? this.getBlockData().set(BlockButtonAbstract.FACING, enumdirection).set(BlockButtonAbstract.POWERED, Boolean.valueOf(false)) : this.getBlockData().set(BlockButtonAbstract.FACING, EnumDirection.DOWN).set(BlockButtonAbstract.POWERED, Boolean.valueOf(false));
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (this.e(world, blockposition, iblockdata) && !a(world, blockposition, (EnumDirection) iblockdata.get(BlockButtonAbstract.FACING))) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }

    }

    private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.canPlace(world, blockposition)) {
            return true;
        } else {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            return false;
        }
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockButtonAbstract.FACING);
        boolean flag = ((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)).booleanValue();

        switch (enumdirection) {
        case EAST:
            return flag ? BlockButtonAbstract.G : BlockButtonAbstract.g;

        case WEST:
            return flag ? BlockButtonAbstract.F : BlockButtonAbstract.f;

        case SOUTH:
            return flag ? BlockButtonAbstract.E : BlockButtonAbstract.e;

        case NORTH:
        default:
            return flag ? BlockButtonAbstract.D : BlockButtonAbstract.d;

        case UP:
            return flag ? BlockButtonAbstract.C : BlockButtonAbstract.c;

        case DOWN:
            return flag ? BlockButtonAbstract.B : BlockButtonAbstract.b;
        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)).booleanValue()) {
            return true;
        } else {
            world.setTypeAndData(blockposition, iblockdata.set(BlockButtonAbstract.POWERED, Boolean.valueOf(true)), 3);
            world.b(blockposition, blockposition);
            this.a(entityhuman, world, blockposition);
            this.c(world, blockposition, (EnumDirection) iblockdata.get(BlockButtonAbstract.FACING));
            world.a(blockposition, (Block) this, this.a(world));
            return true;
        }
    }

    protected abstract void a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition);

    protected abstract void b(World world, BlockPosition blockposition);

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)).booleanValue()) {
            this.c(world, blockposition, (EnumDirection) iblockdata.get(BlockButtonAbstract.FACING));
        }

        super.remove(world, blockposition, iblockdata);
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return ((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)).booleanValue() ? 15 : 0;
    }

    public int c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)).booleanValue() ? 0 : (iblockdata.get(BlockButtonAbstract.FACING) == enumdirection ? 15 : 0);
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {}

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            if (((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)).booleanValue()) {
                if (this.I) {
                    this.d(iblockdata, world, blockposition);
                } else {
                    world.setTypeUpdate(blockposition, iblockdata.set(BlockButtonAbstract.POWERED, Boolean.valueOf(false)));
                    this.c(world, blockposition, (EnumDirection) iblockdata.get(BlockButtonAbstract.FACING));
                    this.b(world, blockposition);
                    world.b(blockposition, blockposition);
                }

            }
        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        if (!world.isClientSide) {
            if (this.I) {
                if (!((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)).booleanValue()) {
                    this.d(iblockdata, world, blockposition);
                }
            }
        }
    }

    private void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        List list = world.a(EntityArrow.class, iblockdata.e(world, blockposition).a(blockposition));
        boolean flag = !list.isEmpty();
        boolean flag1 = ((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)).booleanValue();

        if (flag && !flag1) {
            world.setTypeUpdate(blockposition, iblockdata.set(BlockButtonAbstract.POWERED, Boolean.valueOf(true)));
            this.c(world, blockposition, (EnumDirection) iblockdata.get(BlockButtonAbstract.FACING));
            world.b(blockposition, blockposition);
            this.a((EntityHuman) null, world, blockposition);
        }

        if (!flag && flag1) {
            world.setTypeUpdate(blockposition, iblockdata.set(BlockButtonAbstract.POWERED, Boolean.valueOf(false)));
            this.c(world, blockposition, (EnumDirection) iblockdata.get(BlockButtonAbstract.FACING));
            world.b(blockposition, blockposition);
            this.b(world, blockposition);
        }

        if (flag) {
            world.a(new BlockPosition(blockposition), (Block) this, this.a(world));
        }

    }

    private void c(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        world.applyPhysics(blockposition, this, false);
        world.applyPhysics(blockposition.shift(enumdirection.opposite()), this, false);
    }

    public IBlockData fromLegacyData(int i) {
        EnumDirection enumdirection;

        switch (i & 7) {
        case 0:
            enumdirection = EnumDirection.DOWN;
            break;

        case 1:
            enumdirection = EnumDirection.EAST;
            break;

        case 2:
            enumdirection = EnumDirection.WEST;
            break;

        case 3:
            enumdirection = EnumDirection.SOUTH;
            break;

        case 4:
            enumdirection = EnumDirection.NORTH;
            break;

        case 5:
        default:
            enumdirection = EnumDirection.UP;
        }

        return this.getBlockData().set(BlockButtonAbstract.FACING, enumdirection).set(BlockButtonAbstract.POWERED, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        int i;

        switch ((EnumDirection) iblockdata.get(BlockButtonAbstract.FACING)) {
        case EAST:
            i = 1;
            break;

        case WEST:
            i = 2;
            break;

        case SOUTH:
            i = 3;
            break;

        case NORTH:
            i = 4;
            break;

        case UP:
        default:
            i = 5;
            break;

        case DOWN:
            i = 0;
        }

        if (((Boolean) iblockdata.get(BlockButtonAbstract.POWERED)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockButtonAbstract.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockButtonAbstract.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockButtonAbstract.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockButtonAbstract.FACING, BlockButtonAbstract.POWERED});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
