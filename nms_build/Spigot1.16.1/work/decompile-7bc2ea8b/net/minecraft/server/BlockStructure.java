package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockStructure extends BlockTileEntity {

    public static final BlockStateEnum<BlockPropertyStructureMode> a = BlockProperties.aM;

    protected BlockStructure(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityStructure();
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityStructure ? (((TileEntityStructure) tileentity).a(entityhuman) ? EnumInteractionResult.a(world.isClientSide) : EnumInteractionResult.PASS) : EnumInteractionResult.PASS;
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        if (!world.isClientSide) {
            if (entityliving != null) {
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity instanceof TileEntityStructure) {
                    ((TileEntityStructure) tileentity).setAuthor(entityliving);
                }
            }

        }
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockStructure.a, BlockPropertyStructureMode.DATA);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockStructure.a);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityStructure) {
                TileEntityStructure tileentitystructure = (TileEntityStructure) tileentity;
                boolean flag1 = world.isBlockIndirectlyPowered(blockposition);
                boolean flag2 = tileentitystructure.H();

                if (flag1 && !flag2) {
                    tileentitystructure.d(true);
                    this.a(tileentitystructure);
                } else if (!flag1 && flag2) {
                    tileentitystructure.d(false);
                }

            }
        }
    }

    private void a(TileEntityStructure tileentitystructure) {
        switch (tileentitystructure.getUsageMode()) {
            case SAVE:
                tileentitystructure.b(false);
                break;
            case LOAD:
                tileentitystructure.c(false);
                break;
            case CORNER:
                tileentitystructure.F();
            case DATA:
        }

    }
}
