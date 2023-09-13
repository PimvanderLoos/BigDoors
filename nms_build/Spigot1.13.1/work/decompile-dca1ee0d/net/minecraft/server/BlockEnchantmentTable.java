package net.minecraft.server;

public class BlockEnchantmentTable extends BlockTileEntity {

    protected static final VoxelShape a = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    protected BlockEnchantmentTable(Block.Info block_info) {
        super(block_info);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockEnchantmentTable.a;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityEnchantTable();
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityEnchantTable) {
                entityhuman.openTileEntity((TileEntityEnchantTable) tileentity);
            }

            return true;
        }
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityEnchantTable) {
                ((TileEntityEnchantTable) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
