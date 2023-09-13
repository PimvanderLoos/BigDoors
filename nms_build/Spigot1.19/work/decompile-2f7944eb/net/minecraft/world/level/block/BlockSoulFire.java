package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
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
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return this.canSurvive(iblockdata, generatoraccess, blockposition) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return canSurviveOnBlock(iworldreader.getBlockState(blockposition.below()));
    }

    public static boolean canSurviveOnBlock(IBlockData iblockdata) {
        return iblockdata.is(TagsBlock.SOUL_FIRE_BASE_BLOCKS);
    }

    @Override
    protected boolean canBurn(IBlockData iblockdata) {
        return true;
    }
}
