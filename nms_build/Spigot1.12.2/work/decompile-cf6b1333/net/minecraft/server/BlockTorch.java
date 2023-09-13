package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import javax.annotation.Nullable;

public class BlockTorch extends Block {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", new Predicate() {
        public boolean a(@Nullable EnumDirection enumdirection) {
            return enumdirection != EnumDirection.DOWN;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((EnumDirection) object);
        }
    });
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.4000000059604645D, 0.0D, 0.4000000059604645D, 0.6000000238418579D, 0.6000000238418579D, 0.6000000238418579D);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.3499999940395355D, 0.20000000298023224D, 0.699999988079071D, 0.6499999761581421D, 0.800000011920929D, 1.0D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.3499999940395355D, 0.20000000298023224D, 0.0D, 0.6499999761581421D, 0.800000011920929D, 0.30000001192092896D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.699999988079071D, 0.20000000298023224D, 0.3499999940395355D, 1.0D, 0.800000011920929D, 0.6499999761581421D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.0D, 0.20000000298023224D, 0.3499999940395355D, 0.30000001192092896D, 0.800000011920929D, 0.6499999761581421D);

    protected BlockTorch() {
        super(Material.ORIENTABLE);
        this.w(this.blockStateList.getBlockData().set(BlockTorch.FACING, EnumDirection.UP));
        this.a(true);
        this.a(CreativeModeTab.c);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection) iblockdata.get(BlockTorch.FACING)) {
        case EAST:
            return BlockTorch.f;

        case WEST:
            return BlockTorch.e;

        case SOUTH:
            return BlockTorch.d;

        case NORTH:
            return BlockTorch.c;

        default:
            return BlockTorch.b;
        }
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockTorch.k;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    private boolean b(World world, BlockPosition blockposition) {
        Block block = world.getType(blockposition).getBlock();
        boolean flag = block == Blocks.END_GATEWAY || block == Blocks.LIT_PUMPKIN;

        if (world.getType(blockposition).q()) {
            return !flag;
        } else {
            boolean flag1 = block instanceof BlockFence || block == Blocks.GLASS || block == Blocks.COBBLESTONE_WALL || block == Blocks.STAINED_GLASS;

            return flag1 && !flag;
        }
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        Iterator iterator = BlockTorch.FACING.c().iterator();

        EnumDirection enumdirection;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            enumdirection = (EnumDirection) iterator.next();
        } while (!this.a(world, blockposition, enumdirection));

        return true;
    }

    private boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
        IBlockData iblockdata = world.getType(blockposition1);
        Block block = iblockdata.getBlock();
        EnumBlockFaceShape enumblockfaceshape = iblockdata.d(world, blockposition1, enumdirection);

        return enumdirection.equals(EnumDirection.UP) && this.b(world, blockposition1) ? true : (enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN ? !c(block) && enumblockfaceshape == EnumBlockFaceShape.SOLID : false);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        if (this.a(world, blockposition, enumdirection)) {
            return this.getBlockData().set(BlockTorch.FACING, enumdirection);
        } else {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection1;

            do {
                if (!iterator.hasNext()) {
                    return this.getBlockData();
                }

                enumdirection1 = (EnumDirection) iterator.next();
            } while (!this.a(world, blockposition, enumdirection1));

            return this.getBlockData().set(BlockTorch.FACING, enumdirection1);
        }
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.f(world, blockposition, iblockdata);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        this.e(world, blockposition, iblockdata);
    }

    protected boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.f(world, blockposition, iblockdata)) {
            return true;
        } else {
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockTorch.FACING);
            EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.k();
            EnumDirection enumdirection1 = enumdirection.opposite();
            BlockPosition blockposition1 = blockposition.shift(enumdirection1);
            boolean flag = false;

            if (enumdirection_enumaxis.c() && world.getType(blockposition1).d(world, blockposition1, enumdirection) != EnumBlockFaceShape.SOLID) {
                flag = true;
            } else if (enumdirection_enumaxis.b() && !this.b(world, blockposition1)) {
                flag = true;
            }

            if (flag) {
                this.b(world, blockposition, iblockdata, 0);
                world.setAir(blockposition);
                return true;
            } else {
                return false;
            }
        }
    }

    protected boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.getBlock() == this && this.a(world, blockposition, (EnumDirection) iblockdata.get(BlockTorch.FACING))) {
            return true;
        } else {
            if (world.getType(blockposition).getBlock() == this) {
                this.b(world, blockposition, iblockdata, 0);
                world.setAir(blockposition);
            }

            return false;
        }
    }

    public IBlockData fromLegacyData(int i) {
        IBlockData iblockdata = this.getBlockData();

        switch (i) {
        case 1:
            iblockdata = iblockdata.set(BlockTorch.FACING, EnumDirection.EAST);
            break;

        case 2:
            iblockdata = iblockdata.set(BlockTorch.FACING, EnumDirection.WEST);
            break;

        case 3:
            iblockdata = iblockdata.set(BlockTorch.FACING, EnumDirection.SOUTH);
            break;

        case 4:
            iblockdata = iblockdata.set(BlockTorch.FACING, EnumDirection.NORTH);
            break;

        case 5:
        default:
            iblockdata = iblockdata.set(BlockTorch.FACING, EnumDirection.UP);
        }

        return iblockdata;
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i;

        switch ((EnumDirection) iblockdata.get(BlockTorch.FACING)) {
        case EAST:
            i = b0 | 1;
            break;

        case WEST:
            i = b0 | 2;
            break;

        case SOUTH:
            i = b0 | 3;
            break;

        case NORTH:
            i = b0 | 4;
            break;

        case DOWN:
        case UP:
        default:
            i = b0 | 5;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockTorch.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockTorch.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockTorch.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockTorch.FACING});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
