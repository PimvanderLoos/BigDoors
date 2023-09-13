package net.minecraft.server;

public class BlockEnchantmentTable extends BlockTileEntity {

    protected static final AxisAlignedBB a = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

    protected BlockEnchantmentTable() {
        super(Material.STONE, MaterialMapColor.E);
        this.e(0);
        this.a(CreativeModeTab.c);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockEnchantmentTable.a;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public TileEntity a(World world, int i) {
        return new TileEntityEnchantTable();
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
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
        super.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
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
}
