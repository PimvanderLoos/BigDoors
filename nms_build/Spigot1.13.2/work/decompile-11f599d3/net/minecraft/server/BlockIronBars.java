package net.minecraft.server;

public class BlockIronBars extends BlockTall {

    protected BlockIronBars(Block.Info block_info) {
        super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockIronBars.NORTH, false)).set(BlockIronBars.EAST, false)).set(BlockIronBars.SOUTH, false)).set(BlockIronBars.WEST, false)).set(BlockIronBars.p, false));
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        BlockPosition blockposition1 = blockposition.north();
        BlockPosition blockposition2 = blockposition.south();
        BlockPosition blockposition3 = blockposition.west();
        BlockPosition blockposition4 = blockposition.east();
        IBlockData iblockdata = world.getType(blockposition1);
        IBlockData iblockdata1 = world.getType(blockposition2);
        IBlockData iblockdata2 = world.getType(blockposition3);
        IBlockData iblockdata3 = world.getType(blockposition4);

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockIronBars.NORTH, this.a(iblockdata, iblockdata.c(world, blockposition1, EnumDirection.SOUTH)))).set(BlockIronBars.SOUTH, this.a(iblockdata1, iblockdata1.c(world, blockposition2, EnumDirection.NORTH)))).set(BlockIronBars.WEST, this.a(iblockdata2, iblockdata2.c(world, blockposition3, EnumDirection.EAST)))).set(BlockIronBars.EAST, this.a(iblockdata3, iblockdata3.c(world, blockposition4, EnumDirection.WEST)))).set(BlockIronBars.p, fluid.c() == FluidTypes.WATER);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockIronBars.p)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return enumdirection.k().c() ? (IBlockData) iblockdata.set((IBlockState) BlockIronBars.q.get(enumdirection), this.a(iblockdata1, iblockdata1.c(generatoraccess, blockposition1, enumdirection.opposite()))) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public final boolean a(IBlockData iblockdata, EnumBlockFaceShape enumblockfaceshape) {
        Block block = iblockdata.getBlock();

        return !f(block) && enumblockfaceshape == EnumBlockFaceShape.SOLID || enumblockfaceshape == EnumBlockFaceShape.MIDDLE_POLE_THIN;
    }

    public static boolean f(Block block) {
        return block instanceof BlockShulkerBox || block instanceof BlockLeaves || block == Blocks.BEACON || block == Blocks.CAULDRON || block == Blocks.GLOWSTONE || block == Blocks.ICE || block == Blocks.SEA_LANTERN || block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD || block == Blocks.MELON || block == Blocks.PUMPKIN || block == Blocks.CARVED_PUMPKIN || block == Blocks.JACK_O_LANTERN || block == Blocks.BARRIER;
    }

    protected boolean X_() {
        return true;
    }

    public TextureType c() {
        return TextureType.CUTOUT_MIPPED;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockIronBars.NORTH, BlockIronBars.EAST, BlockIronBars.WEST, BlockIronBars.SOUTH, BlockIronBars.p);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN ? EnumBlockFaceShape.MIDDLE_POLE_THIN : EnumBlockFaceShape.CENTER_SMALL;
    }
}
