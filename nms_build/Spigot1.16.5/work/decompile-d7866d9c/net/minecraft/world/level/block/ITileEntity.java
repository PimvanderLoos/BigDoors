package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.entity.TileEntity;

public interface ITileEntity {

    @Nullable
    TileEntity createTile(IBlockAccess iblockaccess);
}
