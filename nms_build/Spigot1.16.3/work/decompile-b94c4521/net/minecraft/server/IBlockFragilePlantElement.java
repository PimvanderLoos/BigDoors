package net.minecraft.server;

import java.util.Random;

public interface IBlockFragilePlantElement {

    boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag);

    boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata);

    void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata);
}
