package net.minecraft.server;

public class BlockEnderChest extends BlockChestAbstract<TileEntityEnderChest> implements IBlockWaterlogged {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean c = BlockProperties.C;
    protected static final VoxelShape d = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final IChatBaseComponent e = new ChatMessage("container.enderchest");

    protected BlockEnderChest(BlockBase.Info blockbase_info) {
        super(blockbase_info, () -> {
            return TileEntityTypes.ENDER_CHEST;
        });
        this.j((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockEnderChest.FACING, EnumDirection.NORTH)).set(BlockEnderChest.c, false));
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockEnderChest.d;
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());

        return (IBlockData) ((IBlockData) this.getBlockData().set(BlockEnderChest.FACING, blockactioncontext.f().opposite())).set(BlockEnderChest.c, fluid.getType() == FluidTypes.WATER);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        InventoryEnderChest inventoryenderchest = entityhuman.getEnderChest();
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (inventoryenderchest != null && tileentity instanceof TileEntityEnderChest) {
            BlockPosition blockposition1 = blockposition.up();

            if (world.getType(blockposition1).isOccluding(world, blockposition1)) {
                return EnumInteractionResult.a(world.isClientSide);
            } else if (world.isClientSide) {
                return EnumInteractionResult.SUCCESS;
            } else {
                TileEntityEnderChest tileentityenderchest = (TileEntityEnderChest) tileentity;

                inventoryenderchest.a(tileentityenderchest);
                entityhuman.openContainer(new TileInventory((i, playerinventory, entityhuman1) -> {
                    return ContainerChest.a(i, playerinventory, inventoryenderchest);
                }, BlockEnderChest.e));
                entityhuman.a(StatisticList.OPEN_ENDERCHEST);
                PiglinAI.a(entityhuman, true);
                return EnumInteractionResult.CONSUME;
            }
        } else {
            return EnumInteractionResult.a(world.isClientSide);
        }
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityEnderChest();
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockEnderChest.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockEnderChest.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockEnderChest.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockEnderChest.FACING, BlockEnderChest.c);
    }

    @Override
    public Fluid d(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockEnderChest.c) ? FluidTypes.WATER.a(false) : super.d(iblockdata);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockEnderChest.c)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
