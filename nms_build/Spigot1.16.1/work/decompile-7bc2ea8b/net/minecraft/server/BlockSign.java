package net.minecraft.server;

public abstract class BlockSign extends BlockTileEntity implements IBlockWaterlogged {

    public static final BlockStateBoolean a = BlockProperties.C;
    protected static final VoxelShape b = Block.a(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private final BlockPropertyWood c;

    protected BlockSign(BlockBase.Info blockbase_info, BlockPropertyWood blockpropertywood) {
        super(blockbase_info);
        this.c = blockpropertywood;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockSign.a)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockSign.b;
    }

    @Override
    public boolean ak_() {
        return true;
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntitySign();
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = itemstack.getItem() instanceof ItemDye && entityhuman.abilities.mayBuild;

        if (world.isClientSide) {
            return flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.CONSUME;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntitySign) {
                TileEntitySign tileentitysign = (TileEntitySign) tileentity;

                if (flag) {
                    boolean flag1 = tileentitysign.setColor(((ItemDye) itemstack.getItem()).d());

                    if (flag1 && !entityhuman.isCreative()) {
                        itemstack.subtract(1);
                    }
                }

                return tileentitysign.b(entityhuman) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }

    @Override
    public Fluid d(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockSign.a) ? FluidTypes.WATER.a(false) : super.d(iblockdata);
    }
}
