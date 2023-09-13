package net.minecraft.server;

public class BlockHopper extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockProperties.I;
    public static final BlockStateBoolean ENABLED = BlockProperties.e;
    private static final VoxelShape c = Block.a(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape o = Block.a(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
    private static final VoxelShape p = VoxelShapes.a(BlockHopper.o, BlockHopper.c);
    private static final VoxelShape q = VoxelShapes.a(BlockHopper.p, IHopper.a, OperatorBoolean.ONLY_FIRST);
    private static final VoxelShape r = VoxelShapes.a(BlockHopper.q, Block.a(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
    private static final VoxelShape s = VoxelShapes.a(BlockHopper.q, Block.a(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
    private static final VoxelShape t = VoxelShapes.a(BlockHopper.q, Block.a(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
    private static final VoxelShape u = VoxelShapes.a(BlockHopper.q, Block.a(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
    private static final VoxelShape v = VoxelShapes.a(BlockHopper.q, Block.a(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
    private static final VoxelShape w = IHopper.a;
    private static final VoxelShape x = VoxelShapes.a(IHopper.a, Block.a(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
    private static final VoxelShape y = VoxelShapes.a(IHopper.a, Block.a(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
    private static final VoxelShape z = VoxelShapes.a(IHopper.a, Block.a(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
    private static final VoxelShape A = VoxelShapes.a(IHopper.a, Block.a(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

    public BlockHopper(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockHopper.FACING, EnumDirection.DOWN)).set(BlockHopper.ENABLED, true));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection) iblockdata.get(BlockHopper.FACING)) {
        case DOWN:
            return BlockHopper.r;
        case NORTH:
            return BlockHopper.t;
        case SOUTH:
            return BlockHopper.u;
        case WEST:
            return BlockHopper.v;
        case EAST:
            return BlockHopper.s;
        default:
            return BlockHopper.q;
        }
    }

    public VoxelShape h(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection) iblockdata.get(BlockHopper.FACING)) {
        case DOWN:
            return BlockHopper.w;
        case NORTH:
            return BlockHopper.y;
        case SOUTH:
            return BlockHopper.z;
        case WEST:
            return BlockHopper.A;
        case EAST:
            return BlockHopper.x;
        default:
            return IHopper.a;
        }
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace().opposite();

        return (IBlockData) ((IBlockData) this.getBlockData().set(BlockHopper.FACING, enumdirection.k() == EnumDirection.EnumAxis.Y ? EnumDirection.DOWN : enumdirection)).set(BlockHopper.ENABLED, true);
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityHopper();
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                ((TileEntityHopper) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public boolean r(IBlockData iblockdata) {
        return true;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            this.a(world, blockposition, iblockdata);
        }
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                entityhuman.openContainer((TileEntityHopper) tileentity);
                entityhuman.a(StatisticList.INSPECT_HOPPER);
            }

            return true;
        }
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        this.a(world, blockposition, iblockdata);
    }

    private void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = !world.isBlockIndirectlyPowered(blockposition);

        if (flag != (Boolean) iblockdata.get(BlockHopper.ENABLED)) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockHopper.ENABLED, flag), 4);
        }

    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                InventoryUtils.dropInventory(world, blockposition, (TileEntityHopper) tileentity);
                world.updateAdjacentComparators(blockposition, this);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    public TextureType c() {
        return TextureType.CUTOUT_MIPPED;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockHopper.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockHopper.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockHopper.FACING)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockHopper.FACING, BlockHopper.ENABLED);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? EnumBlockFaceShape.BOWL : EnumBlockFaceShape.UNDEFINED;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityHopper) {
            ((TileEntityHopper) tileentity).a(entity);
        }

    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
