package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockNetherrack extends Block implements IBlockFragilePlantElement {

    public BlockNetherrack(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        if (!iblockaccess.getType(blockposition.up()).a(iblockaccess, blockposition)) {
            return false;
        } else {
            Iterator iterator = BlockPosition.a(blockposition.b(-1, -1, -1), blockposition.b(1, 1, 1)).iterator();

            BlockPosition blockposition1;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                blockposition1 = (BlockPosition) iterator.next();
            } while (!iblockaccess.getType(blockposition1).a((Tag) TagsBlock.NYLIUM));

            return true;
        }
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = false;
        boolean flag1 = false;
        Iterator iterator = BlockPosition.a(blockposition.b(-1, -1, -1), blockposition.b(1, 1, 1)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            IBlockData iblockdata1 = worldserver.getType(blockposition1);

            if (iblockdata1.a(Blocks.WARPED_NYLIUM)) {
                flag1 = true;
            }

            if (iblockdata1.a(Blocks.CRIMSON_NYLIUM)) {
                flag = true;
            }

            if (flag1 && flag) {
                break;
            }
        }

        if (flag1 && flag) {
            worldserver.setTypeAndData(blockposition, random.nextBoolean() ? Blocks.WARPED_NYLIUM.getBlockData() : Blocks.CRIMSON_NYLIUM.getBlockData(), 3);
        } else if (flag1) {
            worldserver.setTypeAndData(blockposition, Blocks.WARPED_NYLIUM.getBlockData(), 3);
        } else if (flag) {
            worldserver.setTypeAndData(blockposition, Blocks.CRIMSON_NYLIUM.getBlockData(), 3);
        }

    }
}
