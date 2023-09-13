package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.lighting.LightEngineLayer;

public abstract class BlockDirtSnowSpreadable extends BlockDirtSnow {

    protected BlockDirtSnowSpreadable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    private static boolean canBeGrass(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.above();
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

        if (iblockdata1.is(Blocks.SNOW) && (Integer) iblockdata1.getValue(BlockSnow.LAYERS) == 1) {
            return true;
        } else if (iblockdata1.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngineLayer.getLightBlockInto(iworldreader, iblockdata, blockposition, iblockdata1, blockposition1, EnumDirection.UP, iblockdata1.getLightBlock(iworldreader, blockposition1));

            return i < iworldreader.getMaxLightLevel();
        }
    }

    private static boolean canPropagate(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.above();

        return canBeGrass(iblockdata, iworldreader, blockposition) && !iworldreader.getFluidState(blockposition1).is((Tag) TagsFluid.WATER);
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!canBeGrass(iblockdata, worldserver, blockposition)) {
            worldserver.setBlockAndUpdate(blockposition, Blocks.DIRT.defaultBlockState());
        } else {
            if (worldserver.getMaxLocalRawBrightness(blockposition.above()) >= 9) {
                IBlockData iblockdata1 = this.defaultBlockState();

                for (int i = 0; i < 4; ++i) {
                    BlockPosition blockposition1 = blockposition.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                    if (worldserver.getBlockState(blockposition1).is(Blocks.DIRT) && canPropagate(iblockdata1, worldserver, blockposition1)) {
                        worldserver.setBlockAndUpdate(blockposition1, (IBlockData) iblockdata1.setValue(BlockDirtSnowSpreadable.SNOWY, worldserver.getBlockState(blockposition1.above()).is(Blocks.SNOW)));
                    }
                }
            }

        }
    }
}
