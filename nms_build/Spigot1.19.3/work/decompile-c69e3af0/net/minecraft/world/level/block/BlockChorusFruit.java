package net.minecraft.world.level.block;

import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.pathfinder.PathMode;

public class BlockChorusFruit extends BlockSprawling {

    protected BlockChorusFruit(BlockBase.Info blockbase_info) {
        super(0.3125F, blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockChorusFruit.NORTH, false)).setValue(BlockChorusFruit.EAST, false)).setValue(BlockChorusFruit.SOUTH, false)).setValue(BlockChorusFruit.WEST, false)).setValue(BlockChorusFruit.UP, false)).setValue(BlockChorusFruit.DOWN, false));
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return this.getStateForPlacement(blockactioncontext.getLevel(), blockactioncontext.getClickedPos());
    }

    public IBlockData getStateForPlacement(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getBlockState(blockposition.below());
        IBlockData iblockdata1 = iblockaccess.getBlockState(blockposition.above());
        IBlockData iblockdata2 = iblockaccess.getBlockState(blockposition.north());
        IBlockData iblockdata3 = iblockaccess.getBlockState(blockposition.east());
        IBlockData iblockdata4 = iblockaccess.getBlockState(blockposition.south());
        IBlockData iblockdata5 = iblockaccess.getBlockState(blockposition.west());

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockChorusFruit.DOWN, iblockdata.is((Block) this) || iblockdata.is(Blocks.CHORUS_FLOWER) || iblockdata.is(Blocks.END_STONE))).setValue(BlockChorusFruit.UP, iblockdata1.is((Block) this) || iblockdata1.is(Blocks.CHORUS_FLOWER))).setValue(BlockChorusFruit.NORTH, iblockdata2.is((Block) this) || iblockdata2.is(Blocks.CHORUS_FLOWER))).setValue(BlockChorusFruit.EAST, iblockdata3.is((Block) this) || iblockdata3.is(Blocks.CHORUS_FLOWER))).setValue(BlockChorusFruit.SOUTH, iblockdata4.is((Block) this) || iblockdata4.is(Blocks.CHORUS_FLOWER))).setValue(BlockChorusFruit.WEST, iblockdata5.is((Block) this) || iblockdata5.is(Blocks.CHORUS_FLOWER));
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canSurvive(generatoraccess, blockposition)) {
            generatoraccess.scheduleTick(blockposition, (Block) this, 1);
            return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        } else {
            boolean flag = iblockdata1.is((Block) this) || iblockdata1.is(Blocks.CHORUS_FLOWER) || enumdirection == EnumDirection.DOWN && iblockdata1.is(Blocks.END_STONE);

            return (IBlockData) iblockdata.setValue((IBlockState) BlockChorusFruit.PROPERTY_BY_DIRECTION.get(enumdirection), flag);
        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (!iblockdata.canSurvive(worldserver, blockposition)) {
            worldserver.destroyBlock(blockposition, true);
        }

    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition.below());
        boolean flag = !iworldreader.getBlockState(blockposition.above()).isAir() && !iblockdata1.isAir();
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        IBlockData iblockdata2;

        do {
            BlockPosition blockposition1;
            IBlockData iblockdata3;

            do {
                if (!iterator.hasNext()) {
                    return iblockdata1.is((Block) this) || iblockdata1.is(Blocks.END_STONE);
                }

                EnumDirection enumdirection = (EnumDirection) iterator.next();

                blockposition1 = blockposition.relative(enumdirection);
                iblockdata3 = iworldreader.getBlockState(blockposition1);
            } while (!iblockdata3.is((Block) this));

            if (flag) {
                return false;
            }

            iblockdata2 = iworldreader.getBlockState(blockposition1.below());
        } while (!iblockdata2.is((Block) this) && !iblockdata2.is(Blocks.END_STONE));

        return true;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockChorusFruit.NORTH, BlockChorusFruit.EAST, BlockChorusFruit.SOUTH, BlockChorusFruit.WEST, BlockChorusFruit.UP, BlockChorusFruit.DOWN);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
