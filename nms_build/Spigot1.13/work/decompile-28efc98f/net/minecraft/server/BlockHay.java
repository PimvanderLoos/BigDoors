package net.minecraft.server;

public class BlockHay extends BlockRotatable {

    public BlockHay(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockHay.AXIS, EnumDirection.EnumAxis.Y));
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        entity.c(f, 0.2F);
    }
}
