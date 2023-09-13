package net.minecraft.server;

import com.google.common.base.MoreObjects;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockTripwireHook extends Block {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    public static final BlockStateBoolean ATTACHED = BlockStateBoolean.of("attached");
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.3125D, 0.0D, 0.625D, 0.6875D, 0.625D, 1.0D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.3125D, 0.0D, 0.0D, 0.6875D, 0.625D, 0.375D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.625D, 0.0D, 0.3125D, 1.0D, 0.625D, 0.6875D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.0D, 0.3125D, 0.375D, 0.625D, 0.6875D);

    public BlockTripwireHook() {
        super(Material.ORIENTABLE);
        this.w(this.blockStateList.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.NORTH).set(BlockTripwireHook.POWERED, Boolean.valueOf(false)).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(false)));
        this.a(CreativeModeTab.d);
        this.a(true);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection) iblockdata.get(BlockTripwireHook.FACING)) {
        case EAST:
        default:
            return BlockTripwireHook.g;

        case WEST:
            return BlockTripwireHook.f;

        case SOUTH:
            return BlockTripwireHook.e;

        case NORTH:
            return BlockTripwireHook.d;
        }
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockTripwireHook.k;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        EnumDirection enumdirection1 = enumdirection.opposite();
        BlockPosition blockposition1 = blockposition.shift(enumdirection1);
        IBlockData iblockdata = world.getType(blockposition1);
        boolean flag = c(iblockdata.getBlock());

        return !flag && enumdirection.k().c() && iblockdata.d(world, blockposition1, enumdirection) == EnumBlockFaceShape.SOLID && !iblockdata.m();
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        EnumDirection enumdirection;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            enumdirection = (EnumDirection) iterator.next();
        } while (!this.canPlace(world, blockposition, enumdirection));

        return true;
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = this.getBlockData().set(BlockTripwireHook.POWERED, Boolean.valueOf(false)).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(false));

        if (enumdirection.k().c()) {
            iblockdata = iblockdata.set(BlockTripwireHook.FACING, enumdirection);
        }

        return iblockdata;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        this.a(world, blockposition, iblockdata, false, false, -1, (IBlockData) null);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (block != this) {
            if (this.e(world, blockposition, iblockdata)) {
                EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockTripwireHook.FACING);

                if (!this.canPlace(world, blockposition, enumdirection)) {
                    this.b(world, blockposition, iblockdata, 0);
                    world.setAir(blockposition);
                }
            }

        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag, boolean flag1, int i, @Nullable IBlockData iblockdata1) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockTripwireHook.FACING);
        boolean flag2 = ((Boolean) iblockdata.get(BlockTripwireHook.ATTACHED)).booleanValue();
        boolean flag3 = ((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue();
        boolean flag4 = !flag;
        boolean flag5 = false;
        int j = 0;
        IBlockData[] aiblockdata = new IBlockData[42];

        BlockPosition blockposition1;

        for (int k = 1; k < 42; ++k) {
            blockposition1 = blockposition.shift(enumdirection, k);
            IBlockData iblockdata2 = world.getType(blockposition1);

            if (iblockdata2.getBlock() == Blocks.TRIPWIRE_HOOK) {
                if (iblockdata2.get(BlockTripwireHook.FACING) == enumdirection.opposite()) {
                    j = k;
                }
                break;
            }

            if (iblockdata2.getBlock() != Blocks.TRIPWIRE && k != i) {
                aiblockdata[k] = null;
                flag4 = false;
            } else {
                if (k == i) {
                    iblockdata2 = (IBlockData) MoreObjects.firstNonNull(iblockdata1, iblockdata2);
                }

                boolean flag6 = !((Boolean) iblockdata2.get(BlockTripwire.DISARMED)).booleanValue();
                boolean flag7 = ((Boolean) iblockdata2.get(BlockTripwire.POWERED)).booleanValue();

                flag5 |= flag6 && flag7;
                aiblockdata[k] = iblockdata2;
                if (k == i) {
                    world.a(blockposition, (Block) this, this.a(world));
                    flag4 &= flag6;
                }
            }
        }

        flag4 &= j > 1;
        flag5 &= flag4;
        IBlockData iblockdata3 = this.getBlockData().set(BlockTripwireHook.ATTACHED, Boolean.valueOf(flag4)).set(BlockTripwireHook.POWERED, Boolean.valueOf(flag5));

        if (j > 0) {
            blockposition1 = blockposition.shift(enumdirection, j);
            EnumDirection enumdirection1 = enumdirection.opposite();

            world.setTypeAndData(blockposition1, iblockdata3.set(BlockTripwireHook.FACING, enumdirection1), 3);
            this.a(world, blockposition1, enumdirection1);
            this.a(world, blockposition1, flag4, flag5, flag2, flag3);
        }

        this.a(world, blockposition, flag4, flag5, flag2, flag3);
        if (!flag) {
            world.setTypeAndData(blockposition, iblockdata3.set(BlockTripwireHook.FACING, enumdirection), 3);
            if (flag1) {
                this.a(world, blockposition, enumdirection);
            }
        }

        if (flag2 != flag4) {
            for (int l = 1; l < j; ++l) {
                BlockPosition blockposition2 = blockposition.shift(enumdirection, l);
                IBlockData iblockdata4 = aiblockdata[l];

                if (iblockdata4 != null && world.getType(blockposition2).getMaterial() != Material.AIR) {
                    world.setTypeAndData(blockposition2, iblockdata4.set(BlockTripwireHook.ATTACHED, Boolean.valueOf(flag4)), 3);
                }
            }
        }

    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {}

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        this.a(world, blockposition, iblockdata, false, true, -1, (IBlockData) null);
    }

    private void a(World world, BlockPosition blockposition, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        if (flag1 && !flag3) {
            world.a((EntityHuman) null, blockposition, SoundEffects.ia, SoundCategory.BLOCKS, 0.4F, 0.6F);
        } else if (!flag1 && flag3) {
            world.a((EntityHuman) null, blockposition, SoundEffects.hZ, SoundCategory.BLOCKS, 0.4F, 0.5F);
        } else if (flag && !flag2) {
            world.a((EntityHuman) null, blockposition, SoundEffects.hY, SoundCategory.BLOCKS, 0.4F, 0.7F);
        } else if (!flag && flag2) {
            world.a((EntityHuman) null, blockposition, SoundEffects.ib, SoundCategory.BLOCKS, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
        }

    }

    private void a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        world.applyPhysics(blockposition, this, false);
        world.applyPhysics(blockposition.shift(enumdirection.opposite()), this, false);
    }

    private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.canPlace(world, blockposition)) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            return false;
        } else {
            return true;
        }
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = ((Boolean) iblockdata.get(BlockTripwireHook.ATTACHED)).booleanValue();
        boolean flag1 = ((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue();

        if (flag || flag1) {
            this.a(world, blockposition, iblockdata, true, false, -1, (IBlockData) null);
        }

        if (flag1) {
            world.applyPhysics(blockposition, this, false);
            world.applyPhysics(blockposition.shift(((EnumDirection) iblockdata.get(BlockTripwireHook.FACING)).opposite()), this, false);
        }

        super.remove(world, blockposition, iblockdata);
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return ((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue() ? 15 : 0;
    }

    public int c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue() ? 0 : (iblockdata.get(BlockTripwireHook.FACING) == enumdirection ? 15 : 0);
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.fromType2(i & 3)).set(BlockTripwireHook.POWERED, Boolean.valueOf((i & 8) > 0)).set(BlockTripwireHook.ATTACHED, Boolean.valueOf((i & 4) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockTripwireHook.FACING)).get2DRotationValue();

        if (((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue()) {
            i |= 8;
        }

        if (((Boolean) iblockdata.get(BlockTripwireHook.ATTACHED)).booleanValue()) {
            i |= 4;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockTripwireHook.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockTripwireHook.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockTripwireHook.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockTripwireHook.FACING, BlockTripwireHook.POWERED, BlockTripwireHook.ATTACHED});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
