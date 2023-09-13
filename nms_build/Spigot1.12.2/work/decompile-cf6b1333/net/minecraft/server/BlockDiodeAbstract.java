package net.minecraft.server;

import java.util.Random;

public abstract class BlockDiodeAbstract extends BlockFacingHorizontal {

    protected static final AxisAlignedBB c = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected final boolean d;

    protected BlockDiodeAbstract(boolean flag) {
        super(Material.ORIENTABLE);
        this.d = flag;
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockDiodeAbstract.c;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return world.getType(blockposition.down()).q() ? super.canPlace(world, blockposition) : false;
    }

    public boolean b(World world, BlockPosition blockposition) {
        return world.getType(blockposition.down()).q();
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {}

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!this.b((IBlockAccess) world, blockposition, iblockdata)) {
            boolean flag = this.e(world, blockposition, iblockdata);

            if (this.d && !flag) {
                world.setTypeAndData(blockposition, this.z(iblockdata), 2);
            } else if (!this.d) {
                world.setTypeAndData(blockposition, this.y(iblockdata), 2);
                if (!flag) {
                    world.a(blockposition, this.y(iblockdata).getBlock(), this.E(iblockdata), -1);
                }
            }

        }
    }

    protected boolean A(IBlockData iblockdata) {
        return this.d;
    }

    public int c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return iblockdata.a(iblockaccess, blockposition, enumdirection);
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !this.A(iblockdata) ? 0 : (iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection ? this.a(iblockaccess, blockposition, iblockdata) : 0);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (this.b(world, blockposition)) {
            this.g(world, blockposition, iblockdata);
        } else {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this, false);
            }

        }
    }

    protected void g(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.b((IBlockAccess) world, blockposition, iblockdata)) {
            boolean flag = this.e(world, blockposition, iblockdata);

            if (this.d != flag && !world.a(blockposition, (Block) this)) {
                byte b0 = -1;

                if (this.i(world, blockposition, iblockdata)) {
                    b0 = -3;
                } else if (this.d) {
                    b0 = -2;
                }

                world.a(blockposition, this, this.x(iblockdata), b0);
            }

        }
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return false;
    }

    protected boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return this.f(world, blockposition, iblockdata) > 0;
    }

    protected int f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockDiodeAbstract.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        int i = world.getBlockFacePower(blockposition1, enumdirection);

        if (i >= 15) {
            return i;
        } else {
            IBlockData iblockdata1 = world.getType(blockposition1);

            return Math.max(i, iblockdata1.getBlock() == Blocks.REDSTONE_WIRE ? ((Integer) iblockdata1.get(BlockRedstoneWire.POWER)).intValue() : 0);
        }
    }

    protected int c(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockDiodeAbstract.FACING);
        EnumDirection enumdirection1 = enumdirection.e();
        EnumDirection enumdirection2 = enumdirection.f();

        return Math.max(this.a(iblockaccess, blockposition.shift(enumdirection1), enumdirection1), this.a(iblockaccess, blockposition.shift(enumdirection2), enumdirection2));
    }

    protected int a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        Block block = iblockdata.getBlock();

        return this.B(iblockdata) ? (block == Blocks.REDSTONE_BLOCK ? 15 : (block == Blocks.REDSTONE_WIRE ? ((Integer) iblockdata.get(BlockRedstoneWire.POWER)).intValue() : iblockaccess.getBlockPower(blockposition, enumdirection))) : 0;
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockDiodeAbstract.FACING, entityliving.getDirection().opposite());
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (this.e(world, blockposition, iblockdata)) {
            world.a(blockposition, (Block) this, 1);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.h(world, blockposition, iblockdata);
    }

    protected void h(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockDiodeAbstract.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());

        world.a(blockposition1, (Block) this, blockposition);
        world.a(blockposition1, (Block) this, enumdirection);
    }

    public void postBreak(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.d) {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this, false);
            }
        }

        super.postBreak(world, blockposition, iblockdata);
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    protected boolean B(IBlockData iblockdata) {
        return iblockdata.m();
    }

    protected int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return 15;
    }

    public static boolean isDiode(IBlockData iblockdata) {
        return Blocks.UNPOWERED_REPEATER.D(iblockdata) || Blocks.UNPOWERED_COMPARATOR.D(iblockdata);
    }

    public boolean D(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();

        return block == this.y(this.getBlockData()).getBlock() || block == this.z(this.getBlockData()).getBlock();
    }

    public boolean i(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockDiodeAbstract.FACING)).opposite();
        BlockPosition blockposition1 = blockposition.shift(enumdirection);

        return isDiode(world.getType(blockposition1)) ? world.getType(blockposition1).get(BlockDiodeAbstract.FACING) != enumdirection : false;
    }

    protected int E(IBlockData iblockdata) {
        return this.x(iblockdata);
    }

    protected abstract int x(IBlockData iblockdata);

    protected abstract IBlockData y(IBlockData iblockdata);

    protected abstract IBlockData z(IBlockData iblockdata);

    public boolean d(Block block) {
        return this.D(block.getBlockData());
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }
}
