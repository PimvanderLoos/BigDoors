package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockConduit extends BlockTileEntity {

    protected static final VoxelShape a = Block.a(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

    public BlockConduit(Block.Info block_info) {
        super(block_info);
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityConduit();
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    public Fluid h(IBlockData iblockdata) {
        return FluidTypes.c.a(false);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        Fluid fluid = iworldreader.b(blockposition);

        return fluid.a(TagsFluid.a) && fluid.g() == 8;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        generatoraccess.H().a(blockposition, FluidTypes.c, FluidTypes.c.a((IWorldReader) generatoraccess));
        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockConduit.a;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBeacon) {
                ((TileEntityBeacon) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
