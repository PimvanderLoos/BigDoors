package net.minecraft.world.level.block;

import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;

public class InfestedRotatedPillarBlock extends BlockMonsterEggs {

    public InfestedRotatedPillarBlock(Block block, BlockBase.Info blockbase_info) {
        super(block, blockbase_info);
        this.k((IBlockData) this.getBlockData().set(BlockRotatable.AXIS, EnumDirection.EnumAxis.Y));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return BlockRotatable.b(iblockdata, enumblockrotation);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRotatable.AXIS);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockRotatable.AXIS, blockactioncontext.getClickedFace().n());
    }
}
