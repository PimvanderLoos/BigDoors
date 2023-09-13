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
        this.j((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockChorusFruit.a, false)).set(BlockChorusFruit.b, false)).set(BlockChorusFruit.c, false)).set(BlockChorusFruit.d, false)).set(BlockChorusFruit.e, false)).set(BlockChorusFruit.f, false));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return this.a((IBlockAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition());
    }

    public IBlockData a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = iblockaccess.getType(blockposition.down()).getBlock();
        Block block1 = iblockaccess.getType(blockposition.up()).getBlock();
        Block block2 = iblockaccess.getType(blockposition.north()).getBlock();
        Block block3 = iblockaccess.getType(blockposition.east()).getBlock();
        Block block4 = iblockaccess.getType(blockposition.south()).getBlock();
        Block block5 = iblockaccess.getType(blockposition.west()).getBlock();

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockChorusFruit.f, block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE)).set(BlockChorusFruit.e, block1 == this || block1 == Blocks.CHORUS_FLOWER)).set(BlockChorusFruit.a, block2 == this || block2 == Blocks.CHORUS_FLOWER)).set(BlockChorusFruit.b, block3 == this || block3 == Blocks.CHORUS_FLOWER)).set(BlockChorusFruit.c, block4 == this || block4 == Blocks.CHORUS_FLOWER)).set(BlockChorusFruit.d, block5 == this || block5 == Blocks.CHORUS_FLOWER);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        } else {
            boolean flag = iblockdata1.getBlock() == this || iblockdata1.a(Blocks.CHORUS_FLOWER) || enumdirection == EnumDirection.DOWN && iblockdata1.a(Blocks.END_STONE);

            return (IBlockData) iblockdata.set((IBlockState) BlockChorusFruit.g.get(enumdirection), flag);
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

        Block block;

        do {
            BlockPosition blockposition1;
            Block block1;

            do {
                if (!iterator.hasNext()) {
                    Block block2 = iblockdata1.getBlock();

                    return block2 == this || block2 == Blocks.END_STONE;
                }

                EnumDirection enumdirection = (EnumDirection) iterator.next();

                blockposition1 = blockposition.shift(enumdirection);
                block1 = iworldreader.getType(blockposition1).getBlock();
            } while (block1 != this);

            if (flag) {
                return false;
            }

            block = iworldreader.getType(blockposition1.down()).getBlock();
        } while (block != this && block != Blocks.END_STONE);

        return true;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockChorusFruit.a, BlockChorusFruit.b, BlockChorusFruit.c, BlockChorusFruit.d, BlockChorusFruit.e, BlockChorusFruit.f);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
