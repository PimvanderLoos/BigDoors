package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;

public class BlockRedstoneLamp extends Block {

    public static final BlockStateBoolean LIT = BlockRedstoneTorch.LIT;

    public BlockRedstoneLamp(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) this.defaultBlockState().setValue(BlockRedstoneLamp.LIT, false));
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) this.defaultBlockState().setValue(BlockRedstoneLamp.LIT, blockactioncontext.getLevel().hasNeighborSignal(blockactioncontext.getClickedPos()));
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            boolean flag1 = (Boolean) iblockdata.getValue(BlockRedstoneLamp.LIT);

            if (flag1 != world.hasNeighborSignal(blockposition)) {
                if (flag1) {
                    world.scheduleTick(blockposition, (Block) this, 4);
                } else {
                    world.setBlock(blockposition, (IBlockData) iblockdata.cycle(BlockRedstoneLamp.LIT), 2);
                }
            }

        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if ((Boolean) iblockdata.getValue(BlockRedstoneLamp.LIT) && !worldserver.hasNeighborSignal(blockposition)) {
            worldserver.setBlock(blockposition, (IBlockData) iblockdata.cycle(BlockRedstoneLamp.LIT), 2);
        }

    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockRedstoneLamp.LIT);
    }
}
