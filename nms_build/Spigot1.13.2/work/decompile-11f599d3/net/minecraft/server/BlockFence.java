package net.minecraft.server;

public class BlockFence extends BlockTall {

    private final VoxelShape[] t;

    public BlockFence(Block.Info block_info) {
        super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockFence.NORTH, false)).set(BlockFence.EAST, false)).set(BlockFence.SOUTH, false)).set(BlockFence.WEST, false)).set(BlockFence.p, false));
        this.t = this.a(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
    }

    public VoxelShape g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.t[this.k(iblockdata)];
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    public boolean a(IBlockData iblockdata, EnumBlockFaceShape enumblockfaceshape) {
        Block block = iblockdata.getBlock();
        boolean flag = enumblockfaceshape == EnumBlockFaceShape.MIDDLE_POLE && (iblockdata.getMaterial() == this.material || block instanceof BlockFenceGate);

        return !f(block) && enumblockfaceshape == EnumBlockFaceShape.SOLID || flag;
    }

    public static boolean f(Block block) {
        return Block.b(block) || block == Blocks.BARRIER || block == Blocks.MELON || block == Blocks.PUMPKIN || block == Blocks.CARVED_PUMPKIN || block == Blocks.JACK_O_LANTERN || block == Blocks.FROSTED_ICE || block == Blocks.TNT;
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!world.isClientSide) {
            return ItemLeash.a(entityhuman, world, blockposition);
        } else {
            ItemStack itemstack = entityhuman.b(enumhand);

            return itemstack.getItem() == Items.LEAD || itemstack.isEmpty();
        }
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        BlockPosition blockposition1 = blockposition.north();
        BlockPosition blockposition2 = blockposition.east();
        BlockPosition blockposition3 = blockposition.south();
        BlockPosition blockposition4 = blockposition.west();
        IBlockData iblockdata = world.getType(blockposition1);
        IBlockData iblockdata1 = world.getType(blockposition2);
        IBlockData iblockdata2 = world.getType(blockposition3);
        IBlockData iblockdata3 = world.getType(blockposition4);

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) super.getPlacedState(blockactioncontext).set(BlockFence.NORTH, this.a(iblockdata, iblockdata.c(world, blockposition1, EnumDirection.SOUTH)))).set(BlockFence.EAST, this.a(iblockdata1, iblockdata1.c(world, blockposition2, EnumDirection.WEST)))).set(BlockFence.SOUTH, this.a(iblockdata2, iblockdata2.c(world, blockposition3, EnumDirection.NORTH)))).set(BlockFence.WEST, this.a(iblockdata3, iblockdata3.c(world, blockposition4, EnumDirection.EAST)))).set(BlockFence.p, fluid.c() == FluidTypes.WATER);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockFence.p)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return enumdirection.k().d() == EnumDirection.EnumDirectionLimit.HORIZONTAL ? (IBlockData) iblockdata.set((IBlockState) BlockFence.q.get(enumdirection), this.a(iblockdata1, iblockdata1.c(generatoraccess, blockposition1, enumdirection.opposite()))) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockFence.NORTH, BlockFence.EAST, BlockFence.WEST, BlockFence.SOUTH, BlockFence.p);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN ? EnumBlockFaceShape.MIDDLE_POLE : EnumBlockFaceShape.CENTER;
    }
}
