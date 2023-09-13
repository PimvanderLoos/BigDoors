package net.minecraft.server;

public class BlockBrewingStand extends BlockTileEntity {

    public static final BlockStateBoolean[] HAS_BOTTLE = new BlockStateBoolean[]{BlockProperties.k, BlockProperties.l, BlockProperties.m};
    protected static final VoxelShape b = VoxelShapes.a(Block.a(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D));

    public BlockBrewingStand(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockBrewingStand.HAS_BOTTLE[0], false)).set(BlockBrewingStand.HAS_BOTTLE[1], false)).set(BlockBrewingStand.HAS_BOTTLE[2], false));
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityBrewingStand();
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockBrewingStand.b;
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBrewingStand) {
                entityhuman.openContainer((TileEntityBrewingStand) tileentity);
                entityhuman.a(StatisticList.INTERACT_WITH_BREWINGSTAND);
            }

            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBrewingStand) {
                ((TileEntityBrewingStand) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBrewingStand) {
                InventoryUtils.dropInventory(world, blockposition, (TileEntityBrewingStand) tileentity);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBrewingStand.HAS_BOTTLE[0], BlockBrewingStand.HAS_BOTTLE[1], BlockBrewingStand.HAS_BOTTLE[2]);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
