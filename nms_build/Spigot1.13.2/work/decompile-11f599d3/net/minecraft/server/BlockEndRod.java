package net.minecraft.server;

public class BlockEndRod extends BlockDirectional {

    protected static final VoxelShape b = Block.a(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape c = Block.a(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape o = Block.a(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

    protected BlockEndRod(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockEndRod.FACING, EnumDirection.UP));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockEndRod.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockEndRod.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) iblockdata.set(BlockEndRod.FACING, enumblockmirror.b((EnumDirection) iblockdata.get(BlockEndRod.FACING)));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch (((EnumDirection) iblockdata.get(BlockEndRod.FACING)).k()) {
        case X:
        default:
            return BlockEndRod.o;
        case Z:
            return BlockEndRod.c;
        case Y:
            return BlockEndRod.b;
        }
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().shift(enumdirection.opposite()));

        return iblockdata.getBlock() == this && iblockdata.get(BlockEndRod.FACING) == enumdirection ? (IBlockData) this.getBlockData().set(BlockEndRod.FACING, enumdirection.opposite()) : (IBlockData) this.getBlockData().set(BlockEndRod.FACING, enumdirection);
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockEndRod.FACING);
    }

    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
