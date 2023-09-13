package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
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
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockChorusFruit.NORTH, false)).set(BlockChorusFruit.EAST, false)).set(BlockChorusFruit.SOUTH, false)).set(BlockChorusFruit.WEST, false)).set(BlockChorusFruit.UP, false)).set(BlockChorusFruit.DOWN, false));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return this.a((IBlockAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition());
    }

    public IBlockData a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition.down());
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.up());
        IBlockData iblockdata2 = iblockaccess.getType(blockposition.north());
        IBlockData iblockdata3 = iblockaccess.getType(blockposition.east());
        IBlockData iblockdata4 = iblockaccess.getType(blockposition.south());
        IBlockData iblockdata5 = iblockaccess.getType(blockposition.west());

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockChorusFruit.DOWN, iblockdata.a((Block) this) || iblockdata.a(Blocks.CHORUS_FLOWER) || iblockdata.a(Blocks.END_STONE))).set(BlockChorusFruit.UP, iblockdata1.a((Block) this) || iblockdata1.a(Blocks.CHORUS_FLOWER))).set(BlockChorusFruit.NORTH, iblockdata2.a((Block) this) || iblockdata2.a(Blocks.CHORUS_FLOWER))).set(BlockChorusFruit.EAST, iblockdata3.a((Block) this) || iblockdata3.a(Blocks.CHORUS_FLOWER))).set(BlockChorusFruit.SOUTH, iblockdata4.a((Block) this) || iblockdata4.a(Blocks.CHORUS_FLOWER))).set(BlockChorusFruit.WEST, iblockdata5.a((Block) this) || iblockdata5.a(Blocks.CHORUS_FLOWER));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        } else {
            boolean flag = iblockdata1.a((Block) this) || iblockdata1.a(Blocks.CHORUS_FLOWER) || enumdirection == EnumDirection.DOWN && iblockdata1.a(Blocks.END_STONE);

            return (IBlockData) iblockdata.set((IBlockState) BlockChorusFruit.PROPERTY_BY_DIRECTION.get(enumdirection), flag);
        }
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!iblockdata.canPlace(worldserver, blockposition)) {
            worldserver.b(blockposition, true);
        }

    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.down());
        boolean flag = !iworldreader.getType(blockposition.up()).isAir() && !iblockdata1.isAir();
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        IBlockData iblockdata2;

        do {
            BlockPosition blockposition1;
            IBlockData iblockdata3;

            do {
                if (!iterator.hasNext()) {
                    return iblockdata1.a((Block) this) || iblockdata1.a(Blocks.END_STONE);
                }

                EnumDirection enumdirection = (EnumDirection) iterator.next();

                blockposition1 = blockposition.shift(enumdirection);
                iblockdata3 = iworldreader.getType(blockposition1);
            } while (!iblockdata3.a((Block) this));

            if (flag) {
                return false;
            }

            iblockdata2 = iworldreader.getType(blockposition1.down());
        } while (!iblockdata2.a((Block) this) && !iblockdata2.a(Blocks.END_STONE));

        return true;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockChorusFruit.NORTH, BlockChorusFruit.EAST, BlockChorusFruit.SOUTH, BlockChorusFruit.WEST, BlockChorusFruit.UP, BlockChorusFruit.DOWN);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
