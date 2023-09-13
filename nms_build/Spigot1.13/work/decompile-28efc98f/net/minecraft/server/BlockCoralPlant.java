package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockCoralPlant extends Block {

    protected static final VoxelShape a = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    protected BlockCoralPlant(Block.Info block_info) {
        super(block_info);
    }

    protected boolean X_() {
        return true;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCoralPlant.a;
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().b(blockactioncontext.getClickPosition());

        return fluid.a(TagsFluid.a) && fluid.g() == 8 ? super.getPlacedState(blockactioncontext) : null;
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();

        return Block.a(iworldreader.getType(blockposition1).h(iworldreader, blockposition1), EnumDirection.UP);
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public Fluid h(IBlockData iblockdata) {
        return FluidTypes.c.a(false);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        generatoraccess.H().a(blockposition, FluidTypes.c, FluidTypes.c.a((IWorldReader) generatoraccess));
        return enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
