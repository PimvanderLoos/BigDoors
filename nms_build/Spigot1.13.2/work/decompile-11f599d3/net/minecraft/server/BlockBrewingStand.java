package net.minecraft.server;

public class BlockBrewingStand extends BlockTileEntity {

    public static final BlockStateBoolean[] HAS_BOTTLE = new BlockStateBoolean[] { BlockProperties.i, BlockProperties.j, BlockProperties.k};
    protected static final VoxelShape b = VoxelShapes.a(Block.a(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D));

    public BlockBrewingStand(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockBrewingStand.HAS_BOTTLE[0], false)).set(BlockBrewingStand.HAS_BOTTLE[1], false)).set(BlockBrewingStand.HAS_BOTTLE[2], false));
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityBrewingStand();
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockBrewingStand.b;
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBrewingStand) {
                entityhuman.openContainer((TileEntityBrewingStand) tileentity);
                entityhuman.a(StatisticList.INTERACT_WITH_BREWINGSTAND);
            }

            return true;
        }
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBrewingStand) {
                ((TileEntityBrewingStand) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBrewingStand) {
                InventoryUtils.dropInventory(world, blockposition, (TileEntityBrewingStand) tileentity);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBrewingStand.HAS_BOTTLE[0], BlockBrewingStand.HAS_BOTTLE[1], BlockBrewingStand.HAS_BOTTLE[2]);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
