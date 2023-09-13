package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockSoulFire extends BlockFireAbstract {

    public BlockSoulFire(BlockBase.Info blockbase_info) {
        super(blockbase_info, 2.0F);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return this.canPlace(iblockdata, generatoraccess, blockposition) ? this.getBlockData() : Blocks.AIR.getBlockData();
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return h(iworldreader.getType(blockposition.down()));
    }

    public static boolean h(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.SOUL_FIRE_BASE_BLOCKS);
    }

    @Override
    protected boolean f(IBlockData iblockdata) {
        return true;
    }
}
