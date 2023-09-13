package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.item.ItemWearable;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathMode;

public abstract class BlockSkullAbstract extends BlockTileEntity implements ItemWearable {

    private final BlockSkull.a a;

    public BlockSkullAbstract(BlockSkull.a blockskull_a, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.a = blockskull_a;
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntitySkull();
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
