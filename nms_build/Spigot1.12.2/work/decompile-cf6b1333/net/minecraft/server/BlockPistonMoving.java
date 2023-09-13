package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockPistonMoving extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockPistonExtension.FACING;
    public static final BlockStateEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;

    public BlockPistonMoving() {
        super(Material.PISTON);
        this.w(this.blockStateList.getBlockData().set(BlockPistonMoving.FACING, EnumDirection.NORTH).set(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.DEFAULT));
        this.c(-1.0F);
    }

    @Nullable
    public TileEntity a(World world, int i) {
        return null;
    }

    public static TileEntity a(IBlockData iblockdata, EnumDirection enumdirection, boolean flag, boolean flag1) {
        return new TileEntityPiston(iblockdata, enumdirection, flag, flag1);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityPiston) {
            ((TileEntityPiston) tileentity).j();
        } else {
            super.remove(world, blockposition, iblockdata);
        }

    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return false;
    }

    public void postBreak(World world, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonMoving.FACING)).opposite());
        IBlockData iblockdata1 = world.getType(blockposition1);

        if (iblockdata1.getBlock() instanceof BlockPiston && ((Boolean) iblockdata1.get(BlockPiston.EXTENDED)).booleanValue()) {
            world.setAir(blockposition1);
        }

    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!world.isClientSide && world.getTileEntity(blockposition) == null) {
            world.setAir(blockposition);
            return true;
        } else {
            return false;
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.a;
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        if (!world.isClientSide) {
            TileEntityPiston tileentitypiston = this.c(world, blockposition);

            if (tileentitypiston != null) {
                IBlockData iblockdata1 = tileentitypiston.a();

                iblockdata1.getBlock().b(world, blockposition, iblockdata1, 0);
            }
        }
    }

    @Nullable
    public MovingObjectPosition a(IBlockData iblockdata, World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
        return null;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            world.getTileEntity(blockposition);
        }

    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntityPiston tileentitypiston = this.c(iblockaccess, blockposition);

        return tileentitypiston == null ? null : tileentitypiston.a(iblockaccess, blockposition);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        TileEntityPiston tileentitypiston = this.c(world, blockposition);

        if (tileentitypiston != null) {
            tileentitypiston.a(world, blockposition, axisalignedbb, list, entity);
        }

    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntityPiston tileentitypiston = this.c(iblockaccess, blockposition);

        return tileentitypiston != null ? tileentitypiston.a(iblockaccess, blockposition) : BlockPistonMoving.j;
    }

    @Nullable
    private TileEntityPiston c(IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityPiston ? (TileEntityPiston) tileentity : null;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.a;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockPistonMoving.FACING, BlockPistonExtension.b(i)).set(BlockPistonMoving.TYPE, (i & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockPistonMoving.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockPistonMoving.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockPistonMoving.FACING)));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockPistonMoving.FACING)).a();

        if (iblockdata.get(BlockPistonMoving.TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockPistonMoving.FACING, BlockPistonMoving.TYPE});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
