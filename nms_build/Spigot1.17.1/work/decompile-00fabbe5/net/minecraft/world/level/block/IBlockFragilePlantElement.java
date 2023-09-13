package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;

public interface IBlockFragilePlantElement {

    boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag);

    boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata);

    void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata);
}
