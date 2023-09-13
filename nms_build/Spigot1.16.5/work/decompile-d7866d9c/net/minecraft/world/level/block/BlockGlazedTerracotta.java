package net.minecraft.world.level.block;

import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.EnumPistonReaction;

public class BlockGlazedTerracotta extends BlockFacingHorizontal {

    public BlockGlazedTerracotta(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockGlazedTerracotta.FACING);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockGlazedTerracotta.FACING, blockactioncontext.f().opposite());
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.PUSH_ONLY;
    }
}
