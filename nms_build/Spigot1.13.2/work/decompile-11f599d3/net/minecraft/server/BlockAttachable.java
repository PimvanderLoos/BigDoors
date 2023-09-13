package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockAttachable extends BlockFacingHorizontal {

    public static final BlockStateEnum<BlockPropertyAttachPosition> FACE = BlockProperties.K;

    protected BlockAttachable(Block.Info block_info) {
        super(block_info);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = k(iblockdata).opposite();
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);
        Block block = iblockdata1.getBlock();

        if (a(block)) {
            return false;
        } else {
            boolean flag = iblockdata1.c(iworldreader, blockposition1, enumdirection.opposite()) == EnumBlockFaceShape.SOLID;

            return enumdirection == EnumDirection.UP ? block == Blocks.HOPPER || flag : !b(block) && flag;
        }
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection[] aenumdirection = blockactioncontext.e();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];
            IBlockData iblockdata;

            if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
                iblockdata = (IBlockData) ((IBlockData) this.getBlockData().set(BlockAttachable.FACE, enumdirection == EnumDirection.UP ? BlockPropertyAttachPosition.CEILING : BlockPropertyAttachPosition.FLOOR)).set(BlockAttachable.FACING, blockactioncontext.f());
            } else {
                iblockdata = (IBlockData) ((IBlockData) this.getBlockData().set(BlockAttachable.FACE, BlockPropertyAttachPosition.WALL)).set(BlockAttachable.FACING, enumdirection.opposite());
            }

            if (iblockdata.canPlace(blockactioncontext.getWorld(), blockactioncontext.getClickPosition())) {
                return iblockdata;
            }
        }

        return null;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return k(iblockdata).opposite() == enumdirection && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    protected static EnumDirection k(IBlockData iblockdata) {
        switch ((BlockPropertyAttachPosition) iblockdata.get(BlockAttachable.FACE)) {
        case CEILING:
            return EnumDirection.DOWN;
        case FLOOR:
            return EnumDirection.UP;
        default:
            return (EnumDirection) iblockdata.get(BlockAttachable.FACING);
        }
    }
}
