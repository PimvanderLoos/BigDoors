package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.lighting.LightEngineLayer;

public abstract class BlockDirtSnowSpreadable extends BlockDirtSnow {

    protected BlockDirtSnowSpreadable(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    private static boolean b(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);

        if (iblockdata1.a(Blocks.SNOW) && (Integer) iblockdata1.get(BlockSnow.LAYERS) == 1) {
            return true;
        } else if (iblockdata1.getFluid().e() == 8) {
            return false;
        } else {
            int i = LightEngineLayer.a(iworldreader, iblockdata, blockposition, iblockdata1, blockposition1, EnumDirection.UP, iblockdata1.b((IBlockAccess) iworldreader, blockposition1));

            return i < iworldreader.K();
        }
    }

    private static boolean c(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();

        return b(iblockdata, iworldreader, blockposition) && !iworldreader.getFluid(blockposition1).a((Tag) TagsFluid.WATER);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!b(iblockdata, (IWorldReader) worldserver, blockposition)) {
            worldserver.setTypeUpdate(blockposition, Blocks.DIRT.getBlockData());
        } else {
            if (worldserver.getLightLevel(blockposition.up()) >= 9) {
                IBlockData iblockdata1 = this.getBlockData();

                for (int i = 0; i < 4; ++i) {
                    BlockPosition blockposition1 = blockposition.b(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                    if (worldserver.getType(blockposition1).a(Blocks.DIRT) && c(iblockdata1, (IWorldReader) worldserver, blockposition1)) {
                        worldserver.setTypeUpdate(blockposition1, (IBlockData) iblockdata1.set(BlockDirtSnowSpreadable.a, worldserver.getType(blockposition1.up()).a(Blocks.SNOW)));
                    }
                }
            }

        }
    }
}
