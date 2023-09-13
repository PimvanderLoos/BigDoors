package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockSeaGrass extends BlockPlant implements IBlockFragilePlantElement, IFluidContainer {

    protected static final VoxelShape a = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    protected BlockSeaGrass(Block.Info block_info) {
        super(block_info);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSeaGrass.a;
    }

    protected boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return Block.a(iblockdata.getCollisionShape(iblockaccess, blockposition), EnumDirection.UP) && iblockdata.getBlock() != Blocks.MAGMA_BLOCK;
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());

        return fluid.a(TagsFluid.WATER) && fluid.g() == 8 ? super.getPlacedState(blockactioncontext) : null;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        IBlockData iblockdata2 = super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);

        if (!iblockdata2.isAir()) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return iblockdata2;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (!world.isClientSide && itemstack.getItem() == Items.SHEARS) {
            entityhuman.b(StatisticList.BLOCK_MINED.b(this));
            entityhuman.applyExhaustion(0.005F);
            a(world, blockposition, new ItemStack(this));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        }

    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public Fluid h(IBlockData iblockdata) {
        return FluidTypes.WATER.a(false);
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        IBlockData iblockdata1 = Blocks.TALL_SEAGRASS.getBlockData();
        IBlockData iblockdata2 = (IBlockData) iblockdata1.set(BlockTallSeaGrass.c, BlockPropertyDoubleBlockHalf.UPPER);
        BlockPosition blockposition1 = blockposition.up();

        if (world.getType(blockposition1).getBlock() == Blocks.WATER) {
            world.setTypeAndData(blockposition, iblockdata1, 2);
            world.setTypeAndData(blockposition1, iblockdata2, 2);
        }

    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return false;
    }

    public int j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return Blocks.WATER.getBlockData().b(iblockaccess, blockposition);
    }
}
