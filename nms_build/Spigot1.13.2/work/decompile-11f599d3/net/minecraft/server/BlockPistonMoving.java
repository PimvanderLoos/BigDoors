package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockPistonMoving extends BlockTileEntity {

    public static final BlockStateDirection a = BlockPistonExtension.FACING;
    public static final BlockStateEnum<BlockPropertyPistonType> b = BlockPistonExtension.TYPE;

    public BlockPistonMoving(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockPistonMoving.a, EnumDirection.NORTH)).set(BlockPistonMoving.b, BlockPropertyPistonType.DEFAULT));
    }

    @Nullable
    public TileEntity a(IBlockAccess iblockaccess) {
        return null;
    }

    public static TileEntity a(IBlockData iblockdata, EnumDirection enumdirection, boolean flag, boolean flag1) {
        return new TileEntityPiston(iblockdata, enumdirection, flag, flag1);
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityPiston) {
                ((TileEntityPiston) tileentity).j();
            } else {
                super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            }

        }
    }

    public void postBreak(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonMoving.a)).opposite());
        IBlockData iblockdata1 = generatoraccess.getType(blockposition1);

        if (iblockdata1.getBlock() instanceof BlockPiston && (Boolean) iblockdata1.get(BlockPiston.EXTENDED)) {
            generatoraccess.setAir(blockposition1);
        }

    }

    public boolean f(IBlockData iblockdata) {
        return false;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!world.isClientSide && world.getTileEntity(blockposition) == null) {
            world.setAir(blockposition);
            return true;
        } else {
            return false;
        }
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        if (!world.isClientSide) {
            TileEntityPiston tileentitypiston = this.a((IBlockAccess) world, blockposition);

            if (tileentitypiston != null) {
                tileentitypiston.i().a(world, blockposition, 0);
            }
        }
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    public VoxelShape f(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntityPiston tileentitypiston = this.a(iblockaccess, blockposition);

        return tileentitypiston != null ? tileentitypiston.a(iblockaccess, blockposition) : VoxelShapes.a();
    }

    @Nullable
    private TileEntityPiston a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityPiston ? (TileEntityPiston) tileentity : null;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.a;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockPistonMoving.a, enumblockrotation.a((EnumDirection) iblockdata.get(BlockPistonMoving.a)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockPistonMoving.a)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPistonMoving.a, BlockPistonMoving.b);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
