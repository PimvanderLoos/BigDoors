package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;

public interface IBlockFragilePlantElement {

    boolean isValidBonemealTarget(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag);

    boolean isBonemealSuccess(World world, Random random, BlockPosition blockposition, IBlockData iblockdata);

    void performBonemeal(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata);
}
