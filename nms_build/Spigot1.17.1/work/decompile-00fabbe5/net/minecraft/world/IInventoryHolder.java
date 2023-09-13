package net.minecraft.world;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;

public interface IInventoryHolder {

    IWorldInventory a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition);
}
